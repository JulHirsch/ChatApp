package server;

import common.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientHandlerTest {

    private IChatServer _chatServer;
    private Socket _socket;
    private ClientHandler _clientHandler;
    private PipedInputStream _pipedInputStream;
    private PipedOutputStream _pipedOutputStream;
    private BufferedReader _reader;
    private PrintWriter _writer;

    @BeforeEach
    public void setUp() throws Exception {
        _chatServer = mock(IChatServer.class);
        _socket = mock(Socket.class);

        _pipedInputStream = new PipedInputStream();
        _pipedOutputStream = new PipedOutputStream();

        InetAddress inetAddress = mock(InetAddress.class);
        when(inetAddress.getHostAddress()).thenReturn("127.0.0.1");
        when(_socket.getInetAddress()).thenReturn(inetAddress);

        when(_socket.getInputStream()).thenReturn(_pipedInputStream);
        when(_socket.getOutputStream()).thenReturn(_pipedOutputStream);

        _reader = new BufferedReader(new InputStreamReader(new PipedInputStream(_pipedOutputStream)));
        _writer = new PrintWriter(new PipedOutputStream(_pipedInputStream), true);

        _clientHandler = new ClientHandler(_chatServer, _socket);

        // Ensure that the writer is set in the client handler
        Field writerField = ClientHandler.class.getDeclaredField("_toClientWriter");
        writerField.setAccessible(true);
        writerField.set(_clientHandler, _writer);
    }

    @AfterEach
    public void tearDown() throws Exception {
        cleanup();
    }

    private void cleanup() throws Exception {
        _reader.close();
        _writer.close();
        _pipedInputStream.close();
        _pipedOutputStream.close();
    }

    @Test
    public void testConnectionEstablished() throws Exception {
        verify(_chatServer, timeout(1000)).sendMessage(argThat(message -> message.getText().contains(" connected.")));
    }

    @Test
    public void testCleanupOnClientDisconnection() throws Exception {
        // Close the piped input stream to simulate client disconnection
        _pipedInputStream.close();

        // Verify cleanup
        verify(_chatServer, timeout(1000)).removeClient(_clientHandler);
        verify(_chatServer, timeout(1000)).sendMessage(argThat(message -> message.getText().contains("disconnected.")));
    }

    @Test
    public void testSendMessage() throws Exception {
        Message testMessage = new Message("Test message", "server", "custom", "client1", "", "");

        // Call the method to test
        _clientHandler.sendMessage(testMessage);

        // Read the message from the piped input stream
        String jsonMessage = _reader.readLine();

        // Verify the message
        assertNotNull(jsonMessage);
        Message receivedMessage = Message.fromJson(jsonMessage);
        assertEquals("Test message", receivedMessage.getText());
        assertEquals("server", receivedMessage.getSender());
        assertEquals("custom", receivedMessage.getCustomName());
        assertEquals("client1", receivedMessage.getReceiver());
    }
}
