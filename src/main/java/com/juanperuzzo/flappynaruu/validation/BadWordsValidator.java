package com.juanperuzzo.flappynaruu.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class BadWordsValidator implements ConstraintValidator<NoBadWords, String> {

    private static final String BAD_WORDS_ENV = "BAD_WORDS";

    private Set<String> badWords;

    @Override
    public void initialize(NoBadWords constraintAnnotation) {
        this.badWords = loadBadWords();
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext context) {
        if (text == null || text.isBlank()) {
            return true;
        }

        String collapsed = normalize(text);
        if (badWords.contains(collapsed)) {
            return false;
        }

        String[] words = text.split("\\s+");
        for (String word : words) {
            String normalizedWord = normalize(word);
            if (badWords.contains(normalizedWord)) {
                return false;
            }
        }

        return true;
    }

    private Set<String> loadBadWords() {
        String envBadWords = System.getenv(BAD_WORDS_ENV);
        if (envBadWords == null || envBadWords.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(envBadWords.split(","))
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .map(this::normalize)
                .collect(Collectors.toUnmodifiableSet());
    }

    private String normalize(String text) {
        return Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("(.)\\1+", "$1")
                .replaceAll("\\s+", "")
                .replaceAll("[0@]", "a")
                .replaceAll("[3]", "e")
                .replaceAll("[1!|]", "i")
                .replaceAll("[4]", "a")
                .replaceAll("[5$]", "s")
                .replaceAll("[7]", "t")
                .replaceAll("[8]", "b")
                .replaceAll("[^a-z]", "");
    }
}