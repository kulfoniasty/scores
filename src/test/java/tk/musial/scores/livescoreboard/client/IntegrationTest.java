package tk.musial.scores.livescoreboard.client;

import java.util.List;

import org.junit.jupiter.api.Test;

import tk.musial.scores.livescoreboard.InMemoryScoreBoard;
import tk.musial.scores.livescoreboard.Match;
import tk.musial.scores.livescoreboard.Score;
import tk.musial.scores.livescoreboard.ScoreBoard;
import tk.musial.scores.livescoreboard.Team;

public class IntegrationTest {

    @Test
    void playWorldCup() {
        ScoreBoard board = new InMemoryScoreBoard();

        Match match = new Match(new Team("Mexico"), new Team("Canada"));
        match.start(board)
          .updateScore(new Score(1), new Score(0))
          .updateScore(new Score(2), new Score(0))
          .finish();

        List<String> summary = board.getSummary();
    }
}
