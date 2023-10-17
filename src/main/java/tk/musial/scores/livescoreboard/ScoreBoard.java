package tk.musial.scores.livescoreboard;

import java.util.List;

public interface ScoreBoard {
    ActiveMatch start(Match match);
    List<String> getSummary();
}
