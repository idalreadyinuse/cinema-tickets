package uk.gov.dwp.uc.pairtest.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

public class TicketCostComputeServiceTest {

  private TicketCostComputeService service;

  @Before
  public void setUp() {
    service = new TicketCostComputeService();
  }

  @Test
  public void should_return_20_when_only_one_adult() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 1);

    assertEquals(20, service.totalOrderValue(order));
  }

  @Test
  public void should_return_20_when_one_adult_and_one_infant() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 1);
    order.put(Type.INFANT, 1);

    assertEquals(20, service.totalOrderValue(order));
  }

  @Test
  public void should_return_30_when_one_adult_and_one_child() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 1);
    order.put(Type.CHILD, 1);

    assertEquals(30, service.totalOrderValue(order));
  }

  @Test
  public void should_return_30_when_one_adult_one_child_and_one_infant() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 1);
    order.put(Type.CHILD, 1);
    order.put(Type.INFANT, 1);

    assertEquals(30, service.totalOrderValue(order));
  }

  @Test
  public void should_return_60_when_two_adults_and_two_child_and_one_infant() {
    var order = new HashMap<Type, Integer>();
    order.put(Type.ADULT, 2);
    order.put(Type.CHILD, 2);
    order.put(Type.INFANT, 1);

    assertEquals(60, service.totalOrderValue(order));
  }
}
