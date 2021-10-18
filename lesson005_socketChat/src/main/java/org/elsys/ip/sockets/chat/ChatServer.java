package org.elsys.ip.sockets.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new CalculatorClientHandler(serverSocket.accept()).start();
    }

    private static class CalculatorClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public CalculatorClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {
                    String line = in.readLine();
                    out.println(line);
                }
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            } finally {
                dispose();
            }
        }

        private void dispose() {
            try {
                if (clientSocket != null) clientSocket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Throwable t) {
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.start(6666);
    }
}
