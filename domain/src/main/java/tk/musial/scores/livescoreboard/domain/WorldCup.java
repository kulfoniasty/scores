package tk.musial.scores.livescoreboard.domain;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import tk.musial.scores.livescoreboard.persistence.TeamRepository;

@AllArgsConstructor
public class WorldCup implements MatchStateObserver {

  private final Clock clock;
  private final TeamRepository teamRepository;
  private final List<MatchStateObserver> observers = new ArrayList<>();

  public Match startMatch(Team home, Team away) {
    Match match =
        new ObservableMatch(home, away, new Score(0, 0), clock.instant(), null, List.of(this));
    teamRepository.add(home, away);
    this.notify(match);
    return match;
  }

  @Override
  public void notify(Match match) {
    observers.forEach(o -> o.notify(match));
  }

  @Override
  public void notify(FinishedMatch match) {
    teamRepository.remove(match.getHome(), match.getAway());
    observers.forEach(o -> o.notify(match));
  }

  public void observe(MatchStateObserver observer) {
    observers.add(observer);
  }
}
