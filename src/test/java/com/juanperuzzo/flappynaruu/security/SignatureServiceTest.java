package com.juanperuzzo.flappynaruu.security;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignatureServiceTest {

    private final SignatureService service = new SignatureService("test-secret");

    @Test
    public void shouldAcceptValidSignature() {
        long ts = Instant.now().toEpochMilli();
        String signature = service.sign("Uzzoper", 120, ts);

        assertDoesNotThrow(() -> service.verify("Uzzoper", 120, ts, signature));
    }

    @Test
    public void shouldRejectTamperedScore() {
        long ts = Instant.now().toEpochMilli();
        String signature = service.sign("Uzzoper", 120, ts);

        assertThrows(InvalidScoreSignatureException.class,
                () -> service.verify("Uzzoper", 999, ts, signature));
    }

    @Test
    public void shouldRejectTamperedNickname() {
        long ts = Instant.now().toEpochMilli();
        String signature = service.sign("Uzzoper", 120, ts);

        assertThrows(InvalidScoreSignatureException.class,
                () -> service.verify("Hacker", 120, ts, signature));
    }

    @Test
    public void shouldRejectExpiredTimestamp() {
        long ts = Instant.now().minus(java.time.Duration.ofMinutes(10)).toEpochMilli();
        String signature = service.sign("Uzzoper", 120, ts);

        assertThrows(InvalidScoreSignatureException.class,
                () -> service.verify("Uzzoper", 120, ts, signature));
    }

    @Test
    public void shouldAcceptTimestampWithinSkew() {
        long ts = Instant.now().plus(java.time.Duration.ofSeconds(10)).toEpochMilli();
        String signature = service.sign("Uzzoper", 120, ts);

        assertDoesNotThrow(() -> service.verify("Uzzoper", 120, ts, signature));
    }

    @Test
    public void shouldRejectFarFutureTimestamp() {
        long ts = Instant.now().plus(java.time.Duration.ofMinutes(5)).toEpochMilli();
        String signature = service.sign("Uzzoper", 120, ts);

        assertThrows(InvalidScoreSignatureException.class,
                () -> service.verify("Uzzoper", 120, ts, signature));
    }
}
