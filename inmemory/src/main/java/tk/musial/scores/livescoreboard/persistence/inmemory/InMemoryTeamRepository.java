package tk.musial.scores.livescoreboard.persistence.inmemory;

import java.util.HashSet;
import java.util.Set;
import tk.musial.scores.livescoreboard.domain.Team;
import tk.musial.scores.livescoreboard.persistence.TeamRepository;

public class InMemoryTeamRepository implements TeamRepository {

  private final Set<Team> teams = new HashSet<>();
  private final Object updateLock = new Object();

  private void validateNotCurrentlyPlaying(Team team) throws DuplicateTeamException {
    if (teams.contains(team)) {
      throw new DuplicateTeamException(team.name() + " exists already");
    }
  }

  @Override
  public void add(Team home, Team away) throws DuplicateTeamException {
    synchronized (updateLock) {
      validateNotCurrentlyPlaying(home);
      validateNotCurrentlyPlaying(away);
      teams.add(home);
      teams.add(away);
    }
  }

  @Override
  public void remove(Team home, Team away) {
    synchronized (updateLock) {
      teams.remove(home);
      teams.remove(away);
    }
  }
}
