# Cinema Tickets Code Test

My solution for the code test.

I have made the assumption that the calling service will do the necessary to ensure that a total of
each ticket type is requested; so the service doesn't have to handle a request of, for example:
1 adult, 3 child, 2 infant, 2 adult and 4 child tickets.

If this was real-world, I would seek clarification on a couple of the business rules during refinement.

- Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.

That's fine, but what about a single adult with infant twins or, at a push, triplets?
Notwithstanding this, I have a check in place to ensure that there are no more infant tickets than
adult.

- Only a maximum of 20 tickets that can be purchased at a time.

I have taken this at face value in that an error is thrown if the total of **all** tickets, of **all**
types is more than 20. However, as infant tickets cost £0, are they actually purchased; and, as an
infant sits on an adult's lap, one could argue to exclude infant tickets from the maximum
allowed as their seats don't count.
