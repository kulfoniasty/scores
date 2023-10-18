package tk.musial.scores.livescoreboard.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.musial.scores.livescoreboard.persistence.TeamRepository;
import tk.musial.scores.livescoreboard.persistence.TeamRepository.DuplicateTeamException;

@ExtendWith(MockitoExtension.class)
class WorldCupTest {

  private static final Instant NOW = Instant.ofEpochSecond(1697580088);
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

  @Mock private MatchStateObserver worldCupObserver;
  @Mock private TeamRepository teamRepository;

  private WorldCup worldCup;

  @BeforeEach
  void prepare() {
    worldCup = new WorldCup(CLOCK, teamRepository);
    worldCup.observe(worldCupObserver);
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class StartMatch {

    @ParameterizedTest
    @MethodSource("teamPairs")
    void startsWithInitialScore(String home, String away) {
      Match actual = worldCup.startMatch(new Team(home), new Team(away));

      assertThat(actual)
          .isEqualTo(
              new ObservableMatch(
                  new Team(home), new Team(away), new Score(0, 0), NOW, null, List.of(worldCup)));
    }

    @Test
    void notifiesObservers() {
      Match match = worldCup.startMatch(new Team("Colombia"), new Team("Chile"));

      verify(worldCupObserver).notify(match);
    }

    @Test
    void throwsException_whenRepoRejectsSave() {
      Team home = new Team("Australia");
      Team away = new Team("Spain");
      DuplicateTeamException storageException =
          new DuplicateTeamException("Australia exists already");
      doThrow(storageException).when(teamRepository).add(home, away);

      assertThatExceptionOfType(DuplicateTeamException.class)
          .isThrownBy(() -> worldCup.startMatch(home, away))
          .isEqualTo(storageException);
    }

    static Stream<Arguments> teamPairs() {
      return Stream.of(
          Arguments.of("Poland", "Moldova"),
          Arguments.of("Germany", "France"),
          Arguments.of("Italy", "Greece"),
          Arguments.of("Italy2", "Greece2"),
          Arguments.of("Italy4", "Greece3"),
          Arguments.of("Italy22", "Gre2ece3"),
          Arguments.of("Italy232", "Gre32ece3"),
          Arguments.of("Ital2y232", "G2re32ece3"));
    }
  }

  @Nested
  class NotifyFinish {

    @ParameterizedTest
    @MethodSource("teams")
    void removes_thusTheSameTeamCanPlayAgain(List<Team> teams) {
      Match first = worldCup.startMatch(teams.get(0), teams.get(1));

      first.finish(Instant.now());

      worldCup.startMatch(first.getHome(), first.getAway());
    }

    @ParameterizedTest
    @MethodSource("teams")
    void forwardsNotification(List<Team> teams) {
      Match first = worldCup.startMatch(teams.get(0), teams.get(1));

      FinishedMatch actual = first.finish(Instant.now());

      verify(worldCupObserver).notify(actual);
    }

    static Stream<List<Team>> teams() {
      return Stream.of(
          List.of(new Team("Poland"), new Team("Brazil")),
          List.of(new Team("Australia"), new Team("Italy")),
          List.of(new Team("USA"), new Team("Argentina")));
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class Observe {

    @Mock private MatchStateObserver scoreBoard;
    @Mock private MatchStateObserver newsFeed;

    @Test
    void addsObservers_worldCupMakesSureUpdatesArePropagatedForExistingMatches() {
      worldCup.observe(scoreBoard);
      Match startedMatch = worldCup.startMatch(new Team("Finland"), new Team("Sweden"));
      worldCup.observe(newsFeed);

      Match updatedMatch = startedMatch.updateScore(new Score(1, 0));

      verify(scoreBoard).notify(startedMatch);
      verify(scoreBoard).notify(updatedMatch);
    }
  }
}
