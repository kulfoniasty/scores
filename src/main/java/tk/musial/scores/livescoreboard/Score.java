package tk.musial.scores.livescoreboard;

import lombok.Value;
import org.apache.commons.lang3.Validate;

@Value
public class Score implements Comparable<Score> {
  int home;
  int away;

  public Score(int home, int away) {
    Validate.isTrue(home >= 0 && away >= 0, "Negative score not allowed");
    this.home = home;
    this.away = away;
  }

  public Score changeTo(Score newScore) {
    Validate.isTrue(canChangeTo(newScore), "Invalid score change");
    return newScore;
  }

  private boolean canChangeTo(Score newScore) {
    if (this.equals(newScore)) {
      return false;
    }
    return newScore.home - home >= 0 && newScore.away - away >= 0;
  }

  @Override
  public int compareTo(Score that) {
    return this.home + this.away - that.home - that.away;
  }
}
