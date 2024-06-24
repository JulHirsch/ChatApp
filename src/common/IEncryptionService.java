package common;

//TODO either one for symmetric and one for asymmetric or just take the part of the key needed - later probably easier
public interface IEncryptionService<Key extends IKey> {
    String encrypt(String plainText, Key key);
    String decrypt(String cipherText, Key key);
}
