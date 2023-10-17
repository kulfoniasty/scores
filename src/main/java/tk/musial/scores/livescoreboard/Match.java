package tk.musial.scores.livescoreboard;

import java.time.Instant;

public record Match(Team home, Team away, Instant startTime) {
}
