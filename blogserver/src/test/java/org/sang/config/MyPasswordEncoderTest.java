package org.sang.config;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.*;

class MyPasswordEncoderTest {

    private final MyPasswordEncoder passwordEncoder = new MyPasswordEncoder();

    @Test
    void encode_shouldReturnMd5HashForValidInput() {
        String rawPassword = "password123";
        String encoded = passwordEncoder.encode(rawPassword);
        String expected = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        assertEquals(expected, encoded);
    }

    @Test
    void encode_shouldReturnMd5HashForEmptyString() {
        String rawPassword = "";
        String encoded = passwordEncoder.encode(rawPassword);
        String expected = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        assertEquals(expected, encoded);
    }

    @Test
    void encode_shouldReturnMd5HashForSpecialCharacters() {
        String rawPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String encoded = passwordEncoder.encode(rawPassword);
        String expected = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        assertEquals(expected, encoded);
    }

    @Test
    void encode_shouldReturnMd5HashForUnicodeCharacters() {
        String rawPassword = "密码 123";
        String encoded = passwordEncoder.encode(rawPassword);
        String expected = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        assertEquals(expected, encoded);
    }

    @Test
    void encode_shouldReturnMd5HashForLongPassword() {
        String rawPassword = "a".repeat(1000);
        String encoded = passwordEncoder.encode(rawPassword);
        String expected = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        assertEquals(expected, encoded);
    }

    @Test
    void encode_shouldReturnConsistentHashForSameInput() {
        String rawPassword = "testPassword";
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);
        assertEquals(encoded1, encoded2);
    }

    @Test
    void encode_shouldReturnDifferentHashForDifferentInputs() {
        String encoded1 = passwordEncoder.encode("password1");
        String encoded2 = passwordEncoder.encode("password2");
        assertNotEquals(encoded1, encoded2);
    }

    @Test
    void encode_shouldReturn32CharacterHexHash() {
        String rawPassword = "test";
        String encoded = passwordEncoder.encode(rawPassword);
        assertEquals(32, encoded.length());
        assertTrue(encoded.matches("[a-f0-9]+"));
    }

    @Test
    void encode_shouldHandleNullAsEmptyString() {
        String encoded = passwordEncoder.encode(null);
        String expected = DigestUtils.md5DigestAsHex("".getBytes());
        assertEquals(expected, encoded);
    }

    @Test
    void matches_shouldReturnTrueWhenRawPasswordMatchesEncodedPassword() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void matches_shouldReturnFalseWhenRawPasswordDoesNotMatchEncodedPassword() {
        String rawPassword = "password123";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword));
    }

    @Test
    void matches_shouldReturnFalseWhenEncodedPasswordIsNull() {
        String rawPassword = "password123";
        assertFalse(passwordEncoder.matches(rawPassword, null));
    }

    @Test
    void matches_shouldReturnTrueForEmptyPassword() {
        String rawPassword = "";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void matches_shouldReturnTrueForSpecialCharacters() {
        String rawPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void matches_shouldReturnTrueForUnicodeCharacters() {
        String rawPassword = "密码 123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void matches_shouldReturnFalseForCaseSensitivity() {
        String rawPassword = "Password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertFalse(passwordEncoder.matches("password", encodedPassword));
    }

    @Test
    void matches_shouldHandleCharSequenceInput() {
        StringBuilder rawPassword = new StringBuilder("testPassword");
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
