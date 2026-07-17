package com.engineering_lab.hunger.common.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValidatorTest {

    @Test
    void requireTextTrimsBeforeCheckingAndReturningValue() {
        assertEquals("Hunger", Validator.requireText("  Hunger  ", "name", 6));
    }

    @Test
    void requireTextRejectsBlankAndInvalidMaximumLength() {
        assertThrows(IllegalArgumentException.class,
                () -> Validator.requireText("   ", "name", 10));
        assertThrows(IllegalArgumentException.class,
                () -> Validator.requireText("value", "name", 0));
    }

    @Test
    void requireEmailTrimsAndValidatesAddress() {
        assertEquals("User@example.com", Validator.requireEmail("  User@example.com  "));
        assertThrows(IllegalArgumentException.class,
                () -> Validator.requireEmail("user@.example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> Validator.requireEmail("user..name@example.com"));
    }

    @Test
    void normalizeEmailUsesStableLowercaseNormalization() {
        assertEquals("user@example.com", Validator.normalizeEmail("USER@EXAMPLE.COM"));
    }

    @Test
    void requireUuidV7RejectsOtherUuidVersions() {
        UUID uuidV7 = UUID.fromString("01890f9a-6b7c-7def-8123-456789abcdef");

        assertSame(uuidV7, Validator.requireUuidV7(uuidV7, "id"));
        assertThrows(IllegalArgumentException.class,
                () -> Validator.requireUuidV7(UUID.randomUUID(), "id"));
    }

    @Test
    void requireNotBeforeChecksValueReferenceAndOrdering() {
        Instant reference = Instant.parse("2026-01-01T00:00:00Z");

        assertEquals(reference, Validator.requireNotBefore(reference, reference, "updatedAt"));
        assertThrows(IllegalArgumentException.class,
                () -> Validator.requireNotBefore(reference.minusSeconds(1), reference, "updatedAt"));
        assertThrows(NullPointerException.class,
                () -> Validator.requireNotBefore(reference, null, "updatedAt"));
    }

    @Test
    void normalizeTenantCodeTrimsUppercasesAndRestrictsCharacters() {
        assertEquals("HN_01", Validator.normalizeTenantCode(" hn_01 "));
        assertThrows(IllegalArgumentException.class,
                () -> Validator.normalizeTenantCode("HN 01"));
    }
}
