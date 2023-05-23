package com.example.lab7.SocketManager;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager {

    private static final int PORT = 12345;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;
    private SocketListener socketListener;
    private String serverIp;

    public SocketManager(SocketListener listener) {
        socketListener = listener;
    }

    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    waitForClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void connectToServer(String serverIp) {
        this.serverIp = serverIp;
        new ConnectToServerTask().execute();
    }

    private void connectToServerInternal() {
        try {
            clientSocket = new Socket(serverIp, PORT);
            initializeStreams();
            socketListener.onClientConnected();
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeStreams() throws IOException {
        inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void startListening() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message;
                    while ((message = inputStream.readLine()) != null) {
                        socketListener.onMessageReceived(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeConnection();
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        new SendMessageTask().execute(message);
    }

    public void stopServer() {
        closeConnection();
        try {
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

    private void closeConnection() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketListener.onClientDisconnected();
    }

    public interface SocketListener {
        void onMessageReceived(String message);
        void onClientConnected();
        void onClientDisconnected();
    }

    private class ConnectToServerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            connectToServerInternal();
            return null;
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];
            if (outputStream != null) {
                outputStream.println(message);
            } else {
                reconnect();
            }
            return null;
        }
    }

    public void reconnect() {
        if (clientSocket != null && clientSocket.isConnected()) {
            closeConnection();
        }
        connectToServer(serverIp);
    }
}
