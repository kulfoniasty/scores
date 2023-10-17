package tk.musial.scores.livescoreboard;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ActiveMatch {
    private final Instant startTime;
    private final Match match;
    private final Score home;
    private final Score away;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final ScoreBoard scoreBoard;

    public ActiveMatch updateScore(Score home, Score away) { return null; }
    public void finish() {}
}
