package tk.musial.scores.livescoreboard.domain;

public interface MatchStateObserver {
  void notify(Match match);

  void notify(FinishedMatch match);
}
