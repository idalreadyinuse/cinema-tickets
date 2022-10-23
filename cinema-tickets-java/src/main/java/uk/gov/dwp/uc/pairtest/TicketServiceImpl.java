package uk.gov.dwp.uc.pairtest;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.TicketServiceValidator;

public class TicketServiceImpl implements TicketService {
  /** Should only have private methods other than the one below. */
  private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());

  private static final int ADULT_TICKET_COST = 20;
  private static final int CHILD_TICKET_COST = 10;

  private final TicketPaymentService ticketPaymentService;
  private final SeatReservationService seatReservationService;

  public TicketServiceImpl(
      TicketPaymentService ticketPaymentService,
      SeatReservationService seatReservationService) {
    this.ticketPaymentService = ticketPaymentService;
    this.seatReservationService = seatReservationService;
  }

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {

    List<TicketTypeRequest> ticketTypeRequestList = List.of(ticketTypeRequests);
    Map<Type, Integer> ticketRequestMap =
        ticketTypeRequestList.stream()
            .collect(
                Collectors.toMap(
                    TicketTypeRequest::getTicketType, TicketTypeRequest::getNoOfTickets));

    TicketServiceValidator.applyValidation(accountId, ticketRequestMap);

    int seatsToReserve = TicketServiceValidator.validateSeating(accountId, ticketRequestMap);

    int cost = totalOrderValue(ticketRequestMap);

    // send for payment
    LOGGER.info(String.format("Requesting payment for accountID %s for £%s.", accountId, cost));
    ticketPaymentService.makePayment(accountId, cost);

    // send for reservation
    LOGGER.info(String.format("Reserving %s seats for accountID %s.", seatsToReserve, accountId));
    seatReservationService.reserveSeat(accountId, seatsToReserve);
  }

  private int totalOrderValue(Map<Type, Integer> ticketRequestMap) {
    int totalCost = 0;

    for (Map.Entry<Type, Integer> tickets : ticketRequestMap.entrySet()) {
      if (tickets.getKey() == Type.ADULT) {
        totalCost = totalCost + ADULT_TICKET_COST * tickets.getValue();
      }
      if (tickets.getKey() == Type.CHILD) {
        totalCost = totalCost + CHILD_TICKET_COST * tickets.getValue();
      }
    }

    return totalCost;
  }
}
