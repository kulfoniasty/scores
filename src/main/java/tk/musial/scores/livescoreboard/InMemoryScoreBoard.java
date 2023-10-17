package tk.musial.scores.livescoreboard;

import java.util.List;

public class InMemoryScoreBoard implements ScoreBoard, UpdatableScoreBoard {
    @Override
    public List<String> getSummary() {
        return null;
    }

    @Override
    public void put(ActiveMatch match) {

    }
}
