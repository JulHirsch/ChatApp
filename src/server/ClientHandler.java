package server;

import common.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ChatServer _chatServer;
    private final Socket _connectionToClient;
    private final String _ipAddress;
    private BufferedReader _fromClientReader;
    private PrintWriter _toClientWriter;

    public ClientHandler(ChatServer chatServer, Socket connectionToClient) {
        _chatServer = chatServer;
        _connectionToClient = connectionToClient;
        _ipAddress = connectionToClient.getInetAddress().getHostAddress(); // Set name to IP address

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            _fromClientReader = new BufferedReader(new InputStreamReader(_connectionToClient.getInputStream()));
            _toClientWriter = new PrintWriter(new OutputStreamWriter(_connectionToClient.getOutputStream()), true);

            _chatServer.sendMessage(new Message(STR."\{_ipAddress} connected.", "Server", "Server", Message.GLOBAL_RECEIVER, "", ""));

            String jsonMessage;
            while ((jsonMessage = _fromClientReader.readLine()) != null) {
                Message message = Message.fromJson(jsonMessage);
                // Overwrite the sender with the client's IP address
                Message forwardedMessage = new Message(message.getText(), _ipAddress, message.getCustomName(), message.getReceiver(), message.getEncryptionType(), message.getEncryptionKey());
                _chatServer.sendMessage(forwardedMessage);
            }
        } catch (IOException e) {
            // Handle exception (optional logging)
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        _chatServer.removeClient(this);
        _chatServer.sendMessage(new Message(_ipAddress + " disconnected.", "Server", _ipAddress, Message.GLOBAL_RECEIVER, "", ""));

        try {
            if (_fromClientReader != null) {
                _fromClientReader.close();
            }
            if (_toClientWriter != null) {
                _toClientWriter.close();
            }
            if (_connectionToClient != null && !_connectionToClient.isClosed()) {
                _connectionToClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        if (_toClientWriter != null) {
            String jsonMessage = message.toJson();
            _toClientWriter.println(jsonMessage);
        }
    }

    public String getName() {
        return _ipAddress;
    }
}
