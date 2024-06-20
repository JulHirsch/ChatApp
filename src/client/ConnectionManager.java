package client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ConnectionManager {
    private final String _address;
    private final int _port;
    private Socket _connectionToServer;
    private BufferedReader _fromServerReader;
    private PrintWriter _toServerWriter;
    private ChatClient _chatClient;

    public ConnectionManager(String address, int port) {
        _address = address;
        _port = port;
    }

    public void start() {
        try {
            _connectionToServer = new Socket(_address, _port);
            _fromServerReader = new BufferedReader(new InputStreamReader(_connectionToServer.getInputStream()));
            _toServerWriter = new PrintWriter(_connectionToServer.getOutputStream(), true);

            _chatClient = new ChatClient(this);
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            showErrorAndExit("Connection to server with address \"" + _address + "\" failed.");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = _fromServerReader.readLine()) != null) {
                _chatClient.appendMessage(message);
            }
        } catch (IOException e) {
            showErrorAndExit("Connection lost.");
        } finally {
            cleanupResources();
        }
    }

    public void sendMessage(String message) {
        if (_toServerWriter != null) {
            _toServerWriter.println(message);
        }
    }

    private void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(null, message);
        System.exit(1);
    }

    private void cleanupResources() {
        try {
            if (_connectionToServer != null) _connectionToServer.close();
            if (_fromServerReader != null) _fromServerReader.close();
            if (_toServerWriter != null) _toServerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}