package com.isanf.expotrade.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CredentialEncryptorTest {

    private static final String TEST_KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private final CredentialEncryptor encryptor = new CredentialEncryptor(TEST_KEY);

    @Test
    void encryptsAndDecryptsCredentialWithoutKeepingPlainText() {
        String encrypted = encryptor.encrypt("broker-secret");

        assertNotEquals("broker-secret", encrypted);
        assertEquals("broker-secret", encryptor.decrypt(encrypted));
    }

    @Test
    void encryptingSameCredentialTwiceProducesDifferentCiphertext() {
        String first = encryptor.encrypt("same-secret");
        String second = encryptor.encrypt("same-secret");

        assertNotEquals(first, second);
        assertEquals("same-secret", encryptor.decrypt(first));
        assertEquals("same-secret", encryptor.decrypt(second));
    }

    @Test
    void preservesNullCredentials() {
        assertNull(encryptor.encrypt(null));
        assertNull(encryptor.decrypt(null));
    }

    @Test
    void rejectsInvalidCiphertext() {
        assertThrows(RuntimeException.class, () -> encryptor.decrypt("not-a-valid-ciphertext"));
    }
}
