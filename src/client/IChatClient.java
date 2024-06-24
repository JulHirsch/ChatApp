package client;

import common.Messages.TextMessage;

public interface IChatClient {
    void appendMessage(TextMessage message);
}
