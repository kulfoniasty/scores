package tk.musial.scores.livescoreboard.domain;

import java.time.Instant;

public interface FinishedMatch {
  Team getHome();

  Team getAway();

  Score getScore();

  Instant getStartTime();

  Instant getEndTime();
}
