package tk.musial.scores.livescoreboard.persistence;

import tk.musial.scores.livescoreboard.domain.Team;

public interface TeamRepository {
  void add(Team home, Team away) throws DuplicateTeamException;

  void remove(Team home, Team away);

  class DuplicateTeamException extends RuntimeException {
    public DuplicateTeamException(String message) {
      super(message);
    }
  }
}
