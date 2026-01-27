package com.juanperuzzo.flappynaruu.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BadWordsValidatorTest {

    private BadWordsValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new BadWordsValidator();
        validator.initialize(null); // Load bad words
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldBlockExplicitBadWords() {
        assertFalse(validator.isValid("merda", context), "Should block 'merda'");
        assertFalse(validator.isValid("CARALHO", context), "Should block 'CARALHO'");
        assertFalse(validator.isValid("b.o.s.t.a", context), "Should block 'b.o.s.t.a' (ignoring special chars)");
    }

    @Test
    void shouldAllowCommonNames() {
        // "Lucas" contains "cu" (bad word)
        assertTrue(validator.isValid("Lucas", context), "Should allow 'Lucas'");

        // "Paulo" contains "pau" (bad word)
        assertTrue(validator.isValid("Paulo", context), "Should allow 'Paulo'");

        // "Computador" contains "puta" (bad word)
        assertTrue(validator.isValid("Computador", context), "Should allow 'Computador'");
    }

    @Test
    void shouldBlockSpacedBadWords() {
        // This is what the current implementation works well for, but likely fails
        // 'Lucas'
        assertFalse(validator.isValid("m e r d a", context), "Should block 'm e r d a'");
    }
}