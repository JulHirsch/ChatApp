package common;

import common.Encryption.CaesarEncryptionService;
import common.Encryption.CaesarKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CaesarEncryptionServiceTest {

    private CaesarEncryptionService encryptionService;
    private CaesarKey key;

    @BeforeEach
    public void setUp() {
        encryptionService = new CaesarEncryptionService();
        key = new CaesarKey(3); // Beispiel für einen Shift von 3
    }

    @Test
    public void testEncrypt_withUpperCaseLetters() {
        String plainText = "HELLO";
        String encryptedText = encryptionService.encrypt(plainText, key);
        assertEquals("KHOOR", encryptedText);
    }

    @Test
    public void testEncrypt_withLowerCaseLetters() {
        String plainText = "hello";
        String encryptedText = encryptionService.encrypt(plainText, key);
        assertEquals("khoor", encryptedText);
    }

    @Test
    public void testEncrypt_withMixedCaseLetters() {
        String plainText = "HelloWorld";
        String encryptedText = encryptionService.encrypt(plainText, key);
        assertEquals("KhoorZruog", encryptedText);
    }

    @Test
    public void testEncrypt_withNonAlphabeticCharacters() {
        String plainText = "Hello, World!";
        String encryptedText = encryptionService.encrypt(plainText, key);
        assertEquals("Khoor, Zruog!", encryptedText);
    }

    @Test
    public void testEncrypt_withNullInput() {
        String plainText = null;
        String encryptedText = encryptionService.encrypt(plainText, key);
        assertEquals("", encryptedText);
    }

    @Test
    public void testEncrypt_wrapAroundWithinAlphabet() {
        String plainText = "xyz";
        String encryptedText = encryptionService.encrypt(plainText, key);
        assertEquals("abc", encryptedText);
    }

    @Test
    public void testDecrypt_withUpperCaseLetters() {
        String cipherText = "KHOOR";
        String decryptedText = encryptionService.decrypt(cipherText, key);
        assertEquals("HELLO", decryptedText);
    }

    @Test
    public void testDecrypt_withLowerCaseLetters() {
        String cipherText = "khoor";
        String decryptedText = encryptionService.decrypt(cipherText, key);
        assertEquals("hello", decryptedText);
    }

    @Test
    public void testDecrypt_withMixedCaseLetters() {
        String cipherText = "KhoorZruog";
        String decryptedText = encryptionService.decrypt(cipherText, key);
        assertEquals("HelloWorld", decryptedText);
    }

    @Test
    public void testDecrypt_withNonAlphabeticCharacters() {
        String cipherText = "Khoor, Zruog!";
        String decryptedText = encryptionService.decrypt(cipherText, key);
        assertEquals("Hello, World!", decryptedText);
    }

    @Test
    public void testDecrypt_withNullInput() {
        String cipherText = null;
        String decryptedText = encryptionService.decrypt(cipherText, key);
        assertEquals("", decryptedText);
    }

    @Test
    public void testDecrypt_wrapAroundWithinAlphabet() {
        String cipherText = "abc";
        String decryptedText = encryptionService.decrypt(cipherText, key);
        assertEquals("xyz", decryptedText);
    }
}