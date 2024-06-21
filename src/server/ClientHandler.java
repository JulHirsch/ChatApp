package server;

import common.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ChatServer _chatServer;
    private final Socket _connectionToClient;
    public final String _name;
    private BufferedReader _fromClientReader;
    private PrintWriter _toClientWriter;

    public ClientHandler(ChatServer chatServer, Socket connectionToClient) {
        _chatServer = chatServer;
        _connectionToClient = connectionToClient;
        _name = connectionToClient.getInetAddress().getHostAddress(); // set name to ip address

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            _fromClientReader = new BufferedReader(new InputStreamReader(_connectionToClient.getInputStream()));
            _toClientWriter = new PrintWriter(new OutputStreamWriter(_connectionToClient.getOutputStream()), true);

            _chatServer.sendMessage(new Message(_name + " connected.", "Server"));

            String jsonMessage;
            while ((jsonMessage = _fromClientReader.readLine()) != null) {
                Message message = Message.fromJson(jsonMessage);
                _chatServer.sendMessage(new Message(message.getText(), _name, message.getReceiver(), message.getEncryptionType(), message.getEncryptionKey())); // Set the sender to avoid impersonation
            }
        } catch (IOException e) {
            // Handle exception (optional logging)
        } finally {
            cleanup();
        }
    }

    public void sendMessage(Message message) {
        if (_toClientWriter != null) {
            String jsonMessage = message.toJson();
            _toClientWriter.println(jsonMessage);
        }
    }

    public String getName(){
        return _name;
    }

    private void cleanup() {
        _chatServer.removeClient(this);
        _chatServer.sendMessage(new Message(_name + " disconnected.", "Server"));

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

    private String sanitizeMessage(String message) {
        // Basic sanitization logic
        return message.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
