package tk.musial.scores.livescoreboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TeamTest {

  @ParameterizedTest
  @MethodSource("names")
  void nameIsCapitalized(Names names) {
    var actual = new Team(names.given).name();

    assertThat(actual).isEqualTo(names.expected);
  }

  @ParameterizedTest
  @MethodSource("names")
  void equalsIsCaseInsensitive(Names names) {
    var nonCapitalized = new Team(names.given).name();
    var capitalized = new Team(names.expected).name();

    assertThat(nonCapitalized).isEqualTo(capitalized);
  }

  static Stream<Names> names() {
    return Stream.of(
        new Names("PoLaND", "Poland"),
        new Names("gERMANY", "Germany"),
        new Names("FRANCE", "France"),
        new Names("italy", "Italy"));
  }

  private record Names(String given, String expected) {}
}
