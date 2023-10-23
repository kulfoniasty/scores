# Live World Cup Score Board

The master branch of this repository is left empty on purpose. It contains only pom.xml, README.MD and Maven Wrapper configuration.

# Explicit solution

On the `explicit-solution` branch you can find a solution which is an explicit implementation of the requirements given in the PDF.

The approach taken is to make the Score Board a center of the implementation and a single point of contact for the whole library.

Any state transitions of a match can only be done by the scoreboard itself. State transitions are guarded by `synchronized` blocks to make sure that
during the team validation no new match sneaks in and breaks the consistency. Surely, we could use ConcurrentHashMap but this only makes sure that actual .put and .remove calls are thread-safe not keeping our business invariants.

The major pain point of this Score Board-centric approach is violation of Single Responsibility Principle. The board just does too much.

# Alternative solution

On the `rich-domain-solution` branch you can find an alternative approach to the problem.

## What bothered me in the explicit solution
- Score Board was keeping the whole business knowledge
- even if I managed to extract some of the business invariants to Match and Score there was still the Score Board being "too fat"
- what probably bothered me the most was the fact that I could not call "match.updateScore" which I considered to be a state transition of a Match itself and by no means a state of the Score Board
- another pain point was the fact that I cannot have more boards displaying my world cup results
- the summary function in a board just screams to be a lame "data to display" function. And thinking about the board I picture a screen somewhere. Hanging on the wall just displaying data. And definitely not keeping any business knowledge
- however the lack of knowledge in the Score Board itself forced me to create a domain WorldCup object which guards the World Cup Games invariants, e.g. no team can play two matches at the same time.knowledge

    - and of course it serves as a factory of rich Match objects that should not be instantiated by the library clients.

- having all said above in mind the small Observer pattern started rising from the ashes

    - with WorldCup and ScoreBoard being observers. Genuinely interested in the match state, but from totally different reasons.

## PROS of the rich domain approach
- WorldCup is a domain object - keeps the knowledge of teams playing and does not allow the same team to play at the same time
- ScoreBoard finally is just a display screen. Moreover there can be as many ScoreBoards as we need. There can also be arbitrarily many observers, e.g. News Feeds, Emails, SMS service etc.
- Match and Score keep their state and handle transitions. In an immutable way of course.

# Open For Change - Closed for Modification

Picture having a new requirement to add cards to matches. So that we can call `math.card(player, color)`
- In the `rich-domain` the only class that needs changes is `Match`

    - any observers are moreover free to implement the new state update at will. They can also ignore it.
- In the `explicit` solution we would have to touch a ScoreBoard to expose a new action and then forward this action to a match itself changing the `Match` class.
