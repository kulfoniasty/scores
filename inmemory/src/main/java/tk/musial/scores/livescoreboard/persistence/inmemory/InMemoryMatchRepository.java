package tk.musial.scores.livescoreboard.persistence.inmemory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import tk.musial.scores.livescoreboard.domain.Match;
import tk.musial.scores.livescoreboard.persistence.MatchRepository;

public class InMemoryMatchRepository implements MatchRepository {

  private final ConcurrentHashMap<MatchKey, Match> items = new ConcurrentHashMap<>();

  @Override
  public void put(MatchKey key, Match match) {
    items.put(key, match);
  }

  @Override
  public void remove(MatchKey key) {
    items.remove(key);
  }

  @Override
  public Stream<Match> getAll() {
    return items.values().stream();
  }
}
