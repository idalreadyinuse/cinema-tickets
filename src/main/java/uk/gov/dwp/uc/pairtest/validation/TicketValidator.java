package uk.gov.dwp.uc.pairtest.validation;

import java.util.Map;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketValidator {

  private static final int MAX_TICKETS = 20;

  public void validateTickets(Map<Type, Integer> ticketOrder) {

    if (totalTicketCheck(ticketOrder) > MAX_TICKETS) {

      var message = String.format("Total tickets ordered exceeds maximum allowed (%d). Actual:"
          + "Adult: [%s], Child [%s], Infant [%s]",
          MAX_TICKETS,
          ticketOrder.get(Type.ADULT) == null ? "n/a" : ticketOrder.get(Type.ADULT),
          ticketOrder.get(Type.CHILD) == null ? "n/a" : ticketOrder.get(Type.CHILD),
          ticketOrder.get(Type.INFANT) == null ? "n/a" : ticketOrder.get(Type.INFANT)
          );

      throw new InvalidPurchaseException(message);
    }
  }

  private static int totalTicketCheck(Map<Type, Integer> ticketRequestMap) {

    return ticketRequestMap.values().stream().mapToInt(tickets -> tickets).sum();
  }
}
