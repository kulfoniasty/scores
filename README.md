# Live World Cup Score Board

The master branch of this repository is left empty on purpose. It contains only pom.xml, README.MD and Maven Wrapper configuration.

# Explicit solution

On a branch `explicit-solution` you can find a solution which is an explicit implementation of the requirements given in the PDF.

The approach taken is make the Score Board a center of the implementation and a single point of contact for the whole library.

Any state transitions of a match can only be done by the scoreboard itself. State transitions are guarded by `synchronized` blocks to make sure that
during the team validation no new match sneaks in and breaks the consistency. Surely, we could use ConcurrentHashMap but this only makes sure that actual .put and .remove calls are thread-safe not keeping our business invariants.

# Alternative solution

On a branch `rich-domain-solution` you can find an alternative approach to the problem.

What bothered me in the explicit solution:
- score board was keeping the whole business knowledge
- even if I managed to extract some of the business invariants to Match and Score there was still the Score Board being "too fat"
- what bothered me the most was the fact that I could not call "match.updateScore" which I considered to be a state transition of a Match itself and by no means a state of the ScoreBoard (again seeing a TV on a wall in front of my eyes)
- another pain point was the fact that I cannot have more boards displaying my world cup results
- the summary function in a board just screams to be a lame "data to display" function. And thinking about the board I picture a screen somewhere. Hanging on the wall just displaying data. And definitely not keeping any business knowledge
- however the lack of knowledge in the Score Board itself forced me to create a domain WorldCup object which guards the World Cup Games invariants, e.g. no team can play two matches at the same time.knowledge

    - and of course it serves as a factory of rich Match objects that should not be instantiated by the library clients.

- having all said above in mind the small Observer pattern started rising from the ashes

    - with WorldCup and ScoreBoard being observers. Genuinely interested in the match state, but from totally different reasons.

