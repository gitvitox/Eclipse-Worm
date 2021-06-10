package net.vitox.command.commands;

import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;

public class StatisticCommand extends Command {

    public StatisticCommand() {
        super("stats", "Show info of clients that are connected");
    }

    @Override
    public void runCommand(String[] args) {
        int cores = 0;
        for (User user : UserHandler.userList) {
            cores += Integer.parseInt(user.getCores());
        }
        System.out.println("------------- [ Overall Statistics ] -------------");
        System.out.println("Clients: " + UserHandler.userList.size());
        System.out.println("Cores: " + cores);
        System.out.println("Threads: " + cores * 2500);
        System.out.println("--------------------------------------------");
    }
}
