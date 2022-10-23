package uk.gov.dwp.uc.pairtest.validation;

import java.util.Map;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceValidator {

  public static void applyValidation(Long accountId, Map<Type, Integer> ticketRequestMap) {

    if (accountId == null || accountId <= 0) {
      throw new InvalidPurchaseException("Account ID must be present and greater than zero");
    }

    if (totalTicketCheck(ticketRequestMap) > 20) {
      throw new InvalidPurchaseException("Total tickets ordered exceeds maximum allowed: 20");
    }
  }

  public static int validateSeating(Long accountId, Map<Type, Integer> ticketRequestMap) {

    return seatsToReserve(ticketRequestMap);
  }

  private static int totalTicketCheck(Map<Type, Integer> ticketRequestMap) {

    return ticketRequestMap.values().stream().mapToInt(m -> m).sum();
  }

  private static int seatsToReserve(Map<Type, Integer> ticketRequestMap) {
    int allSeats = 0;
    int adultTickets = 0;
    int childTickets = 0;
    int infantTickets = 0;

    for (Map.Entry<Type, Integer> tickets : ticketRequestMap.entrySet()) {
      if (tickets.getKey() == Type.ADULT) {
        adultTickets = tickets.getValue();
      }
      if (tickets.getKey() == Type.CHILD) {
        childTickets = tickets.getValue();
      }
      if (tickets.getKey() == Type.INFANT) {
        infantTickets = tickets.getValue();
      }
    }

    validateSeating(adultTickets, childTickets, infantTickets);

    allSeats = adultTickets + childTickets;

    return allSeats;
  }

  private static void validateSeating(int adultTickets, int childTickets, int infantTickets) {
    // if child or infant there must be 1 or more adult
    if ((childTickets > 0 || infantTickets > 0) && adultTickets == 0) {
      throw new InvalidPurchaseException(
          "Cannot purchase child or infant tickets without also purchasing an adult ticket");
    }
  }
}
