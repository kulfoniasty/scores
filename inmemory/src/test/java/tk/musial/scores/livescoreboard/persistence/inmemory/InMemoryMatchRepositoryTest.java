package tk.musial.scores.livescoreboard.persistence.inmemory;

import static org.assertj.core.api.Assertions.assertThat;
import static tk.musial.scores.livescoreboard.domain.ObservableMatchFixture.startedMatch;
import static tk.musial.scores.livescoreboard.persistence.MatchRepository.MatchKey.matchKey;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tk.musial.scores.livescoreboard.domain.Match;
import tk.musial.scores.livescoreboard.domain.Score;
import tk.musial.scores.livescoreboard.domain.Team;

class InMemoryMatchRepositoryTest {

  private static final String MATCHES_METHOD =
      "tk.musial.scores.livescoreboard.persistence.inmemory.InMemoryMatchRepositoryTest#matches";

  private final InMemoryMatchRepository repository = new InMemoryMatchRepository();

  @ParameterizedTest
  @MethodSource(MATCHES_METHOD)
  void storesNewMatches(Match match) {
    repository.put(matchKey(match), match);

    assertThat(repository.getAll()).containsOnly(match);
  }

  @ParameterizedTest
  @MethodSource(MATCHES_METHOD)
  void overwrites(Match match) {
    repository.put(matchKey(match), match);

    var updated = match.updateScore(new Score(2, 0));
    repository.put(matchKey(updated), updated);

    assertThat(repository.getAll()).containsOnly(updated);
  }

  @ParameterizedTest
  @MethodSource(MATCHES_METHOD)
  void removes(Match match) {
    var theOtherMatch =
        startedMatch().home(new Team("Georgia")).away(new Team("Afganistan")).build();
    repository.put(matchKey(theOtherMatch), theOtherMatch);
    repository.put(matchKey(match), match);

    assertThat(repository.getAll()).containsOnly(match, theOtherMatch);

    repository.remove(matchKey(theOtherMatch));

    assertThat(repository.getAll()).containsOnly(match);
  }

  @SuppressWarnings("unused")
  static Stream<Match> matches() {
    return Stream.of(
        startedMatch().home(new Team("Germany")).build(),
        startedMatch().home(new Team("Sweden")).build(),
        startedMatch().home(new Team("Estonia")).build());
  }
}
