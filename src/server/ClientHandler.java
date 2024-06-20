package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ChatServer _chatServer;
    private final Socket _connectionToClient;
    private final String _name;
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

            _chatServer.broadcastMessage(_name + " connected.");

            String message;
            while ((message = _fromClientReader.readLine()) != null) {
                message = sanitizeMessage(message);
                _chatServer.broadcastMessage(_name + ": " + message);
            }
        } catch (IOException e) {
            // Handle exception (optional logging)
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        _chatServer.removeClient(this);
        _chatServer.broadcastMessage(_name + " disconnected.");

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

    public void sendMessage(String message) {
        if (_toClientWriter != null) {
            _toClientWriter.println(message);
        }
    }

    private String sanitizeMessage(String message) {
        // Basic sanitization logic
        return message.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
