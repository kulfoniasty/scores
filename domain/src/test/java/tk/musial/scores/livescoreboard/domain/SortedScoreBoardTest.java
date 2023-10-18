package tk.musial.scores.livescoreboard.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static tk.musial.scores.livescoreboard.domain.ObservableMatchFixture.finishedMatch;
import static tk.musial.scores.livescoreboard.domain.ObservableMatchFixture.startedMatch;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tk.musial.scores.livescoreboard.board.SortedScoreBoard;
import tk.musial.scores.livescoreboard.persistence.MatchRepository;

class SortedScoreBoardTest {

  private final SortedScoreBoard scoreBoard = new SortedScoreBoard(new TestMatchRepository());

  @Nested
  class NotifyUpdate {

    @Test
    void adds_ifHasNotExistedYet() {
      Match match = startedMatch().build();

      scoreBoard.notify(match);

      assertThat(scoreBoard.getSummary()).contains(summaryOf(match, match.getScore()));
    }

    @Test
    void updates() {
      Score newScore = new Score(1, 1);
      Match match =
          startedMatch().home(new Team("Poland")).away(new Team("Finland")).score(newScore).build();

      scoreBoard.notify(match);

      assertThat(scoreBoard.getSummary()).contains(summaryOf(match, newScore));
    }
  }

  @Nested
  class NotifyFinish {

    @Test
    void doesNothing_ifNotExists() {
      scoreBoard.notify(finishedMatch().build());
    }

    @Test
    void removes() {
      Match first = startedMatch().home(new Team("Poland")).away(new Team("England")).build();
      Match second = startedMatch().home(new Team("Brazil")).away(new Team("France")).build();

      scoreBoard.notify(first);
      scoreBoard.notify(second);

      assertThat(scoreBoard.getSummary()).containsOnly(summaryOf(first), summaryOf(second));

      scoreBoard.notify(first.finish(Instant.now()));

      assertThat(scoreBoard.getSummary()).containsOnly(summaryOf(second));
    }
  }

  @Nested
  class GetSummary {

    private ObservableMatchFixture.MatchBuilder<Match> baseMatch = startedMatch();

    @Test
    void lists_allMatchesInProgress() {
      Match first = baseMatch.home(new Team("Poland")).away(new Team("England")).build();
      Match second = baseMatch.home(new Team("Brazil")).away(new Team("France")).build();
      Match third = baseMatch.home(new Team("USA")).away(new Team("Canada")).build();
      Match fourth = baseMatch.home(new Team("Columbia")).away(new Team("Argentina")).build();

      scoreBoard.notify(first);
      scoreBoard.notify(second);
      scoreBoard.notify(third);
      scoreBoard.notify(fourth);

      scoreBoard.notify(fourth.finish(Instant.now()));

      assertThat(scoreBoard.getSummary())
          .containsOnly(summaryOf(first), summaryOf(second), summaryOf(third));
    }

    @Test
    void lists_forTheSameTotalScoreMoreReentMatchComesFirst() {
      Instant now = Instant.ofEpochSecond(1698013285);
      Score score = new Score(10, 2);
      Match first =
          baseMatch
              .home(new Team("Estonia"))
              .away(new Team("Sweden"))
              .score(score)
              .startTime(now)
              .build();
      scoreBoard.notify(first);

      Instant tenMinutesLater = now.plusSeconds(10 * 60);

      Match second =
          baseMatch
              .home(new Team("Norway"))
              .away(new Team("Angora"))
              .score(score)
              .startTime(tenMinutesLater)
              .build();
      scoreBoard.notify(second);

      assertThat(scoreBoard.getSummary())
          .containsExactly(summaryOf(second, score), summaryOf(first, score));
    }

    @Test
    void lists_descendingByTotalScore() {
      Instant now = Instant.ofEpochSecond(1699213299);
      Score higherScore = new Score(4, 6);
      Match first =
          baseMatch
              .home(new Team("Estonia"))
              .away(new Team("Sweden"))
              .score(higherScore)
              .startTime(now)
              .build();

      Score lowerScore = new Score(1, 2);
      Match second =
          baseMatch
              .home(new Team("Norway"))
              .away(new Team("Angora"))
              .startTime(now)
              .score(lowerScore)
              .build();

      scoreBoard.notify(first);
      scoreBoard.notify(second);

      assertThat(scoreBoard.getSummary())
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

  private static final class TestMatchRepository implements MatchRepository {

    private final Map<MatchKey, Match> matches = new HashMap<>();

    @Override
    public void put(MatchKey key, Match match) {
      matches.put(key, match);
    }

    @Override
    public void remove(MatchKey key) {
      matches.remove(key);
    }

    @Override
    public Stream<Match> getAll() {
      return matches.values().stream();
    }
  }
}
