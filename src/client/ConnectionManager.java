package client;

import common.Messages.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionManager implements IConnectionManager {
    private final String _address;
    private final int _port;
    private Socket _connectionToServer;
    private BufferedReader _fromServerReader;
    private PrintWriter _toServerWriter;
    private IChatClient _chatClient;

    public ConnectionManager(String address, int port) {
        _address = address;
        _port = port;
    }

    public void setChatClient(IChatClient chatClient) {
        _chatClient = chatClient;
    }

    public void start() {
        try {
            _connectionToServer = new Socket(_address, _port);
            _fromServerReader = new BufferedReader(new InputStreamReader(_connectionToServer.getInputStream()));
            _toServerWriter = new PrintWriter(_connectionToServer.getOutputStream(), true);

            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            showErrorAndExit("Connection to server with address \"" + _address + "\" failed.");
        }
    }

    private void receiveMessages() {
        try {
            String jsonMessage;
            while ((jsonMessage = _fromServerReader.readLine()) != null) {
                IMessage message = MessageDeserializer.fromJson(jsonMessage);
                processMessage(message);
            }
        } catch (IOException e) {
            showErrorAndExit("Connection lost.");
        } finally {
            cleanupResources();
        }
    }

    private void processMessage(IMessage message) {
        switch (message.getMessageType()) {
            case TEXT:
                TextMessage textMessage = (TextMessage) message;
                _chatClient.appendMessage(textMessage);
                break;
            case KEY_EXCHANGE:
                KeyExchangeMessage keyExchangeMessage = (KeyExchangeMessage) message;
                processKeyExchangeMessage(keyExchangeMessage);
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + message.getMessageType());
        }
    }

    private void processKeyExchangeMessage(KeyExchangeMessage keyExchangeMessage) {
        System.out.println("Received key exchange message from " + keyExchangeMessage.getSender());
    }

    public void sendMessage(IMessage message) {
        if (_toServerWriter != null) {
            String jsonMessage = message.toJson();
            _toServerWriter.println(jsonMessage);
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
