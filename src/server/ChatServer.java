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
        _clients = new CopyOnWriteArrayList<ClientHandler>(); // thread safe

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
            if(_serverSocket == null){
                return;
            }
            try {
                _serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientHandler client) {
        _clients.remove(client);
    }

    public void broadcastMessage(String message) {
        System.out.println(message);
        if (message == null) {
            return;
        }
        for (ClientHandler client : _clients) {
            client.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        new ChatServer(3141);
    }
}