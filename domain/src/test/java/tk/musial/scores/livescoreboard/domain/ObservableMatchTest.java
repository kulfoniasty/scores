package tk.musial.scores.livescoreboard.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObservableMatchTest {

  @Mock private MatchStateObserver observer;

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
  }

  @Nested
  class UpdateScore {

    @ParameterizedTest
    @ValueSource(strings = {"France", "Poland"})
    void updatesScore(String home) {
      Match match = match(home, "England");

      Score newScore = new Score(1, 0);
      var actual = match.updateScore(newScore);

      assertThat(actual.getScore()).isEqualTo(newScore);
    }

    @Test
    void returnsNewMatchObject() {
      Match match = match("Argentina", "Brazil");

      Score newScore = new Score(0, 1);
      var actual = match.updateScore(newScore);

      assertThat(actual)
          .isNotSameAs(match)
          .isEqualTo(
              new ObservableMatch(
                  match.getHome(),
                  match.getAway(),
                  newScore,
                  match.getStartTime(),
                  null,
                  List.of(observer)));
    }

    @Test
    void notifiesTheObserverWithTheNewState() {
      Match match = match("Ireland", "Wales");

      Score newScore = new Score(1, 0);

      match.updateScore(newScore);

      Match expectedMatch =
          new ObservableMatch(
              match.getHome(),
              match.getAway(),
              newScore,
              match.getStartTime(),
              null,
              List.of(observer));
      verify(observer).notify(expectedMatch);
    }
  }

  @Nested
  class Finish {

    @ParameterizedTest
    @ValueSource(strings = {"France", "Poland"})
    void setsEndTime(String home) {
      Match match = match(home, "England");
      Instant endTime = Instant.now();

      var actual = match.finish(endTime);

      assertThat(actual.getEndTime()).isEqualTo(endTime);
    }

    @ParameterizedTest
    @ValueSource(strings = {"France", "Poland"})
    void returnsNewMatchInstanceWithTheSameData(String home) {
      Match match = match(home, "Algeria").updateScore(new Score(1, 0));
      Instant endTime = Instant.now();

      var actual = match.finish(endTime);

      assertThat(actual)
          .isNotSameAs(match)
          .isEqualTo(
              new ObservableMatch(
                  match.getHome(),
                  match.getAway(),
                  match.getScore(),
                  match.getStartTime(),
                  endTime,
                  List.of(observer)));
    }

    @Test
    void returnsNonUpdatableMatch() {
      Match match = match("Spain", "Portugal");
      Instant endTime = Instant.now();

      var actual = match.finish(endTime);

      assertThat(actual).isInstanceOf(FinishedMatch.class);
    }

    @Test
    void notifiesTheObserverWithTheNewState() {
      Score newScore = new Score(1, 0);
      Instant endTime = Instant.now();
      FinishedMatch match = match("Ireland", "Colombia").updateScore(newScore).finish(endTime);

      FinishedMatch expectedMatch =
          new ObservableMatch(
              match.getHome(),
              match.getAway(),
              newScore,
              match.getStartTime(),
              endTime,
              List.of(observer));
      verify(observer).notify(expectedMatch);
    }
  }

  private Match match(String home, String away) {
    return new ObservableMatch(
        new Team(home), new Team(away), new Score(0, 0), Instant.now(), null, List.of(observer));
  }
}
