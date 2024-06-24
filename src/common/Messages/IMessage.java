package common.Messages;

public interface IMessage {
    MessageType getMessageType();

    String toJson();
}
