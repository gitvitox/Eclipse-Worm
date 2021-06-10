package net.vitox.command.commands;

import net.vitox.command.Command;
import net.vitox.command.CommandManager;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Show a list of all commands + description");
    }

    @Override
    public void runCommand(String[] args) {
        System.out.println("------------------[ Help ]------------------");
        for (Command cmd : CommandManager.commands) {
            System.out.println(CommandManager.prefix + cmd.getCommandName() + " | " + cmd.getCommandDescription());
        }
        System.out.println("--------------------------------------------");
    }
}
