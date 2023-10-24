package uk.gov.dwp.uc.pairtest.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

public class TicketPricesTest {

  @Test
  public void should_return_adult_ticket_price() {
    var actual = TicketPrices.findPriceByType(Type.ADULT);

    assertEquals(20, actual.intValue());
  }

  @Test
  public void should_return_child_ticket_price() {
    var actual = TicketPrices.findPriceByType(Type.CHILD);

    assertEquals(10, actual.intValue());
  }

  @Test
  public void should_return_infant_ticket_price() {
    var actual = TicketPrices.findPriceByType(Type.INFANT);

    assertEquals(0, actual.intValue());
  }
}
