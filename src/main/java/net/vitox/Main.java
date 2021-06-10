package net.vitox;

import net.vitox.command.CommandManager;
import net.vitox.network.Server;

import java.io.IOException;

public class Main {

    public static String TRIM_CHAR = "~";

    public static void main(String[] args) throws IOException {

        Main main = new Main();
        Server server = new Server();
        CommandManager commandManager = new CommandManager();

        main.welcomeScreen();
        commandManager.initCommands();
        server.start();
    }

    public void welcomeScreen() {
        System.out.println("           _                 ");
        System.out.println("          | |               ");
        System.out.println("  ___  ___| |_ _ __  ___  ___ ");
        System.out.println(" / _ \\/ __| | | '_ \\/ __|/ _ \\");
        System.out.println("|  __/ (__| | | |_) \\__ \\  __/");
        System.out.println(" \\___|\\___|_|_| .__/|___/\\___|");
        System.out.println("              | |             ");
        System.out.println("              |_|             ");
        System.out.println("-------------------------------");
    }
}
