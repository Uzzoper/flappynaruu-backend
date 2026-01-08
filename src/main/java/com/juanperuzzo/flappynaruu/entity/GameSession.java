package com.juanperuzzo.flappynaruu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "game_session")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

    public GameSession() {
    }

    public GameSession(String nickname, Integer score) {
        this.nickname = nickname;
        this.score = score;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getScore() {
        return score;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
