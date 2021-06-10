package net.vitox.client;

import net.vitox.client.util.SendUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SendInformationThread implements Runnable {

    public Socket socket;

    public SendInformationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            if (socket.isConnected()) {
                String ipAddress = InetAddress.getLocalHost().getHostName();
                String osName = System.getProperty("os.name");
                int availableCores = Runtime.getRuntime().availableProcessors();

                SendUtil.getInstance().writeMsg(ipAddress + Client.TRIM_CHAR + osName + Client.TRIM_CHAR + availableCores, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
