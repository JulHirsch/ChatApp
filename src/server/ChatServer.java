package server;

import common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private ServerSocket _serverSocket;
    private List<ClientHandler> _clients;

    public ChatServer(int port) {
        _clients = new CopyOnWriteArrayList<>(); // thread safe

        try {
            _serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                System.out.println("Waiting for new client...");
                Socket connectionToClient = _serverSocket.accept();
                ClientHandler client = new ClientHandler(this, connectionToClient);
                _clients.add(client);
                System.out.println("Accepted new client");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    public void removeClient(ClientHandler client) {
        _clients.remove(client);
    }

    public void sendMessage(Message message) {
        if (message == null) {
            return;
        }

        String logMessage = String.format("from %s to %s: %s", message.getSender(), message.getReceiver(), message.getText());
        System.out.println(logMessage);

        if (message.getReceiver().equals(Message.GLOBAL_RECEIVER)) {
            for (ClientHandler client : _clients) {
                client.sendMessage(message);
            }
        } else {
            boolean receiverFound = false;

            // Send to specific receiver
            for (ClientHandler client : _clients) {
                if (client.getName().equals(message.getReceiver())) {
                    client.sendMessage(message);
                    receiverFound = true;
                    break;
                }
            }

            // Respond to sender if receiver is not online
            if (!receiverFound) {
                for (ClientHandler client : _clients) {
                    if (client.getName().equals(message.getSender())) {
                        Message notification = new Message(
                                String.format("User %s is not online.", message.getReceiver()),
                                "Server",
                                message.getSender()
                        );
                        client.sendMessage(notification);
                        break;
                    }
                }
            }
        }
    }

    private void cleanup() {
        if (_serverSocket != null && !_serverSocket.isClosed()) {
            try {
                _serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer(3141);
    }
}
