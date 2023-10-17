package tk.musial.scores.livescoreboard.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import tk.musial.scores.livescoreboard.InMemoryScoreBoard;
import tk.musial.scores.livescoreboard.Score;
import tk.musial.scores.livescoreboard.Team;

public class IntegrationTest {

    @Test
    void playWorldCup() {
        var board = new InMemoryScoreBoard(Clock.system(ZoneOffset.UTC));

        var mexCan = board.startMatch(new Team("Mexico"), new Team("Canada"));
        board.updateScore(mexCan, new Score(0, 5));

        var spaBra = board.startMatch(new Team("Spain"), new Team("Brazil"));
        board.updateScore(spaBra, new Score(10, 2));

        var gerFra = board.startMatch(new Team("Germany"), new Team("France"));
        board.updateScore(gerFra, new Score(2,2));

        var uruIta = board.startMatch(new Team("Uruguay"), new Team("Italy"));
        board.updateScore(uruIta, new Score(6,6));

        var argAus = board.startMatch(new Team("Argentina"), new Team("Australia"));
        board.updateScore(argAus, new Score(3,1));

        assertThat(board.getSummary()).containsExactly(
          "Uruguay 6 - Italy 6",
          "Spain 10 - Brazil 2",
          "Mexico 0 - Canada 5",
          "Argentina 3 - Australia 1",
          "Germany 2 - France 2"
        );
    }
}
