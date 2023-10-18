package tk.musial.scores.livescoreboard;

import java.util.List;

public interface ScoreBoard {
  Match startMatch(Team home, Team away);

  void updateScore(Match match, Score score);

  void finish(Match match);

  List<String> getSummary();
}
