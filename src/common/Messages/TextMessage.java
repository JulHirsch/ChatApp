package common.Messages;


public class TextMessage extends BaseMessage {
    private final String _text;

    public TextMessage(String text, String receiver, String sender, String customName) {
        super(MessageType.TEXT, sender, receiver, customName);
        this._text = text;
    }

    public String getText() {
        return _text;
    }
}