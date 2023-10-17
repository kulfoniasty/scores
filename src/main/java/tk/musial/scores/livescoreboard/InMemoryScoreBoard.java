package tk.musial.scores.livescoreboard;

import static java.util.stream.Collectors.toList;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScoreBoard implements ScoreBoard {
    private final Clock clock;

    private final Map<Match, Score> items = new ConcurrentHashMap<>();
    private final Object startLock = new Object();

    public InMemoryScoreBoard(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Match startMatch(Team home, Team away) {
        synchronized (startLock) {
            var match = new Match(home, away, clock.instant());
            validateNotCurrentlyPlaying(home);
            validateNotCurrentlyPlaying(away);
            items.put(match, new Score(0, 0));
            return match;
        }
    }

    private void validateNotCurrentlyPlaying(Team team) {
        var currentlyPlayingMatch = items.keySet()
          .stream()
          .filter(m -> m.home().equals(team) || m.away().equals(team))
          .findAny();
        if (currentlyPlayingMatch.isPresent()) {
            throw new IllegalArgumentException(team.name() + " is currently playing");
        }
    }

    @Override
    public void updateScore(Match match, Score score) {

    }

    @Override
    public void finish(Match match) {

    }

    @Override
    public List<String> getSummary() {
        return items.entrySet()
          .stream()
          .map(this::asSummary)
          .collect(toList());
    }

    private String asSummary(Map.Entry<Match, Score> item) {
        var match = item.getKey();
        var score = item.getValue();
        return String.join(" ",
          match.home().name(),
          Integer.toString(score.home()),
          "-",
          match.away().name(),
          Integer.toString(score.away()));
    }
}
