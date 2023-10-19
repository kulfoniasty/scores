# Live World Cup Score Board

The master branch of this repository is left empty on purpose. It contains only pom.xml, README.MD and Maven Wrapper configuration.

# Explicit solution

On a branch `explicit-solution` you can find a solution which is an explicit implementation of the requirements given in the PDF.

The approach taken is make the Score Board a center of the implementation and a single point of contact for the whole library.

Any state transitions of a match can only be done by the scoreboard itself.

# Alternative solution

On a branch `rich-domain-solution` you can find an alternative approach to the problem.

I stopped treating the requirements literally and therefore started allocating business logic, invariants to domain objects. This is somewhat expired by the DDD. I attempted to identify aggregates.
My thinking when coding this was to no longer make the Score Board anything more than a board. With a simply summary method allowing you to see the board.
The center of the "universe" now constitutes the Match. It can be started, it can be finished and also a match itself makes sure that the score is initially correct
and transitions are correct.

Score board role is greatly diminished to be a score listener. Nothing more than a dumb "display screen" with sorting capability.
