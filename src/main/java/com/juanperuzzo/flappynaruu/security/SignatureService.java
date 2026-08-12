package com.juanperuzzo.flappynaruu.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;

/**
 * Verifies that a score submission was produced by the game client and not forged
 * by a direct API call. The client signs {@code nickname:score:timestamp} with a
 * shared secret (HmacSHA256); the server recomputes and compares in constant time.
 */
@Service
public class SignatureService {

    private static final Duration MAX_AGE = Duration.ofMinutes(5);
    private static final long FUTURE_SKEW_MILLIS = 30_000L;

    private final String secret;

    public SignatureService(@Value("${LEADERBOARD_SECRET}") String secret) {
        this.secret = secret;
    }

    public void verify(String nickname, int score, long timestamp, String signature) {
        long now = Instant.now().toEpochMilli();

        if (timestamp > now + FUTURE_SKEW_MILLIS) {
            throw new InvalidScoreSignatureException("Timestamp is in the future");
        }
        if (now - timestamp > MAX_AGE.toMillis()) {
            throw new InvalidScoreSignatureException("Score submission expired");
        }

        String expected = sign(nickname, score, timestamp);
        if (!constantTimeEquals(expected, signature)) {
            throw new InvalidScoreSignatureException("Invalid score signature");
        }
    }

    public String sign(String nickname, int score, long timestamp) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String payload = nickname + ":" + score + ":" + timestamp;
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return hex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute score signature", e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        byte[] ab = a.getBytes(StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(ab, bb);
    }
}
