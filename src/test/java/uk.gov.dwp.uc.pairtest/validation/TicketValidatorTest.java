package uk.gov.dwp.uc.pairtest.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketValidatorTest {

  private static TicketValidator ticketValidator;

  @Before
  public void setUp() {
    ticketValidator = new TicketValidator();
  }

  @Test
  public void number_of_tickets_above_maximum_allowed_should_fail_validation() {

    var request = new HashMap<Type, Integer>();
    request.put(Type.ADULT, 10);
    request.put(Type.CHILD, 10);
    request.put(Type.INFANT, 1);

    var thrown =
        assertThrows(
            InvalidPurchaseException.class, () -> ticketValidator.validateTickets(request));

    assertEquals(
        "Total tickets ordered exceeds maximum allowed (20). Actual:Adult: [10], Child [10], Infant [1]",
        thrown.getMessage());
  }

  @Test
  public void number_of_tickets_equal_to_maximum_allowed_should_validate() {

    var request = new HashMap<Type, Integer>();
    request.put(Type.ADULT, 10);
    request.put(Type.CHILD, 5);
    request.put(Type.INFANT, 5);

    ticketValidator.validateTickets(request);
  }
}
