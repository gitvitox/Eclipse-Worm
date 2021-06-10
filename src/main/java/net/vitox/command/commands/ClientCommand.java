package net.vitox.command.commands;

import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;

public class ClientCommand extends Command {
    public ClientCommand() {
        super("clients", "Show info of clients that are connected");
    }

    @Override
    public void runCommand(String[] args) {
        System.out.println("------------------[ Users found: " + UserHandler.userList.size() +"  ]------------------");
        for (User user : UserHandler.userList) {
            System.out.println(user.getName() + " | " + user.getOs() + " | " + user.getIp() + " | Cores:" + user.getCores());
        }
        System.out.println("--------------------------------------------");
    }
}
