package server;

import common.Messages.BaseMessage;
import common.Messages.KeyExchangeMessage;
import common.Messages.TextMessage;

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

    private static void logMessage(BaseMessage message) {
        String logMessage = "";
        if (message instanceof TextMessage m) {
            logMessage = String.format(
                    "Text from %s (%s) to %s: %s",
                    m.getCustomName(),
                    m.getSender(),
                    m.getReceiver(),
                    m.getText());

        } else if (message instanceof KeyExchangeMessage k) {
            logMessage = String.format(
                    "Key exchange from %s (%s) to %s: %s",
                    k.getCustomName(),
                    k.getSender(),
                    k.getReceiver(),
                    k.getEncryptionKey());
        }
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
    public void sendMessage(BaseMessage message) {
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

    private void handlePrivateMessage(BaseMessage message) {
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

    private void respondErrorToSender(BaseMessage message) {
        for (IClientHandler client : _clients) {
            if (!client.getName().equals(message.getSender())) {
                continue;
            }

            BaseMessage notification = new TextMessage(
                    String.format("User %s is not online.", message.getReceiver()),
                    client.getName(),
                    "Server",
                    "Server"
            );
            client.sendMessage(notification);
            break;
        }
    }

    private void sendMessageToAllClientsExcludingTheSender(BaseMessage message) {
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
