package uk.gov.dwp.uc.pairtest;

import static uk.gov.dwp.uc.pairtest.mapper.TicketRequestMapper.mapRequest;

import java.util.List;
import java.util.logging.Logger;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.service.SeatReservationComputeService;
import uk.gov.dwp.uc.pairtest.service.TicketCostComputeService;
import uk.gov.dwp.uc.pairtest.validation.AccountIdValidator;
import uk.gov.dwp.uc.pairtest.validation.TicketValidator;

public class TicketServiceImpl implements TicketService {

  private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());

  private final AccountIdValidator accountIdValidator;
  private final TicketValidator ticketValidator;
  private final SeatReservationComputeService seatReservationComputeService;
  private final TicketCostComputeService ticketCostComputeService;
  private final TicketPaymentService ticketPaymentService;
  private final SeatReservationService seatReservationService;

  public TicketServiceImpl(
      AccountIdValidator accountIdValidator,
      TicketValidator ticketValidator,
      SeatReservationComputeService seatReservationComputeService,
      TicketCostComputeService ticketCostComputeService,
      TicketPaymentService ticketPaymentService,
      SeatReservationService seatReservationService) {
    this.accountIdValidator = accountIdValidator;
    this.ticketValidator = ticketValidator;
    this.seatReservationComputeService = seatReservationComputeService;
    this.ticketCostComputeService = ticketCostComputeService;
    this.ticketPaymentService = ticketPaymentService;
    this.seatReservationService = seatReservationService;
  }

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {

    accountIdValidator.validate(accountId);

    var ticketMap = mapRequest(List.of(ticketTypeRequests));
    ticketValidator.validateTickets(ticketMap);

    var seatsToReserve = seatReservationComputeService.seatsToReserve(ticketMap);
    var orderCost = ticketCostComputeService.totalOrderValue(ticketMap);

    sendPaymentRequest(accountId, orderCost);
    sendSeatReservation(accountId, seatsToReserve);
  }

  private void sendSeatReservation(Long accountId, int seatsToReserve) {
    LOGGER.info(String.format("Reserving %s seats for accountID %s.", seatsToReserve, accountId));
    seatReservationService.reserveSeat(accountId, seatsToReserve);
  }

  private void sendPaymentRequest(Long accountId, int orderCost) {
    LOGGER.info(
        String.format("Requesting payment for accountID %s for £%s.", accountId, orderCost));
    ticketPaymentService.makePayment(accountId, orderCost);
  }
}
