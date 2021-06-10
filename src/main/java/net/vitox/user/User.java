package net.vitox.user;

import java.net.Socket;
import java.util.Objects;

public class User {

    private final String name, os, ip, cores;

    private final Socket socket;

    public User(String name, String os, String ip, String cores, Socket socket) {
        this.name = name;
        this.os = os;
        this.ip = ip;
        this.cores = cores;
        this.socket = socket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(os, that.os) &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(cores, that.cores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, os, ip, cores);
    }

    public String getName() {
        return name;
    }

    public String getOs() {
        return os;
    }

    public String getIp() {
        return ip;
    }

    public String getCores() {
        return cores;
    }

    public Socket getSocket() {
        return socket;
    }

}
