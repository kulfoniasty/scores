package tk.musial.scores.livescoreboard.domain;

import java.time.Instant;

public interface Match {
  Team getHome();

  Team getAway();

  Score getScore();

  Instant getStartTime();

  FinishedMatch finish(Instant endTime);

  Match updateScore(Score newScore);
}
