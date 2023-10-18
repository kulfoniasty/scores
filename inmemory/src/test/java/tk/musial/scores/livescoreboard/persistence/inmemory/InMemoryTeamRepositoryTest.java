package tk.musial.scores.livescoreboard.persistence.inmemory;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tk.musial.scores.livescoreboard.domain.Team;
import tk.musial.scores.livescoreboard.persistence.TeamRepository.DuplicateTeamException;

class InMemoryTeamRepositoryTest {

  private final InMemoryTeamRepository repository = new InMemoryTeamRepository();

  @Nested
  class Add {

    @Test
    void canBeInvokedConcurrently() throws Exception {
      Team home = new Team("Germany");
      Team away = new Team("France");

      AtomicLong exceptionCount = new AtomicLong(0);
      Runnable inserter =
          () -> {
            try {
              repository.add(home, away);
            } catch (DuplicateTeamException e) {
              exceptionCount.incrementAndGet();
            }
          };

      runConcurrently(inserter);

      assertThat(exceptionCount.get()).isEqualTo(9999);
    }

    private void runConcurrently(Runnable inserter)
        throws InterruptedException, ExecutionException {
      ExecutorService threadExecutor = Executors.newFixedThreadPool(200);

      List<Future<?>> tasks =
          LongStream.range(0, 10000)
              .parallel()
              .mapToObj(i -> threadExecutor.submit(inserter))
              .collect(toList());

      awaitAll(tasks);
    }

    private void awaitAll(List<Future<?>> tasks) throws InterruptedException, ExecutionException {
      for (Future<?> t : tasks) {
        t.get();
      }
    }
  }

  @Nested
  class Remove {

    @Test
    void removes_canBeAddedAgain() {
      Team home = new Team("Poland");
      Team away = new Team("Slovakia");
      repository.add(home, away);

      repository.remove(home, away);

      repository.add(home, away);
    }
  }
}
