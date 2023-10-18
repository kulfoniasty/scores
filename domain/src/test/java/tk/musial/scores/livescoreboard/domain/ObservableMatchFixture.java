package tk.musial.scores.livescoreboard.domain;

import java.time.Instant;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;

public class ObservableMatchFixture {

  public static MatchBuilder<Match> startedMatch() {
    return new MatchBuilder<Match>()
        .home(new Team("Spain"))
        .away(new Team("Poland"))
        .score(new Score(0, 0))
        .startTime(Instant.now());
  }

  public static MatchBuilder<FinishedMatch> finishedMatch() {
    return new MatchBuilder<FinishedMatch>()
        .home(new Team("Moldova"))
        .away(new Team("Greece"))
        .score(new Score(3, 1))
        .endTime(Instant.now().plusSeconds(60));
  }

  @Setter
  @Accessors(fluent = true, prefix = "")
  public static class MatchBuilder<T> {
    private Team home;
    private Team away;
    private Score score;
    private Instant startTime;
    private Instant endTime;
    private List<MatchStateObserver> observers = List.of(new NoopObserver());

    public T build() {
      return (T) new ObservableMatch(home, away, score, startTime, endTime, observers);
    }
  }

  private static final class NoopObserver implements MatchStateObserver {

    @Override
    public void notify(Match match) {}

    @Override
    public void notify(FinishedMatch match) {}
  }
}
