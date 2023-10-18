package tk.musial.scores.livescoreboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScoreTest {

  @Nested
  class Constructor {

    @Test
    void rejectsNegativeHomeScore() {
      assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Score(-1, 1));
    }

    @Test
    void rejectsNegativeAwayScore() {
      assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Score(2, -10));
    }

    @Test
    void acceptsZeroToZero() {
      assertThat(new Score(0, 0)).isNotNull();
    }

    @Test
    void acceptsNonZeroScores() {
      assertThat(new Score(1, 2)).isNotNull();
    }
  }

  @Nested
  class ChangeTo {

    private final Score score = new Score(1, 2);

    @Test
    void throws_cannotChangeToTheSameScore() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> score.changeTo(score));
    }

    @Test
    void throws_cannotChangeToTheLowerHomeScore() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> score.changeTo(new Score(score.getHome() - 1, score.getAway() + 2)));
    }

    @Test
    void throws_cannotChangeToTheLowerAwayScore() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> score.changeTo(new Score(score.getHome() + 1, score.getAway() - 1)));
    }

    @Test
    void throws_cannotChangeToTheLowerBothScores() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> score.changeTo(new Score(score.getHome() - 1, score.getAway() - 1)));
    }

    @Test
    void changesHomeScore() {
      Score newScore = new Score(score.getHome() + 2, score.getAway());

      var actual = score.changeTo(newScore);

      assertThat(actual).isEqualTo(newScore);
    }

    @Test
    void changesAwayScore() {
      Score newScore = new Score(score.getHome(), score.getAway() + 1);

      var actual = score.changeTo(newScore);

      assertThat(actual).isEqualTo(newScore);
    }

    @Test
    void true_correctChangeToBothScores() {
      Score newScore = new Score(score.getHome() + 2, score.getAway() + 5);

      var actual = score.changeTo(newScore);

      assertThat(actual).isEqualTo(newScore);
    }
  }

  @Nested
  class CompareTo {

    private final Score higher = new Score(10, 5);
    private final Score lower = new Score(2, 4);

    @Test
    void zeroForEqual() {
      assertThat(higher.compareTo(higher)).isZero();
      assertThat(lower.compareTo(lower)).isZero();
      assertThat(lower.compareTo(new Score(lower.getHome(), lower.getAway()))).isZero();
    }

    @Test
    void higherTotalScoreComesFirst() {
      assertThat(higher.compareTo(lower)).isPositive();
    }

    @Test
    void lowerTotalScoreComesLast() {
      assertThat(lower.compareTo(higher)).isNegative();
    }
  }
}
