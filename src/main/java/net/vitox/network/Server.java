package net.vitox.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void start() throws IOException {

        int port = 55667;
        ServerSocket serverSocket = new ServerSocket(port);

        for (;;) {
            Socket client = serverSocket.accept();
            new Thread(new Heartbeat(client)).start();
            new Thread(new ServerHandler(client)).start();
        }
    }


}