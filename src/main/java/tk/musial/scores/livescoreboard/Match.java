package tk.musial.scores.livescoreboard;

import java.time.Instant;
import lombok.Value;
import org.apache.commons.lang3.Validate;

@Value
public class Match {
  Team home;
  Team away;
  Instant startTime;

  public Match(Team home, Team away, Instant startTime) {
    Validate.isTrue(home != null && !home.equals(away), "Teams must be different");
    this.home = home;
    this.away = away;
    this.startTime = startTime;
  }
}
