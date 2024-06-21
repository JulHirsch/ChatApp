package common;

import com.google.gson.Gson;

public class Message {
    public static final String GLOBAL_RECEIVER = "global";

    private final String _sender;
    private final String _text;
    private final String _receiver;
    private final String _encryptionType;
    private final String _encryptionKey;

    private static final Gson gson = new Gson();

    // Constructor with text only
    public Message(String text) {
        this(text, "", GLOBAL_RECEIVER, "", "");
    }

    // Constructor with text and sender
    public Message(String text, String sender) {
        this(text, sender, GLOBAL_RECEIVER, "", "");
    }

    // Constructor with text, sender, and receiver
    public Message(String text, String sender, String receiver) {
        this(text, sender, receiver, "", "");
    }

    // Constructor with all fields
    public Message(String text, String sender, String receiver, String encryptionType, String encryptionKey) {
        this._text = text != null ? text : "";
        this._sender = sender != null ? sender : "";
        this._receiver = receiver != null ? receiver : GLOBAL_RECEIVER;
        this._encryptionType = encryptionType != null ? encryptionType : "";
        this._encryptionKey = encryptionKey != null ? encryptionKey : "";
    }

    // Getters
    public String getSender() {
        return _sender;
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

    // Serialize to JSON
    public String toJson() {
        return gson.toJson(this);
    }

    // Deserialize from JSON
    public static Message fromJson(String jsonString) {
        return gson.fromJson(jsonString, Message.class);
    }
}
