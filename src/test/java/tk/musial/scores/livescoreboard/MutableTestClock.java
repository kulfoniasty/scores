package tk.musial.scores.livescoreboard;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public final class MutableTestClock extends Clock {

  private Instant instant;

  public MutableTestClock(Instant now) {
    this.instant = now;
  }

  public void tickMinutes(int minutes) {
    this.instant = this.instant.plusSeconds(minutes * 60);
  }

  @Override
  public ZoneId getZone() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Clock withZone(ZoneId zone) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Instant instant() {
    return instant;
  }
}
