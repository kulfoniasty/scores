package tk.musial.scores.livescoreboard.domain;

import static org.apache.commons.lang3.StringUtils.capitalize;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Team {

  private final String name;

  public String name() {
    return name;
  }

  public Team(String name) {
    this.name = capitalize(name.toLowerCase());
  }
}
