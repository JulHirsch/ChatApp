package server;

import common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer implements IChatServer {
    private ServerSocket _serverSocket;
    private List<IClientHandler> _clients;
    private volatile boolean running;
    private Thread serverThread;

    public ChatServer(int port) {
        _clients = new CopyOnWriteArrayList<>();
        try {
            _serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(3141);
        server.start();
    }

    public void start() {
        running = true;
        serverThread = new Thread(() -> {
            try {
                while (running) {
                    System.out.println("Waiting for new client...");
                    Socket connectionToClient = _serverSocket.accept();
                    IClientHandler client = new ClientHandler(this, connectionToClient);
                    _clients.add(client);
                    System.out.println("Accepted new client");
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                cleanup();
            }
        });
        serverThread.start();
    }

    public void stop() {
        running = false;
        try {
            if (_serverSocket != null && !_serverSocket.isClosed()) {
                _serverSocket.close();
            }
            if (serverThread != null) {
                serverThread.join();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeClient(IClientHandler client) {
        _clients.remove(client);
    }

    @Override
    public void sendMessage(Message message) {
        if (message == null) {
            return;
        }

        String logMessage = String.format("from %s (%s) to %s: %s", message.getCustomName(), message.getSender(), message.getReceiver(), message.getText());
        System.out.println(logMessage);

        if (message.getReceiver().equals(Message.GLOBAL_RECEIVER)) {
            for (IClientHandler client : _clients) {
                if (client.getName().equals(message.getSender())) {
                    continue;
                }
                client.sendMessage(message);
            }
        } else {
            boolean receiverFound = false;

            for (IClientHandler client : _clients) {
                if (!client.getName().equals(message.getReceiver())) {
                    continue;
                }

                client.sendMessage(message);
                receiverFound = true;
            }

            if (!receiverFound) {
                for (IClientHandler client : _clients) {
                    if (client.getName().equals(message.getSender())) {
                        Message notification = new Message(
                                String.format("User %s is not online.", message.getReceiver()),
                                "Server",
                                "Server",
                                client.getName(),
                                "",
                                ""
                        );
                        client.sendMessage(notification);
                        break;
                    }
                }
            }
        }
    }

    protected List<IClientHandler> getClients() {
        return _clients;
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
}
