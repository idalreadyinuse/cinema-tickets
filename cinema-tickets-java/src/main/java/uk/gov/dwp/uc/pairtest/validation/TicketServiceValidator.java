package uk.gov.dwp.uc.pairtest.validation;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.CHILD;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.INFANT;

import java.util.Map;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceValidator {

  private static final int MAX_TICKETS = 20;

  public static void applyValidation(Long accountId, Map<Type, Integer> ticketRequestMap) {

    if (accountId == null || accountId <= 0) {
      throw new InvalidPurchaseException("Account ID must be present and greater than zero");
    }

    if (totalTicketCheck(ticketRequestMap) > MAX_TICKETS) {
      throw new InvalidPurchaseException(
          "Total tickets ordered exceeds maximum allowed: " + MAX_TICKETS);
    }
  }

  public static int validateSeating(Map<Type, Integer> ticketRequestMap) {

    int adultTickets = 0;
    int childTickets = 0;
    int infantTickets = 0;

    for (Map.Entry<Type, Integer> tickets : ticketRequestMap.entrySet()) {
      if (tickets.getKey() == ADULT) {
        adultTickets = tickets.getValue();
      }
      if (tickets.getKey() == CHILD) {
        childTickets = tickets.getValue();
      }
      if (tickets.getKey() == INFANT) {
        infantTickets = tickets.getValue();
      }
    }

    checkSeating(adultTickets, childTickets, infantTickets);

    return adultTickets + childTickets;
  }

  private static int totalTicketCheck(Map<Type, Integer> ticketRequestMap) {

    return ticketRequestMap.values().stream().mapToInt(tickets -> tickets).sum();
  }

  private static void checkSeating(int adultTickets, int childTickets, int infantTickets) {

    if ((childTickets > 0 || infantTickets > 0) && adultTickets == 0) {
      throw new InvalidPurchaseException(
          "Cannot purchase child or infant tickets without also purchasing an adult ticket");
    }
  }
}
