package tk.musial.scores.livescoreboard.client;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tk.musial.scores.livescoreboard.InMemoryScoreBoard;
import tk.musial.scores.livescoreboard.Match;
import tk.musial.scores.livescoreboard.Score;
import tk.musial.scores.livescoreboard.ScoreBoard;
import tk.musial.scores.livescoreboard.Team;

public class IntegrationTest {

    @Test
    void playWorldCup() {
        var board = new InMemoryScoreBoard();

        var mexCan = new Match(new Team("Mexico"), new Team("Canada"));
        board.start(mexCan)
          .updateScore(new Score(0, 5));

        var spaBra = new Match(new Team("Spain"), new Team("Brazil"));
        board.start(spaBra)
          .updateScore(new Score(10, 2));

        var gerFra = new Match(new Team("Germany"), new Team("France"));
        board.start(gerFra)
          .updateScore(new Score(2,2));

        var uruIta = new Match(new Team("Uruguay"), new Team("Italy"));
        board.start(uruIta)
          .updateScore(new Score(6,6));

        var argAus = new Match(new Team("Argentina"), new Team("Australia"));
        board.start(argAus)
          .updateScore(new Score(3,1));


        assertThat(board.getSummary()).containsExactly(
          "Uruguay 6 - Italy 6",
          "Spain 10 - Brazil 2",
          "Mexico 0 - Canada 5",
          "Argentina 3 - Australia 1",
          "Germany 2 - France 2"
        );
    }
}
