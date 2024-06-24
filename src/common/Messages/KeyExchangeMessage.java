package common.Messages;

public class KeyExchangeMessage extends BaseMessage {
    private final String _encryptionType;
    private final String _encryptionKey;

    public KeyExchangeMessage(String sender, String receiver, String customName, String encryptionType, String encryptionKey) {
        super(MessageType.KEY_EXCHANGE, sender, receiver, customName);
        this._encryptionType = encryptionType;
        this._encryptionKey = encryptionKey;
    }

    public String getEncryptionType() {
        return _encryptionType;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }
}