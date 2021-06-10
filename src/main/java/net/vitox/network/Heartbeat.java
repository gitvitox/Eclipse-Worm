package net.vitox.network;

import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Heartbeat implements Runnable {

    public Socket socket;
    private boolean running = true;

    public Heartbeat(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (running)
                try {
                    SendUtil.getInstance().writeMsg("KEEP_ALIVE", socket);
                } catch (Exception e) {
                    UserHandler.userList.forEach(user -> {
                        if (socket == user.getSocket()) {
                            try {
                                onDisconnectedClient(socket, user);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    });
                }
        }, 0, 9, TimeUnit.SECONDS);
    }

    public void onDisconnectedClient(Socket socket, User user) throws IOException {
        socket.close();
        Thread.currentThread().interrupt();

        UserHandler.userList.remove(user);

        System.out.println("\u001B[31m" + "------------- [ Lost Connection ] -------------");
        System.out.println(user.getName() + " | " + user.getOs() + " | " + user.getIp() + " | " + "Cores: " + user.getCores());
        System.out.println("-----------------------------------------------" + "\u001B[0m");
    }
}
