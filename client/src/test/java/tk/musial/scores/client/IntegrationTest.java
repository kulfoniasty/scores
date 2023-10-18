package tk.musial.scores.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import tk.musial.scores.livescoreboard.board.SortedScoreBoard;
import tk.musial.scores.livescoreboard.domain.Score;
import tk.musial.scores.livescoreboard.domain.Team;
import tk.musial.scores.livescoreboard.domain.WorldCup;
import tk.musial.scores.livescoreboard.persistence.inmemory.InMemoryMatchRepository;
import tk.musial.scores.livescoreboard.persistence.inmemory.InMemoryTeamRepository;

public class IntegrationTest {

  private final MutableTestClock mutableClock =
      new MutableTestClock(Instant.ofEpochSecond(1698192289));

  @Test
  void playWorldCup() {
    var worldCup = new WorldCup(Clock.system(ZoneOffset.UTC), new InMemoryTeamRepository());
    var board = new SortedScoreBoard(new InMemoryMatchRepository());
    worldCup.observe(board);

    var mexCan = worldCup.startMatch(new Team("Mexico"), new Team("Canada"));
    mexCan.updateScore(new Score(0, 5));
    mutableClock.tickMinutes(1);

    var spaBra = worldCup.startMatch(new Team("Spain"), new Team("Brazil"));
    spaBra.updateScore(new Score(10, 2));
    mutableClock.tickMinutes(1);

    var gerFra = worldCup.startMatch(new Team("Germany"), new Team("France"));
    gerFra.updateScore(new Score(2, 2));
    mutableClock.tickMinutes(1);

    var uruIta = worldCup.startMatch(new Team("Uruguay"), new Team("Italy"));
    uruIta.updateScore(new Score(6, 6));
    mutableClock.tickMinutes(1);

    var argAus = worldCup.startMatch(new Team("Argentina"), new Team("Australia"));
    argAus.updateScore(new Score(3, 1));
    mutableClock.tickMinutes(1);

    assertThat(board.getSummary())
        .containsExactly(
            "Uruguay 6 - Italy 6",
            "Spain 10 - Brazil 2",
            "Mexico 0 - Canada 5",
            "Argentina 3 - Australia 1",
            "Germany 2 - France 2");

    uruIta.finish(Instant.now());

    assertThat(board.getSummary())
        .containsExactly(
            "Spain 10 - Brazil 2",
            "Mexico 0 - Canada 5",
            "Argentina 3 - Australia 1",
            "Germany 2 - France 2");

    mexCan.finish(Instant.now());

    assertThat(board.getSummary())
        .containsExactly(
            "Spain 10 - Brazil 2", "Argentina 3 - Australia 1", "Germany 2 - France 2");
  }
}
