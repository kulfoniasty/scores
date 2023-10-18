package tk.musial.scores.livescoreboard.persistence;

import java.util.stream.Stream;
import tk.musial.scores.livescoreboard.domain.FinishedMatch;
import tk.musial.scores.livescoreboard.domain.Match;
import tk.musial.scores.livescoreboard.domain.Team;

public interface MatchRepository {

  record MatchKey(Team home, Team away) {
    public static MatchKey matchKey(Match match) {
      return new MatchKey(match.getHome(), match.getAway());
    }

    public static MatchKey matchKey(FinishedMatch match) {
      return new MatchKey(match.getHome(), match.getAway());
    }
  }

  void put(MatchKey key, Match match);

  void remove(MatchKey key);

  Stream<Match> getAll();
}
