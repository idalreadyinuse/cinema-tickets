package uk.gov.dwp.uc.pairtest.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static uk.gov.dwp.uc.pairtest.validation.TicketServiceValidator.applyValidation;
import static uk.gov.dwp.uc.pairtest.validation.TicketServiceValidator.validateSeating;

import java.util.Map;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceValidatorTest {

  @Test
  public void accountId_is_zero_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.ADULT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> applyValidation(0L, ticketRequest));

    assertTrue(thrown.getMessage().contains("Account ID must be present and greater than zero"));
  }

  @Test
  public void accountId_less_than_zero_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.ADULT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> applyValidation(-1L, ticketRequest));

    assertTrue(thrown.getMessage().contains("Account ID must be present and greater than zero"));
  }

  @Test
  public void accountId_not_present_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.ADULT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> applyValidation(null, ticketRequest));

    assertTrue(thrown.getMessage().contains("Account ID must be present and greater than zero"));
  }

  @Test
  public void total_tickets_ordered_over_20_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.ADULT, 19, Type.CHILD, 2);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> applyValidation(1234L, ticketRequest));

    assertTrue(thrown.getMessage().contains("Total tickets ordered exceeds maximum allowed: 20"));
  }

  @Test
  public void child_tickets_ordered_no_adult_tickets_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.CHILD, 2);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> validateSeating(ticketRequest));

    assertTrue(thrown.getMessage()
        .contains("Cannot purchase child or infant tickets without also purchasing an adult ticket"));
  }

  @Test
  public void infant_tickets_ordered_no_adult_tickets_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.INFANT, 2);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> validateSeating(ticketRequest));

    assertTrue(thrown.getMessage()
        .contains("Cannot purchase child or infant tickets without also purchasing an adult ticket"));
  }

  @Test
  public void child_and_infant_tickets_ordered_no_adult_tickets_should_throw_exception() {

    Map<Type, Integer> ticketRequest = Map.of(Type.CHILD, 2, Type.INFANT, 1);

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> validateSeating(ticketRequest));

    assertTrue(thrown.getMessage()
        .contains("Cannot purchase child or infant tickets without also purchasing an adult ticket"));
  }

  @Test
  public void valid_request_should_return_correct_number_of_seats_to_reserve() {

    Map<Type, Integer> ticketRequest = Map.of(Type.ADULT, 2, Type.CHILD, 4);

    int seatsReserved = validateSeating(ticketRequest);

    assertEquals(seatsReserved, 6);
  }

  @Test
  public void valid_request_with_infant_tickets_should_return_correct_number_of_seats_to_reserve() {

    Map<Type, Integer> ticketRequest = Map.of(Type.ADULT, 2, Type.CHILD, 2, Type.INFANT, 2);

    int seatsReserved = validateSeating(ticketRequest);

    assertEquals(seatsReserved, 4);
  }
}
