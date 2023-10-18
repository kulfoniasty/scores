package tk.musial.scores.livescoreboard;

import static java.util.stream.Collectors.toList;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;

public class InMemoryScoreBoard implements ScoreBoard {

  private final Clock clock;
  private final Map<Match, Score> items = new HashMap<>();
  private final Object updateLock = new Object();

  public InMemoryScoreBoard(Clock clock) {
    this.clock = clock;
  }

  @Override
  public Match startMatch(Team home, Team away) {
    synchronized (updateLock) {
      var match = new Match(home, away, clock.instant());
      validateNotCurrentlyPlaying(home);
      validateNotCurrentlyPlaying(away);
      items.put(match, new Score(0, 0));
      return match;
    }
  }

  private void validateNotCurrentlyPlaying(Team team) {
    var currentlyPlayingMatch =
        items.keySet().stream()
            .filter(m -> m.getHome().equals(team) || m.getAway().equals(team))
            .findAny();
    if (currentlyPlayingMatch.isPresent()) {
      throw new IllegalArgumentException(team.name() + " is currently playing");
    }
  }

  @Override
  public void updateScore(Match match, Score score) {
    synchronized (updateLock) {
      var currentScore = items.get(match);
      Validate.isTrue(currentScore != null, "Match not found");
      items.put(match, currentScore.changeTo(score));
    }
  }

  @Override
  public void finish(Match match) {
    synchronized (updateLock) {
      var result = items.remove(match);
      Validate.isTrue(result != null, "Match not found");
    }
  }

  @Override
  public List<String> getSummary() {
    return items.entrySet().stream()
        .sorted(this::descByTotalScoreThenMostRecentFirst)
        .map(this::asSummary)
        .collect(toList());
  }

  private int descByTotalScoreThenMostRecentFirst(
      Map.Entry<Match, Score> first, Map.Entry<Match, Score> second) {
    var firstScore = first.getValue();
    var secondScore = second.getValue();
    var scoreComparison = secondScore.compareTo(firstScore);

    var firstMatchStart = first.getKey().getStartTime();
    var secondMatchStart = second.getKey().getStartTime();
    var timeComparison = secondMatchStart.compareTo(firstMatchStart);
    return scoreComparison == 0 ? timeComparison : scoreComparison;
  }

  private String asSummary(Map.Entry<Match, Score> item) {
    var match = item.getKey();
    var score = item.getValue();
    return String.join(
        " ",
        match.getHome().name(),
        Integer.toString(score.getHome()),
        "-",
        match.getAway().name(),
        Integer.toString(score.getAway()));
  }
}
