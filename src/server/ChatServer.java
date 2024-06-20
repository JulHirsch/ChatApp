package server;

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

    public void broadcastMessage(String message) {
        if (message == null) {
            return;
        }
        System.out.println(message);
        for (ClientHandler client : _clients) {
            client.sendMessage(message);
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
