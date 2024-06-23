package server;

import common.Message;

public interface IClientHandler {
    void sendMessage(Message message);
    String getName();
}
