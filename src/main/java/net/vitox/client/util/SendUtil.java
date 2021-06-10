package net.vitox.client.util;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SendUtil {

    private DataInputStream in;
    private DataOutputStream out;

    public static SendUtil instance = new SendUtil();

    public static SendUtil getInstance() {
        return instance;
    }

    public void writeMsg(String message, Socket socket) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.write(intToByteArray(message.getBytes().length));
        out.write(message.getBytes());
        out.flush();
    }

    public byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public String readMsg(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());

        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    public byte readByte(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        return in.readByte();
    }

    public int readInt(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        return in.readInt();
    }

    public void read(byte[] buffer, Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        in.readFully(buffer);
    }

    public short readShort(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        return in.readShort();
    }

}
