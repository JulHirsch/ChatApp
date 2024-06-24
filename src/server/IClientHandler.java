package server;

import common.Messages.BaseMessage;

public interface IClientHandler {
    void sendMessage(BaseMessage message);

    String getName();
}
