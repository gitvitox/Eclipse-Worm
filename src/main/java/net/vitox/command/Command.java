package net.vitox.command;

public class Command {

    public String commandName;
    public String commandDescription;

    public Command(String commandName, String commandDescription) {
        this.commandName = commandName;
        this.commandDescription = commandDescription;
    }

    public void runCommand(String[] args) {

    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public void setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
    }
}
