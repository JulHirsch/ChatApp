package client;

import common.Messages.KeyExchangeMessage;
import common.Messages.TextMessage;

public interface IChatClient {
    void appendTextMessage(TextMessage message);

    void appendKeyExchangeMessage(KeyExchangeMessage keyExchangeMessage);
}
