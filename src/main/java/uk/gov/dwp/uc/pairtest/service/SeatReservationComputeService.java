package uk.gov.dwp.uc.pairtest.service;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.CHILD;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.INFANT;

import java.util.Map;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class SeatReservationComputeService {

  public int seatsToReserve(Map<Type, Integer> ticketMap) {

    final int adultTickets = ticketMap.get(ADULT) == null ? 0 : ticketMap.get(ADULT);
    final int childTickets = ticketMap.get(CHILD) == null ? 0 : ticketMap.get(CHILD);
    final int infantTickets = ticketMap.get(INFANT) == null ? 0 : ticketMap.get(INFANT);

    checkAdultPresent(adultTickets, childTickets, infantTickets);
    checkAdultToInfantRatio(adultTickets, infantTickets);

    return adultTickets + childTickets;
  }

  private void checkAdultPresent(int adultTickets, int childTickets, int infantTickets) {
    if ((childTickets > 0 || infantTickets > 0) && adultTickets == 0) {
      throw new InvalidPurchaseException(
          "Cannot purchase child or infant tickets without also purchasing an adult ticket");
    }
  }

  private void checkAdultToInfantRatio(int adultTickets, int infantTickets) {
    if (infantTickets > adultTickets) {
      throw new InvalidPurchaseException("Cannot have more infant tickets than adult tickets");
    }
  }
}
