package common.Messages;

import com.google.gson.Gson;

public abstract class BaseMessage implements IMessage {
    public static final String GLOBAL_RECEIVER = "global";
    private static final Gson _gson = new Gson();

    private final MessageType _messageType;
    private final String _receiver;
    private final String _customName;
    private String _sender;

    public BaseMessage(MessageType messageType, String sender, String receiver, String customName) {
        this._messageType = messageType;
        this._sender = sender != null ? sender : "";
        this._receiver = receiver != null ? receiver : GLOBAL_RECEIVER;
        this._customName = customName != null ? customName : "";
    }

    public MessageType getMessageType() {
        return _messageType;
    }

    public String getSender() {
        return _sender;
    }

    public void setSender(String sender) {
        this._sender = sender;
    }

    public String getReceiver() {
        return _receiver;
    }

    public boolean isGlobal() {
        return _receiver.equals(BaseMessage.GLOBAL_RECEIVER);
    }

    public String getCustomName() {
        return _customName;
    }

    public String toJson() {
        return _gson.toJson(this);
    }
}