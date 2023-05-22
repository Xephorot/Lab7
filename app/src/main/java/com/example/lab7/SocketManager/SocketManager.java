package com.example.lab7.SocketManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final int PORT = 12345;

    public interface SocketListener {
        void onMessageReceived(String message);

        void onClientConnected();

        void onClientDisconnected();
    }

    private SocketListener socketListener;

    public SocketManager(SocketListener listener) {
        socketListener = listener;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            waitForClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer(String serverIP) {
        try {
            clientSocket = new Socket(serverIP, PORT);
            initializeStreams();
            socketListener.onClientConnected();
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void stopServer() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForClient() {
        try {
            clientSocket = serverSocket.accept();
            initializeStreams();
            socketListener.onClientConnected();
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeStreams() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private void startListening() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        socketListener.onMessageReceived(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    socketListener.onClientDisconnected();
                }
            }
        });
        thread.start();
    }
}
