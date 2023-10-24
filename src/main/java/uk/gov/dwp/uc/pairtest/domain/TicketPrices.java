package uk.gov.dwp.uc.pairtest.domain;

import java.util.HashMap;
import java.util.Map;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

public class TicketPrices {

  private static final Map<Type, Integer> PRICE_TABLE =
      new HashMap<>() {
        {
          put(TicketTypeRequest.Type.ADULT, 20);
          put(TicketTypeRequest.Type.CHILD, 10);
          put(TicketTypeRequest.Type.INFANT, 0);
        }
      };

  public static Integer findPriceByType(TicketTypeRequest.Type type) {
    return PRICE_TABLE.get(type);
  }
}
