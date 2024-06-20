package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private ChatServer _chatServer;
    private Socket _connectionToClient;
    private String name;
    private BufferedReader _fromClientReader;
    private PrintWriter _toClientWriter;

    public ClientHandler(ChatServer chatServer, Socket connectionToClient) {

        _chatServer = chatServer;
        _connectionToClient = connectionToClient;

        name = connectionToClient.getInetAddress().getHostAddress(); // set name to ip address

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            _fromClientReader = new BufferedReader(new InputStreamReader(_connectionToClient.getInputStream()));
            _toClientWriter = new PrintWriter(new OutputStreamWriter(_connectionToClient.getOutputStream()));

            _chatServer.broadcastMessage(name + " connected.");

            String message = _fromClientReader.readLine();
            while (message != null) {
                message = _fromClientReader.readLine(); //TODO sanitize string
                _chatServer.broadcastMessage(name + ": " + message);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            _chatServer.removeClient(this);
            _chatServer.broadcastMessage(name + " disconnected.");

            if (_fromClientReader != null) {
                try {
                    _fromClientReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (_toClientWriter != null) {
                _toClientWriter.close();
            }
        }
    }

    public void sendMessage(String message) {
        _toClientWriter.println(message);
        _toClientWriter.flush();
    }
}
