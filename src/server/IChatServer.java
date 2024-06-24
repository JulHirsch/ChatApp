package server;

import common.Message;

public interface IChatServer {
    void removeClient(IClientHandler client);

    void sendMessage(Message message);
}
