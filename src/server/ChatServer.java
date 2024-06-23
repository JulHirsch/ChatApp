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

    private static void logMessage(Message message) {
        String logMessage = String.format(
                "from %s (%s) to %s: %s",
                message.getCustomName(),
                message.getSender(),
                message.getReceiver(),
                message.getText());
        System.out.println(logMessage);
    }

    public void start() {
        running = true;
        serverThread = new Thread(() -> {
            try {
                tryToAcceptClients();
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

    private void tryToAcceptClients() throws IOException {
        while (running) {
            System.out.println("Waiting for new client...");
            Socket connectionToClient = _serverSocket.accept();
            IClientHandler client = new ClientHandler(this, connectionToClient);
            _clients.add(client);
            System.out.println("Accepted new client");
        }
    }

    public void stop() {
        running = false;
        try {
            tryToCloseServerSocket();
            tryToTerminateServerThread();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tryToCloseServerSocket() throws IOException {
        if (_serverSocket != null && !_serverSocket.isClosed()) {
            _serverSocket.close();
        }
    }

    private void tryToTerminateServerThread() throws InterruptedException {
        if (serverThread != null) {
            serverThread.join();
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

        logMessage(message);

        if (message.isGlobal()) {
            sendMessageToAllClientsExcludingTheSender(message);
        } else {
            handlePrivateMessage(message);
        }
    }

    private void handlePrivateMessage(Message message) {
        boolean receiverFound = false;

        for (IClientHandler client : _clients) {
            if (!client.getName().equals(message.getReceiver())) {
                continue;
            }
            client.sendMessage(message);
            receiverFound = true;
        }

        if (!receiverFound) {
            respondErrorToSender(message);
        }
    }

    private void respondErrorToSender(Message message) {
        for (IClientHandler client : _clients) {
            if (!client.getName().equals(message.getSender())) {
                continue;
            }

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

    private void sendMessageToAllClientsExcludingTheSender(Message message) {
        for (IClientHandler client : _clients) {
            if (client.getName().equals(message.getSender())) {
                continue;
            }
            client.sendMessage(message);
        }
    }

    protected List<IClientHandler> getClients() {
        return _clients;
    }

    private void cleanup() {
        if (_serverSocket == null || _serverSocket.isClosed()) {
            return;
        }

        try {
            _serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
