package tk.musial.scores.livescoreboard;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ActiveMatch {
    private final Instant startTime;
    private final Match match;
    private final Score score;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final ScoreBoard scoreBoard;

    public Score getScore() {
        return score;
    }

    public ActiveMatch updateScore(Score score) { return null; }
    public void finish() {}

}
