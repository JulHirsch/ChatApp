package common.Encryption;

public class EncryptionInfo {
    private final EncryptionType _type;
    private final IKey _symmetricKey;
    private final RSAKeyPair _myKeyPair;
    private RSAPublicKey _otherPublicKey;

    // Constructor for no encryption
    public EncryptionInfo(EncryptionType encryptionType) {
        this._type = encryptionType;
        this._symmetricKey = null;
        this._myKeyPair = null;
        this._otherPublicKey = null;
    }

    // Constructor for symmetric encryption
    public EncryptionInfo(EncryptionType encryptionType, IKey encryptionKey) {
        this._type = encryptionType;
        this._symmetricKey = encryptionKey;
        this._myKeyPair = null;
        this._otherPublicKey = null;
    }

    // Constructor for RSA encryption with both keys
    public EncryptionInfo(EncryptionType encryptionType, RSAKeyPair myKeyPair) {
        this._type = encryptionType;
        this._symmetricKey = null;
        this._myKeyPair = myKeyPair;
        this._otherPublicKey = null;
    }

    public EncryptionInfo(EncryptionType encryptionType, RSAKeyPair myKeyPair, RSAPublicKey otherPublicKey) {
        this._type = encryptionType;
        this._symmetricKey = null;
        this._myKeyPair = myKeyPair;
        this._otherPublicKey = otherPublicKey;
    }

    public EncryptionType getType() {
        return _type;
    }

    public IKey getSymmetricKey() {
        return _symmetricKey;
    }

    public RSAKeyPair getOwnKeyPair(){
        return _myKeyPair;
    }

    public RSAPublicKey getOtherPublicKey(){
        return _otherPublicKey;
    }

    public void setOtherPublicKey(RSAPublicKey key){
        this._otherPublicKey = key;
    }
}
