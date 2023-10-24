package uk.gov.dwp.uc.pairtest.service;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.CHILD;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.INFANT;

import java.util.Map;
import java.util.Map.Entry;
import uk.gov.dwp.uc.pairtest.domain.TicketPrices;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

public class TicketCostComputeService {

  public int totalOrderValue(Map<Type, Integer> ticketRequestMap) {
    int totalCost = 0;

    for (Entry<Type, Integer> tickets : ticketRequestMap.entrySet()) {
      switch (tickets.getKey()) {
        case ADULT -> totalCost += TicketPrices.findPriceByType(ADULT) * tickets.getValue();
        case CHILD -> totalCost += TicketPrices.findPriceByType(CHILD) * tickets.getValue();
        case INFANT -> totalCost += TicketPrices.findPriceByType(INFANT) * tickets.getValue();
        default -> totalCost += 0;
      }
    }

    return totalCost;
  }
}
