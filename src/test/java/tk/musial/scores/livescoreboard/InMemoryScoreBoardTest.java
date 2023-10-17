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
            Runnable inserter = () -> {
                try {
                    scoreBoard.startMatch(home, away);
                } catch (IllegalArgumentException e) {
                    exceptionCount.incrementAndGet();
                }
            };

            runConcurrently(inserter);

            assertThat(exceptionCount.get()).isEqualTo(9999);
        }

        private void runConcurrently(Runnable inserter) throws InterruptedException, ExecutionException {
            ExecutorService threadExecutor = Executors.newFixedThreadPool(200);

            List<Future<?>> tasks = LongStream.range(0, 10000)
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
            return Stream.of(Arguments.of("Poland", "Moldova"), Arguments.of("Germany", "France"), Arguments.of("Italy", "Greece"), Arguments.of("Italy2", "Greece2"), Arguments.of("Italy4", "Greece3"), Arguments.of("Italy22", "Gre2ece3"),
              Arguments.of("Italy232", "Gre32ece3"), Arguments.of("Ital2y232", "G2re32ece3")

            );
        }

    }

    private String summaryOf(Match match) {
        return String.join(" ", match.home()
          .name(), "0", "-", match.away()
          .name(), "0");
    }
}