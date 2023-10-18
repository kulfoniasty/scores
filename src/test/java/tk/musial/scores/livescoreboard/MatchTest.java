package tk.musial.scores.livescoreboard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.time.Instant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MatchTest {

  @Nested
  class Constructor {

    @Test
    void throwsIfHomeAndAwayTheSame() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> match("Poland", "Poland"))
          .withMessage("Teams must be different");
    }

    @Test
    void throwsIfHomeAndAwayTheSame_caseInsensitive() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> match("Germany", "germanY"))
          .withMessage("Teams must be different");
    }

    private Match match(String home, String away) {
      return new Match(new Team(home), new Team(away), Instant.now());
    }
  }
}
