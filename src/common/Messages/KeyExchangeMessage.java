package common.Messages;

import common.Encryption.EncryptionType;

public class KeyExchangeMessage extends BaseMessage {

    private final String _encryptionKey;

    public KeyExchangeMessage(String sender, String receiver, String customName, String encryptionKey, EncryptionType encryptionType) {
        super(MessageType.KEY_EXCHANGE, sender, receiver, customName, encryptionType);
        this._encryptionKey = encryptionKey;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }
}