package server;

import common.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class ChatServerTest {

    private ChatServer chatServer;
    private IClientHandler senderHandler;
    private IClientHandler otherHandler;

    @BeforeEach
    public void setUp() throws Exception {
        chatServer = new ChatServer(3141);
        senderHandler = Mockito.mock(IClientHandler.class);
        otherHandler = Mockito.mock(IClientHandler.class);
        chatServer.start();
    }

    @AfterEach
    public void tearDown() {
        chatServer.stop();
    }

    @Test
    public void testSendMessageToGlobal() {
        Message message = new Message("Hello, world!","client1", "custom", "global", "", "");

        List<IClientHandler> clients = chatServer.getClients();
        clients.add(senderHandler);
        clients.add(otherHandler);

        when(senderHandler.getName()).thenReturn("client1");
        when(otherHandler.getName()).thenReturn("client2");

        chatServer.sendMessage(message);

        verify(otherHandler, times(1)).sendMessage(message);
        verify(senderHandler, never()).sendMessage(any(Message.class));
    }

    @Test
    public void testSendMessageToSpecificClient() {
        Message message = new Message("Hello, client2!","client1", "custom", "client2", "", "");

        List<IClientHandler> clients = chatServer.getClients();
        clients.add(senderHandler);
        clients.add(otherHandler);

        when(senderHandler.getName()).thenReturn("client1");
        when(otherHandler.getName()).thenReturn("client2");

        chatServer.sendMessage(message);

        verify(otherHandler, times(1)).sendMessage(message);
        verify(senderHandler, never()).sendMessage(any(Message.class));
    }

    @Test
    public void testSendMessageToNonExistingClient() {
        Message message = new Message("Hello, client2!","client1", "custom", "client2", "", "");

        List<IClientHandler> clients = chatServer.getClients();
        clients.add(senderHandler);
        clients.add(otherHandler);

        // Mock the client names
        when(senderHandler.getName()).thenReturn("client1");
        when(otherHandler.getName()).thenReturn("client3");

        // Send message to non-existing client "client2"
        chatServer.sendMessage(message);

        // Verify that sender gets a notification about the non-existing client
        verify(senderHandler, times(1)).sendMessage(argThat(arg -> arg.getText().contains("User client2 is not online.")));
        verify(otherHandler, never()).sendMessage(any(Message.class));
    }
}
