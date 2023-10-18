package tk.musial.scores.livescoreboard.domain;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

@Builder
@ToString
@EqualsAndHashCode
class ObservableMatch implements Match, FinishedMatch {
  private final Team home;
  private final Team away;
  private final Score score;
  private final Instant startTime;
  private final Instant endTime;
  private final List<MatchStateObserver> observers;

  ObservableMatch(
      Team home,
      Team away,
      Score score,
      Instant startTime,
      Instant endTime,
      List<MatchStateObserver> observers) {
    Validate.isTrue(home != null && !home.equals(away), "Teams must be different");
    this.home = home;
    this.away = away;
    this.score = score;
    this.startTime = startTime;
    this.endTime = endTime;
    Validate.isTrue(observers != null && !observers.isEmpty(), "At least one observer is required");
    this.observers = List.copyOf(observers);
  }

  @Override
  public Team getHome() {
    return home;
  }

  @Override
  public Team getAway() {
    return away;
  }

  @Override
  public Score getScore() {
    return score;
  }

  @Override
  public Instant getStartTime() {
    return startTime;
  }

  @Override
  public Instant getEndTime() {
    return endTime;
  }

  @Override
  public FinishedMatch finish(Instant endTime) {
    FinishedMatch finishedMatch =
        new ObservableMatch(home, away, score, startTime, endTime, observers);
    observers.get(0).notify(finishedMatch);
    return finishedMatch;
  }

  @Override
  public Match updateScore(Score newScore) {
    Match updatedMatch =
        new ObservableMatch(home, away, score.changeTo(newScore), startTime, endTime, observers);
    observers.get(0).notify(updatedMatch);
    return updatedMatch;
  }
}
