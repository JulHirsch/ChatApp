package common.Encryption;

public class EncryptionInfo {
    private final EncryptionType _type;
    private final IKey _key;

    public EncryptionInfo(EncryptionType encryptionType) {
        this._type = encryptionType;
        this._key = null;
    }

    public EncryptionInfo(EncryptionType encryptionType, IKey encryptionKey) {
        this._type = encryptionType;
        this._key = encryptionKey;
    }

    public EncryptionType getType() {
        return _type;
    }

    public IKey getKey() {
        return _key;
    }
}
