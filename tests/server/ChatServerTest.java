package server;

import common.Messages.BaseMessage;
import common.Messages.TextMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class ChatServerTest {

    private ChatServer _chatServer;
    private IClientHandler _senderHandler;
    private IClientHandler _otherHandler;

    @BeforeEach
    public void setUp() throws Exception {
        _chatServer = new ChatServer(3141);
        _senderHandler = Mockito.mock(IClientHandler.class);
        _otherHandler = Mockito.mock(IClientHandler.class);
        _chatServer.start();
    }

    @AfterEach
    public void tearDown() {
        _chatServer.stop();
    }

    @Test
    public void testSendMessageToGlobal() {
        BaseMessage message = new TextMessage("Hello, world!", "global", "client1", "custom");

        List<IClientHandler> clients = _chatServer.getClients();
        clients.add(_senderHandler);
        clients.add(_otherHandler);

        when(_senderHandler.getName()).thenReturn("client1");
        when(_otherHandler.getName()).thenReturn("client2");

        _chatServer.sendMessage(message);

        verify(_otherHandler, times(1)).sendMessage(message);
        verify(_senderHandler, never()).sendMessage(any(BaseMessage.class));
    }

    @Test
    public void testSendMessageToSpecificClient() {
        BaseMessage message = new TextMessage("Hello, client2!", "client2", "client1", "custom");

        List<IClientHandler> clients = _chatServer.getClients();
        clients.add(_senderHandler);
        clients.add(_otherHandler);

        when(_senderHandler.getName()).thenReturn("client1");
        when(_otherHandler.getName()).thenReturn("client2");

        _chatServer.sendMessage(message);

        verify(_otherHandler, times(1)).sendMessage(message);
        verify(_senderHandler, never()).sendMessage(any(BaseMessage.class));
    }

    @Test
    public void testSendMessageToNonExistingClient() {
        BaseMessage message = new TextMessage("Hello, client2!", "client2", "client1", "custom");

        List<IClientHandler> clients = _chatServer.getClients();
        clients.add(_senderHandler);
        clients.add(_otherHandler);

        // Mock the client names
        when(_senderHandler.getName()).thenReturn("client1");
        when(_otherHandler.getName()).thenReturn("client3");

        // Send message to non-existing client "client2"
        _chatServer.sendMessage(message);

        // Verify that sender gets a notification about the non-existing client
        verify(_senderHandler, times(1)).sendMessage(argThat(arg -> arg instanceof TextMessage && ((TextMessage) arg).getText().contains("User client2 is not online.")));
        verify(_otherHandler, never()).sendMessage(any(BaseMessage.class));
    }
}
