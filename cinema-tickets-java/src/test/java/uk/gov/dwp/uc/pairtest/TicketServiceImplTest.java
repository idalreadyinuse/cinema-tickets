package uk.gov.dwp.uc.pairtest;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

  @Mock
  private TicketPaymentService ticketPaymentService;
  @Mock
  private SeatReservationService seatReservationService;
  @InjectMocks
  private TicketServiceImpl ticketService;

  private static final long ACCOUNT_ID = 123456L;

  @Test
  public void accountId_not_present_should_throw_exception() {

    TicketTypeRequest ticketRequest = new TicketTypeRequest(Type.ADULT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(null, ticketRequest));

    assertTrue(thrown.getMessage().contains("Account ID must be present and greater than zero"));
  }

  @Test
  public void accountId_is_zero_should_throw_exception() {

    TicketTypeRequest ticketRequest = new TicketTypeRequest(Type.ADULT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(0L, ticketRequest));

    assertTrue(thrown.getMessage().contains("Account ID must be present and greater than zero"));
  }

  @Test
  public void accountId_less_than_zero_should_throw_exception() {

    TicketTypeRequest ticketRequest = new TicketTypeRequest(Type.ADULT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(-1L, ticketRequest));

    assertTrue(thrown.getMessage().contains("Account ID must be present and greater than zero"));
  }

  @Test
  public void total_tickets_ordered_over_20_should_throw_exception() {

    TicketTypeRequest adultTicketRequest = new TicketTypeRequest(Type.ADULT, 19);
    TicketTypeRequest childTicketRequest = new TicketTypeRequest(Type.CHILD, 2);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(ACCOUNT_ID, adultTicketRequest, childTicketRequest));

    assertTrue(thrown.getMessage().contains("Total tickets ordered exceeds maximum allowed: 20"));
  }

  @Test
  public void child_tickets_ordered_no_adult_tickets_should_throw_exception() {

    TicketTypeRequest childTicketRequest = new TicketTypeRequest(Type.CHILD, 2);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(ACCOUNT_ID, childTicketRequest));

    assertTrue(thrown.getMessage()
        .contains("Cannot purchase child or infant tickets without also purchasing an adult ticket"));
  }

  @Test
  public void infant_tickets_ordered_no_adult_tickets_should_throw_exception() {

    TicketTypeRequest infantTicketRequest = new TicketTypeRequest(Type.INFANT, 2);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(ACCOUNT_ID, infantTicketRequest));

    assertTrue(thrown.getMessage()
        .contains("Cannot purchase child or infant tickets without also purchasing an adult ticket"));
  }

  @Test
  public void child_and_infant_tickets_ordered_no_adult_tickets_should_throw_exception() {

    TicketTypeRequest childTicketRequest = new TicketTypeRequest(Type.CHILD, 2);
    TicketTypeRequest infantTicketRequest = new TicketTypeRequest(Type.INFANT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> ticketService.purchaseTickets(ACCOUNT_ID, childTicketRequest, infantTicketRequest));

    assertTrue(thrown.getMessage()
        .contains("Cannot purchase child or infant tickets without also purchasing an adult ticket"));
  }

  @Test
  public void one_adult_ticket_should_request_one_seat_with_cost_20() {

    TicketTypeRequest adultTicketRequest = new TicketTypeRequest(Type.ADULT, 1);

    ticketService.purchaseTickets(ACCOUNT_ID, adultTicketRequest);

    verify(ticketPaymentService, times(1)).makePayment(ACCOUNT_ID, 20);
    verify(seatReservationService, times(1)).reserveSeat(ACCOUNT_ID, 1);
  }

  @Test
  public void one_adult_and_one_child_ticket_should_request_two_seats_with_cost_30() {

    TicketTypeRequest adultTicketRequest = new TicketTypeRequest(Type.ADULT, 1);
    TicketTypeRequest childTicketRequest = new TicketTypeRequest(Type.CHILD, 1);

    ticketService.purchaseTickets(ACCOUNT_ID, adultTicketRequest, childTicketRequest);

    verify(ticketPaymentService, times(1)).makePayment(ACCOUNT_ID, 30);
    verify(seatReservationService, times(1)).reserveSeat(ACCOUNT_ID, 2);
  }

  @Test
  public void one_adult_one_child_and_one_infant_ticket_should_request_two_seats_with_cost_30() {

    TicketTypeRequest adultTicketRequest = new TicketTypeRequest(Type.ADULT, 1);
    TicketTypeRequest childTicketRequest = new TicketTypeRequest(Type.CHILD, 1);
    TicketTypeRequest infantTicketRequest = new TicketTypeRequest(Type.INFANT, 1);

    ticketService.purchaseTickets(ACCOUNT_ID, adultTicketRequest, childTicketRequest, infantTicketRequest);

    verify(ticketPaymentService, times(1)).makePayment(ACCOUNT_ID, 30);
    verify(seatReservationService, times(1)).reserveSeat(ACCOUNT_ID, 2);
  }

  @Test
  public void multiple_ticket_types_should_request_correct_seats_and_cost() {

    TicketTypeRequest adultTicketRequest = new TicketTypeRequest(Type.ADULT, 2);
    TicketTypeRequest childTicketRequest = new TicketTypeRequest(Type.CHILD, 2);
    TicketTypeRequest infantTicketRequest = new TicketTypeRequest(Type.INFANT, 1);

    ticketService.purchaseTickets(ACCOUNT_ID, adultTicketRequest, childTicketRequest, infantTicketRequest );

    verify(ticketPaymentService, times(1)).makePayment(ACCOUNT_ID, 60);
    verify(seatReservationService, times(1)).reserveSeat(ACCOUNT_ID, 4);
  }
}
