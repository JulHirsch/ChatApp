package common.Encryption;

public interface IEncryptionService<Key extends IKey> {
    String encrypt(String plainText, Key key);

    String decrypt(String cipherText, Key key);
}
