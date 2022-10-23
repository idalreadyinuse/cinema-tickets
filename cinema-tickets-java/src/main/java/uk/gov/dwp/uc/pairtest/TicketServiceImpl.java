package uk.gov.dwp.uc.pairtest;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /** Should only have private methods other than the one below. */

    private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());

    private static final int ADULT_TICKET_COST = 20;
    private static final int CHILD_TICKET_COST = 10;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(
        TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
        throws InvalidPurchaseException {

        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be present and greater than zero");
        }

        List<TicketTypeRequest> ticketTypeRequestList = List.of(ticketTypeRequests);
        Map<Type, Integer> ticketRequestMap =
            ticketTypeRequestList.stream()
                .collect(
                    Collectors.toMap(
                        TicketTypeRequest::getTicketType, TicketTypeRequest::getNoOfTickets));

        if (totalTicketCheck(ticketRequestMap) > 20) {
            throw new InvalidPurchaseException("Total tickets ordered exceeds maximum allowed: 20");
        }

        int seatsToReserve = seatsToReserve(ticketRequestMap);
        int cost = totalOrderValue(ticketRequestMap);

        // send for payment
        LOGGER.info(String.format("Requesting payment for accountID %s for £%s.", accountId, cost));
        ticketPaymentService.makePayment(accountId, cost);

        // send for reservation
        LOGGER.info(String.format("Reserving %s seats for accountID %s.", seatsToReserve, accountId));
        seatReservationService.reserveSeat(accountId, seatsToReserve);
    }

    private int totalTicketCheck(Map<Type, Integer> ticketRequestMap) {

        return ticketRequestMap.values().stream().mapToInt(m -> m).sum();
    }

    private int totalOrderValue(Map<Type, Integer> ticketRequestMap) {
        int totalCost = 0;

        for (Map.Entry<Type, Integer> tickets : ticketRequestMap.entrySet()) {
            if (tickets.getKey() == Type.ADULT) {
                totalCost = totalCost + ADULT_TICKET_COST * tickets.getValue();
            }
            if (tickets.getKey() == Type.CHILD) {
                totalCost = totalCost + CHILD_TICKET_COST * tickets.getValue();
            }
        }

        return totalCost;
    }

    private int seatsToReserve(Map<Type, Integer> ticketRequestMap) {
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

    private void validateSeating(int adultTickets, int childTickets, int infantTickets) {

        // if child or infant there must be 1 or more adult
        if ((childTickets > 0 || infantTickets > 0) && adultTickets == 0) {
            throw new InvalidPurchaseException(
                "Cannot purchase child or infant tickets without also purchasing an adult ticket");
        }
    }
}
