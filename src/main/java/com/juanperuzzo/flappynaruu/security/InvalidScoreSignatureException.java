package com.juanperuzzo.flappynaruu.security;

public class InvalidScoreSignatureException extends RuntimeException {

    public InvalidScoreSignatureException(String message) {
        super(message);
    }
}
