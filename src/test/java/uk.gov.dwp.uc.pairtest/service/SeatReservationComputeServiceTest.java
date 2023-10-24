package uk.gov.dwp.uc.pairtest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class SeatReservationComputeServiceTest {

  private SeatReservationComputeService service;

  @Before
  public void setUp() {
    service = new SeatReservationComputeService();
  }

  @Test
  public void should_return_the_correct_number_of_seats_adult_only() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 2);

    assertEquals(2, service.seatsToReserve(order));
  }

  @Test
  public void should_return_the_correct_number_of_seats_adult_and_child() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 2);
    order.put(Type.CHILD, 3);

    assertEquals(5, service.seatsToReserve(order));
  }

  @Test
  public void should_return_the_correct_number_of_seats_adult_child_and_infant() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 2);
    order.put(Type.CHILD, 2);
    order.put(Type.INFANT, 1);

    assertEquals(4, service.seatsToReserve(order));
  }

  @Test
  public void child_ticket_no_adult_should_throw_exception() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.CHILD, 2);

    var thrown = assertThrows(InvalidPurchaseException.class,
        () -> service.seatsToReserve(order));

    assertEquals(
        "Cannot purchase child or infant tickets without also purchasing an adult ticket",
        thrown.getMessage());
  }

  @Test
  public void infant_ticket_no_adult_should_throw_exception() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.INFANT, 2);

    var thrown = assertThrows(InvalidPurchaseException.class,
        () -> service.seatsToReserve(order));

    assertEquals(
        "Cannot purchase child or infant tickets without also purchasing an adult ticket",
        thrown.getMessage());
  }

  @Test
  public void child_and_infant_tickets_no_adult_should_throw_exception() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.CHILD, 2);
    order.put(Type.INFANT, 1);

    var thrown = assertThrows(InvalidPurchaseException.class,
        () -> service.seatsToReserve(order));

    assertEquals(
        "Cannot purchase child or infant tickets without also purchasing an adult ticket",
        thrown.getMessage());
  }

  @Test
  public void more_infant_than_adult_tickets_should_throw_exception() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 2);
    order.put(Type.INFANT, 3);

    var thrown = assertThrows(InvalidPurchaseException.class,
        () -> service.seatsToReserve(order));

    assertEquals("Cannot have more infant tickets than adult tickets", thrown.getMessage());
  }

  @Test
  public void same_infant_and_adult_tickets_should_not_throw_exception() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 2);
    order.put(Type.INFANT, 2);

    assertEquals(2, service.seatsToReserve(order));
  }
}
