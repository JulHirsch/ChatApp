package server;

import common.Messages.TextMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class ClientHandlerTest {

    private IChatServer _chatServer;
    private Socket _socket;
    private ClientHandler _clientHandler;
    private ByteArrayInputStream _inputStream;
    private ByteArrayOutputStream _outputStream;
    private BufferedReader _reader;
    private PrintWriter _writer;

    @BeforeEach
    public void setUp() throws Exception {
        _chatServer = mock(IChatServer.class);
        _socket = mock(Socket.class);

        _outputStream = new ByteArrayOutputStream();
        _inputStream = new ByteArrayInputStream(new byte[0]);

        InetAddress inetAddress = mock(InetAddress.class);
        when(inetAddress.getHostAddress()).thenReturn("127.0.0.1");
        when(_socket.getInetAddress()).thenReturn(inetAddress);

        when(_socket.getInputStream()).thenReturn(_inputStream);
        when(_socket.getOutputStream()).thenReturn(_outputStream);

        _writer = new PrintWriter(_outputStream, true);

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
        _writer.close();
        _inputStream.close();
        _outputStream.close();
    }

    @Test
    public void testConnectionEstablished() throws Exception {
        verify(_chatServer, timeout(1000)).sendMessage(argThat(message -> ((TextMessage) message).getText().contains(" connected.")));
    }

    @Test
    public void testCleanupOnClientDisconnection() throws Exception {
        // Close the input stream to simulate client disconnection
        _inputStream.close();

        // Verify cleanup
        verify(_chatServer, timeout(1000)).removeClient(_clientHandler);
        verify(_chatServer, timeout(1000)).sendMessage(argThat(message -> ((TextMessage) message).getText().contains("disconnected.")));
    }

    /*TODO maybe use an interface for the writer
    @Test
    public void testSendMessage() throws Exception {
        Message testMessage = new Message("Test message", "server", "custom", "client1", "", "");

        // Call the method to test
        _clientHandler.sendMessage(testMessage);

        // Convert the output stream to a byte array
        byte[] byteArray = _outputStream.toByteArray();

        // Create a new ByteArrayInputStream from the byte array
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        BufferedReader reader = new BufferedReader(new InputStreamReader(byteArrayInputStream));

        // Read the message from the new ByteArrayInputStream
        String jsonMessage = reader.readLine();

        // Verify the message
        assertNotNull(jsonMessage);
        Message receivedMessage = Message.fromJson(jsonMessage);
        assertEquals("Test message", receivedMessage.getText());
        assertEquals("server", receivedMessage.getSender());
        assertEquals("custom", receivedMessage.getCustomName());
        assertEquals("client1", receivedMessage.getReceiver());
    }*/
}
