package tk.musial.scores.livescoreboard;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InMemoryScoreBoardTest {

  private static final Instant NOW = Instant.ofEpochSecond(1697580088);
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

  private final InMemoryScoreBoard scoreBoard = new InMemoryScoreBoard(CLOCK);

  @Nested
  class StartMatch {

    @ParameterizedTest
    @MethodSource("teamPairs")
    void startsWithInitialScoreAndAddsToTheBoard(String home, String away) {
      Match actual = scoreBoard.startMatch(new Team(home), new Team(away));

      assertThat(scoreBoard.getSummary()).contains(summaryOf(actual));
    }

    @Test
    void startsWithCurrentTimestampAsStartTime() {
      Team home = new Team("Germany");
      Team away = new Team("France");
      var actual = scoreBoard.startMatch(home, away);

      assertThat(actual).isEqualTo(new Match(home, away, NOW));
    }

    @Test
    void throwsException_whenMatchAlreadyInProgress() {
      Team home = new Team("Australia");
      Team away = new Team("Spain");
      scoreBoard.startMatch(home, away);

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> scoreBoard.startMatch(home, away));
    }

    @Test
    void throwsException_whenHomeTeamIsAlreadyInMatch() {
      Team home = new Team("Australia");
      Team away = new Team("Spain");
      scoreBoard.startMatch(home, away);

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> scoreBoard.startMatch(home, new Team("Egypt")))
          .withMessage(home.name() + " is currently playing");
    }

    @Test
    void throwsException_whenAwayTeamIsAlreadyInMatch() {
      Team home = new Team("Portugal");
      Team away = new Team("Belgium");
      scoreBoard.startMatch(home, away);

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> scoreBoard.startMatch(new Team("Poland"), away))
          .withMessage(away.name() + " is currently playing");
    }

    @Test
    void startCanBeInvokedInParallel() throws Exception {
      Team home = new Team("Germany");
      Team away = new Team("France");

      AtomicLong exceptionCount = new AtomicLong(0);
      Runnable inserter =
          () -> {
            try {
              scoreBoard.startMatch(home, away);
            } catch (IllegalArgumentException e) {
              exceptionCount.incrementAndGet();
            }
          };

      runConcurrently(inserter);

      assertThat(exceptionCount.get()).isEqualTo(9999);
    }

    private void runConcurrently(Runnable inserter)
        throws InterruptedException, ExecutionException {
      ExecutorService threadExecutor = Executors.newFixedThreadPool(200);

      List<Future<?>> tasks =
          LongStream.range(0, 10000)
              .parallel()
              .mapToObj(i -> threadExecutor.submit(inserter))
              .collect(toList());

      awaitAll(tasks);
    }

    private void awaitAll(List<Future<?>> tasks) throws InterruptedException, ExecutionException {
      for (Future<?> t : tasks) {
        t.get();
      }
    }

    static Stream<Arguments> teamPairs() {
      return Stream.of(
          Arguments.of("Poland", "Moldova"),
          Arguments.of("Germany", "France"),
          Arguments.of("Italy", "Greece"),
          Arguments.of("Italy2", "Greece2"),
          Arguments.of("Italy4", "Greece3"),
          Arguments.of("Italy22", "Gre2ece3"),
          Arguments.of("Italy232", "Gre32ece3"),
          Arguments.of("Ital2y232", "G2re32ece3"));
    }
  }

  @Nested
  class UpdateScore {

    @Test
    void throwsError_whenMatchDoesNotExistOnScoreBoard() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(
              () -> {
                Match match = new Match(new Team("Poland"), new Team("England"), NOW);
                scoreBoard.updateScore(match, new Score(1, 3));
              })
          .withMessage("Match not found");
    }

    @Test
    void throwsError_whenScoreTheSame() {
      Match match = scoreBoard.startMatch(new Team("Poland"), new Team("England"));

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> scoreBoard.updateScore(match, new Score(0, 0)))
          .withMessage("Invalid score change");
    }

    @Test
    void throwsError_whenScoreUpdatedToLower() {
      Match match = scoreBoard.startMatch(new Team("Poland"), new Team("Finland"));
      scoreBoard.updateScore(match, new Score(1, 1));

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> scoreBoard.updateScore(match, new Score(0, 1)))
          .withMessage("Invalid score change");
    }

    @Test
    void updates() {
      Match match = scoreBoard.startMatch(new Team("Poland"), new Team("Finland"));
      Score newScore = new Score(1, 1);
      scoreBoard.updateScore(match, newScore);

      assertThat(scoreBoard.getSummary()).contains(summaryOf(match, newScore));
    }
  }

  @Nested
  class Finish {

    @Test
    void throwsError_whenMatchDoesNotExistOnScoreBoard() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(
              () -> {
                Match match = new Match(new Team("Poland"), new Team("England"), NOW);
                scoreBoard.finish(match);
              })
          .withMessage("Match not found");
    }

    @Test
    void finishes_removesTheMatchFromScoreBoard() {
      Match first = scoreBoard.startMatch(new Team("Poland"), new Team("England"));
      Match second = scoreBoard.startMatch(new Team("Brazil"), new Team("France"));

      assertThat(scoreBoard.getSummary()).containsOnly(summaryOf(first), summaryOf(second));

      scoreBoard.finish(first);

      assertThat(scoreBoard.getSummary()).containsOnly(summaryOf(second));
    }
  }

  @Nested
  class GetSummary {

    private MutableTestClock mutableClock = new MutableTestClock(NOW);
    private ScoreBoard summaryScoreBoard = new InMemoryScoreBoard(mutableClock);

    @Test
    void lists_allMatchesInProgress() {
      Match first = summaryScoreBoard.startMatch(new Team("Poland"), new Team("England"));
      Match second = summaryScoreBoard.startMatch(new Team("Brazil"), new Team("France"));
      Match third = summaryScoreBoard.startMatch(new Team("USA"), new Team("Canada"));
      Match fourth = summaryScoreBoard.startMatch(new Team("Columbia"), new Team("Argentina"));
      summaryScoreBoard.finish(fourth);

      assertThat(summaryScoreBoard.getSummary())
          .containsOnly(summaryOf(first), summaryOf(second), summaryOf(third));
    }

    @Test
    void lists_forTheSameTotalScoreMoreReentMatchComesFirst() {
      Score score = new Score(10, 2);
      Match first = summaryScoreBoard.startMatch(new Team("Estonia"), new Team("Sweden"));
      summaryScoreBoard.updateScore(first, score);

      mutableClock.tickMinutes(10);

      Match second = summaryScoreBoard.startMatch(new Team("Norway"), new Team("Angora"));
      summaryScoreBoard.updateScore(second, score);

      assertThat(summaryScoreBoard.getSummary())
          .containsExactly(summaryOf(second, score), summaryOf(first, score));
    }

    @Test
    void lists_descendingByTotalScore() {
      Match first = summaryScoreBoard.startMatch(new Team("Estonia"), new Team("Sweden"));
      Score higherScore = new Score(4, 6);
      summaryScoreBoard.updateScore(first, higherScore);

      Match second = summaryScoreBoard.startMatch(new Team("Norway"), new Team("Angora"));
      Score lowerScore = new Score(1, 2);
      summaryScoreBoard.updateScore(second, lowerScore);

      assertThat(summaryScoreBoard.getSummary())
          .containsExactly(summaryOf(first, higherScore), summaryOf(second, lowerScore));
    }
  }

  private String summaryOf(Match match) {
    return summaryOf(match, new Score(0, 0));
  }

  private String summaryOf(Match match, Score score) {
    return String.join(
        " ",
        match.getHome().name(),
        String.valueOf(score.getHome()),
        "-",
        match.getAway().name(),
        String.valueOf(score.getAway()));
  }
}
