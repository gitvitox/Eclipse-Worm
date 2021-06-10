package net.vitox.network;

import net.vitox.Main;
import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

import java.io.IOException;
import java.net.Socket;

public class ServerHandler implements Runnable {

    public Socket client;

    public ServerHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {

            String[] getInformation = SendUtil.getInstance().readMsg(client).split(Main.TRIM_CHAR);

            String getComputerName = getInformation[0];
            String getOS = getInformation[1];
            String getCores = getInformation[2];

            User newUser = new User(getComputerName, getOS, client.getInetAddress().getHostAddress() + ":" + client.getLocalPort(), getCores, client);

            System.out.println("\u001B[32m" +"------------- [ New Connection ] -------------");
            System.out.println(getComputerName +  " | " + getOS + " | " + client.getInetAddress() + " | " + "Cores: " + getCores);
            System.out.println("-----------------------------------------------" + "\u001B[0m");

            UserHandler.userList.add(newUser);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}