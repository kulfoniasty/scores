package tk.musial.scores.livescoreboard;

import java.util.List;

public class InMemoryScoreBoard implements ScoreBoard {
    @Override
    public List<String> getSummary() {
        return null;
    }

    @Override
    public ActiveMatch start(Match match) {
        return match.start(this);
    }
}
