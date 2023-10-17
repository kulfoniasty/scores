package tk.musial.scores.livescoreboard;

public record Match(Team home, Team away) {
    public ActiveMatch start(ScoreBoard scoreBoard) {
        return null;
    }
}
