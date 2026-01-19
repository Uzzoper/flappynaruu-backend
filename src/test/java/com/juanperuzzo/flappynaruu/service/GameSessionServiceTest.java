package com.juanperuzzo.flappynaruu.service;

import com.juanperuzzo.flappynaruu.entity.GameSession;
import com.juanperuzzo.flappynaruu.exception.InvalidNicknameException;
import com.juanperuzzo.flappynaruu.exception.InvalidScoreException;
import com.juanperuzzo.flappynaruu.repository.GameSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameSessionServiceTest {

    @Mock
    private GameSessionRepository repository;

    @InjectMocks
    private GameSessionService service;

    @Test
    public void shouldSaveScoreWhenTop5HasLessThanFive() {
        List<GameSession> top5 = List.of(
                new GameSession("A", 100),
                new GameSession("B", 80),
                new GameSession("C", 60),
                new GameSession("D", 40),
                new GameSession("E", 20)
        );

        when(repository.findTop5ByOrderByScoreDescCreatedAtAsc()).thenReturn(top5);

        boolean saved = service.registerScoreIfHighscore("Player", 10);

        assertFalse(saved);
        verify(repository, never()).save(any());
        verify(repository, never()).delete(any());
    }

    @Test
    public void shouldThrowExceptionWhenNicknameIsBlank() {
        assertThrows(InvalidNicknameException.class, () -> service.registerScoreIfHighscore(" ", 10));
    }

    @Test
    public void shouldThrowExceptionWhenNicknameIsNull() {
        assertThrows(InvalidNicknameException.class, () -> service.registerScoreIfHighscore(null, 10));
    }

    @Test
    public void shouldThrowExceptionWhenScoreIsNull() {
        assertThrows(InvalidScoreException.class, () -> service.registerScoreIfHighscore("player", null));
    }

    @Test
    public void shouldThrowExceptionWhenScoreIsZeroOrNegative() {
        assertThrows(InvalidScoreException.class, () -> service.registerScoreIfHighscore("player", 0));
        assertThrows(InvalidScoreException.class, () -> service.registerScoreIfHighscore("player", -5));
    }
}
