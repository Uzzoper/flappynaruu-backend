package com.juanperuzzo.flappynaruu.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Set;
import java.util.stream.Collectors;

public class BadWordsValidator implements ConstraintValidator<NoBadWords, String> {

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
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("bad-words.txt"),
                        StandardCharsets.UTF_8))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(this::normalize)
                    .collect(Collectors.toUnmodifiableSet());
        } catch (Exception e) {
            throw new IllegalStateException("Error loading bad-words.txt", e);
        }
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