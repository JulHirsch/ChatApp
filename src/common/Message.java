package common;

import com.google.gson.Gson;

public class Message {
    public static final String GLOBAL_RECEIVER = "global";
    private static final Gson gson = new Gson();
    private final String _sender;
    private final String _customName;
    private final String _text;
    private final String _receiver;
    private final String _encryptionType;
    private final String _encryptionKey;

    // Constructor with all fields
    public Message(String text, String customName, String receiver, String encryptionType, String encryptionKey) {
        this(text, "", customName, receiver, encryptionType, encryptionKey);
    }

    // Constructor with all fields including sender
    public Message(String text, String sender, String customName, String receiver, String encryptionType, String encryptionKey) {
        this._text = text != null ? text : "";
        this._sender = sender != null ? sender : "";
        this._customName = customName != null ? customName : "";
        this._receiver = receiver != null ? receiver : GLOBAL_RECEIVER;
        this._encryptionType = encryptionType != null ? encryptionType : "";
        this._encryptionKey = encryptionKey != null ? encryptionKey : "";
    }

    // Deserialize from JSON
    public static Message fromJson(String jsonString) {
        return gson.fromJson(jsonString, Message.class);
    }

    // Getters
    public String getSender() {
        return _sender;
    }

    public String getCustomName() {
        return _customName;
    }

    public String getText() {
        return _text;
    }

    public String getReceiver() {
        return _receiver;
    }

    public String getEncryptionType() {
        return _encryptionType;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }

    public boolean isGlobal() {
        return _receiver.equals(Message.GLOBAL_RECEIVER);
    }

    // Serialize to JSON
    public String toJson() {
        return gson.toJson(this);
    }
}
