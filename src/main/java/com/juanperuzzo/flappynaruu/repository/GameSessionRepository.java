package com.juanperuzzo.flappynaruu.repository;

import com.juanperuzzo.flappynaruu.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
