package uk.gov.dwp.uc.pairtest;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.service.SeatReservationComputeService;
import uk.gov.dwp.uc.pairtest.service.TicketCostComputeService;
import uk.gov.dwp.uc.pairtest.validation.AccountIdValidator;
import uk.gov.dwp.uc.pairtest.validation.TicketValidator;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

  @Mock
  private AccountIdValidator accountIdValidator;

  @Mock
  private TicketValidator ticketValidator;

  @Mock
  private TicketCostComputeService ticketCostComputeService;

  @Mock
  private SeatReservationComputeService seatReservationComputeService;

  @Mock
  private TicketPaymentService ticketPaymentService;

  @Mock
  private SeatReservationService seatReservationService;

  @InjectMocks
  private TicketServiceImpl ticketService;

  private static final long ACCOUNT_ID = 123456L;

  @Test
  public void fails_accountId_validation_should_throw_exception() {

    var ticketRequest = new TicketTypeRequest(Type.ADULT, 1);

    doThrow(InvalidPurchaseException.class).when(accountIdValidator).validate(0L);

    assertThrows(
        InvalidPurchaseException.class, () -> ticketService.purchaseTickets(0L, ticketRequest));

    verifyNoInteractions(ticketValidator);
    verifyNoInteractions(seatReservationComputeService);
    verifyNoInteractions(ticketCostComputeService);
    verifyNoInteractions(seatReservationService);
    verifyNoInteractions(ticketPaymentService);
  }

  @Test
  public void fails_ticket_validation_should_throw_exception() {

    var ticketRequest = new TicketTypeRequest(Type.ADULT, 1);

    doNothing().when(accountIdValidator).validate(any());
    doThrow(InvalidPurchaseException.class).when(ticketValidator).validateTickets(anyMap());

    assertThrows(
        InvalidPurchaseException.class, () -> ticketService.purchaseTickets(ACCOUNT_ID, ticketRequest));

    verifyNoInteractions(seatReservationComputeService);
    verifyNoInteractions(ticketCostComputeService);
    verifyNoInteractions(seatReservationService);
    verifyNoInteractions(ticketPaymentService);
  }

  @Test
  public void fails_seats_to_reserve_validation_should_throw_exception() {

    var adultTicketRequest = new TicketTypeRequest(Type.ADULT, 1);
    var infantTicketRequest = new TicketTypeRequest(Type.INFANT, 2);

    doNothing().when(accountIdValidator).validate(any());
    doNothing().when(ticketValidator).validateTickets(anyMap());
    doThrow(InvalidPurchaseException.class).when(seatReservationComputeService).seatsToReserve(anyMap());

    assertThrows(
        InvalidPurchaseException.class,
        () -> ticketService.purchaseTickets(ACCOUNT_ID, adultTicketRequest, infantTicketRequest));

    verifyNoInteractions(ticketCostComputeService);
    verifyNoInteractions(seatReservationService);
    verifyNoInteractions(ticketPaymentService);
  }

  @Test
  public void should_invoke_3rd_party_services_on_valid_request() {

    var adultTicketRequest = new TicketTypeRequest(Type.ADULT, 2);
    var childTicketRequest = new TicketTypeRequest(Type.CHILD, 1);
    var infantTicketRequest = new TicketTypeRequest(Type.INFANT, 1);

    doNothing().when(accountIdValidator).validate(any());
    doNothing().when(ticketValidator).validateTickets(anyMap());
    when(seatReservationComputeService.seatsToReserve(anyMap())).thenReturn(3);
    when(ticketCostComputeService.totalOrderValue(anyMap())).thenReturn(30);

    ticketService.purchaseTickets(
        ACCOUNT_ID, adultTicketRequest, childTicketRequest, infantTicketRequest);

    verify(seatReservationService, times(1)).reserveSeat(ACCOUNT_ID, 3);
    verify(ticketPaymentService, times(1)).makePayment(ACCOUNT_ID, 30);
  }
}
