package common.Messages;

public class KeyExchangeMessage extends BaseMessage {

    private final String _encryptionKey;

    public KeyExchangeMessage(String sender, String receiver, String customName, String encryptionKey) {
        super(MessageType.KEY_EXCHANGE, sender, receiver, customName);
        this._encryptionKey = encryptionKey;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }
}