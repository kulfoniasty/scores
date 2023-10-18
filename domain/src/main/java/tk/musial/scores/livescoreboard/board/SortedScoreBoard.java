package tk.musial.scores.livescoreboard.board;

import static java.util.stream.Collectors.toList;
import static tk.musial.scores.livescoreboard.persistence.MatchRepository.MatchKey.matchKey;

import java.util.List;
import lombok.AllArgsConstructor;
import tk.musial.scores.livescoreboard.domain.FinishedMatch;
import tk.musial.scores.livescoreboard.domain.Match;
import tk.musial.scores.livescoreboard.domain.MatchStateObserver;
import tk.musial.scores.livescoreboard.domain.ScoreBoard;
import tk.musial.scores.livescoreboard.persistence.MatchRepository;

@AllArgsConstructor
public class SortedScoreBoard implements ScoreBoard, MatchStateObserver {

  private final MatchRepository scoreRepository;

  @Override
  public void notify(Match match) {
    scoreRepository.put(matchKey(match), match);
  }

  @Override
  public void notify(FinishedMatch match) {
    scoreRepository.remove(matchKey(match));
  }

  @Override
  public List<String> getSummary() {
    return scoreRepository
        .getAll()
        .sorted(this::descByTotalScoreThenMostRecentFirst)
        .map(this::asSummary)
        .collect(toList());
  }

  private int descByTotalScoreThenMostRecentFirst(Match first, Match second) {
    var firstScore = first.getScore();
    var secondScore = second.getScore();
    var scoreComparison = secondScore.compareTo(firstScore);

    var firstMatchStart = first.getStartTime();
    var secondMatchStart = second.getStartTime();
    var timeComparison = secondMatchStart.compareTo(firstMatchStart);
    return scoreComparison == 0 ? timeComparison : scoreComparison;
  }

  private String asSummary(Match match) {
    return String.join(
        " ",
        match.getHome().name(),
        Integer.toString(match.getScore().getHome()),
        "-",
        match.getAway().name(),
        Integer.toString(match.getScore().getAway()));
  }
}
