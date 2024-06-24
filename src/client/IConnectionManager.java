package client;

import common.Message;

public interface IConnectionManager {
    void setChatClient(IChatClient chatClient);

    void start();

    void sendMessage(Message message);
}
