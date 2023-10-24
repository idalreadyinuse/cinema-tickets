package uk.gov.dwp.uc.pairtest.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

public class TicketRequestMapper {

  public static Map<Type, Integer> mapRequest(List<TicketTypeRequest> ticketRequestList) {

    return ticketRequestList.stream()
        .collect(
            Collectors.toMap(TicketTypeRequest::getTicketType, TicketTypeRequest::getNoOfTickets));
  }
}
