package client;

import common.Messages.IMessage;

public interface IConnectionManager {
    void setChatClient(IChatClient chatClient);

    void start();

    void sendMessage(IMessage message);
}
