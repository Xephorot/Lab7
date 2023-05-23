package com.example.lab7.Cliente;
import java.net.*;
import java.io.*;

public class Client {
    private Socket socket = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            in = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
        } catch(UnknownHostException u) {
            System.out.println(u);
        } catch(IOException i) {
            System.out.println(i);
        }

        String line = "";
        while (!line.equals("End")) {
            try {
                line = in.readLine();
                out.writeUTF(line);
            } catch(IOException i) {
                System.out.println(i);
            }
        }

        try {
            in.close();
            out.close();
            socket.close();
        } catch(IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("192.168.2.102", 5000);
    }
}