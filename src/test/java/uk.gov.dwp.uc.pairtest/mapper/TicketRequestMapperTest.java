package uk.gov.dwp.uc.pairtest.mapper;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketRequestMapperTest {

  @Test
  public void should_return_map_from_list_of_requests() {
    var actual = TicketRequestMapper.mapRequest(List.of(
        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
    ));

    assertEquals(5, actual.get(TicketTypeRequest.Type.ADULT).intValue());
    assertEquals(2, actual.get(TicketTypeRequest.Type.CHILD).intValue());
    assertEquals(3, actual.get(TicketTypeRequest.Type.INFANT).intValue());
  }
}
