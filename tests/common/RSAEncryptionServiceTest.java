package common;

import common.Encryption.RSAEncryptionService;
import common.Encryption.RSAKeyGenerator;
import common.Encryption.RSAKeyPair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RSAEncryptionServiceTest {

    @Test
    public void testEncryptionDecryption2048() {
        RSAKeyPair keyPair = RSAKeyGenerator.generateKeyPair(2048);
        testEncryptionDecryption(keyPair);
    }

    @Test
    public void testEncryptionDecryption3072() {
        RSAKeyPair keyPair = RSAKeyGenerator.generateKeyPair(3072);
        testEncryptionDecryption(keyPair);
    }

    @Test
    public void testEncryptionDecryption4096() {
        RSAKeyPair keyPair = RSAKeyGenerator.generateKeyPair(4096);
        testEncryptionDecryption(keyPair);
    }

    private void testEncryptionDecryption(RSAKeyPair keyPair) {
        RSAEncryptionService encryptionService = new RSAEncryptionService();
        String plainText = "Hello, RSA! This is a test message.";

        String encryptedText = encryptionService.encrypt(plainText, keyPair.getPublicKey());
        String decryptedText = encryptionService.decrypt(encryptedText, keyPair.getPrivateKey());

        assertNotNull(encryptedText);
        assertNotNull(decryptedText);
        assertEquals(plainText, decryptedText);
    }

    @Test
    public void testEmptyString() {
        RSAKeyPair keyPair = RSAKeyGenerator.generateKeyPair(2048);
        RSAEncryptionService encryptionService = new RSAEncryptionService();
        String plainText = "";

        String encryptedText = encryptionService.encrypt(plainText, keyPair.getPublicKey());
        String decryptedText = encryptionService.decrypt(encryptedText, keyPair.getPrivateKey());

        assertNotNull(encryptedText);
        assertNotNull(decryptedText);
        assertEquals(plainText, decryptedText);
    }

    @Test
    public void testSpecialCharacters() {
        RSAKeyPair keyPair = RSAKeyGenerator.generateKeyPair(2048);
        RSAEncryptionService encryptionService = new RSAEncryptionService();
        String plainText = "Special characters: !@#$%^&*()_+-=~`<>?,./;:'\"[]{}|\\ ";

        String encryptedText = encryptionService.encrypt(plainText, keyPair.getPublicKey());
        String decryptedText = encryptionService.decrypt(encryptedText, keyPair.getPrivateKey());

        assertNotNull(encryptedText);
        assertNotNull(decryptedText);
        assertEquals(plainText, decryptedText);
    }
}