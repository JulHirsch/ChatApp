package server;

import common.Messages.BaseMessage;

public interface IChatServer {
    void removeClient(IClientHandler client);

    void sendMessage(BaseMessage message);
}
