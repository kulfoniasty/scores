package tk.musial.scores.livescoreboard;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.fest.assertions.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class InMemoryScoreBoardTest {
    private final InMemoryScoreBoard scoreBoard = new InMemoryScoreBoard();

    @Nested
    class Start {

        private ActiveMatch match;

        @BeforeEach
        void startMatch() {
            match = scoreBoard.start(new Match(new Team("Poland"), new Team("Moldova")));
        }

        @Test
        void startsWithInitialScore() {
            assertThat(match.getScore()).isEqualTo(new Score(0, 0));
        }

        @Test
        void matchIsVisibleInTheSummary() {
            assertThat(scoreBoard.getSummary()).contains("Poland 0 - Moldova 0");
        }

    }
}