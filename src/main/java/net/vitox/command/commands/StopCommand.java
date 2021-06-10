package net.vitox.command.commands;

import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

public class StopCommand extends Command {

    public StopCommand() {
        super("rem", "Stop / Remove a client.");
    }

    @Override
    public void runCommand(String[] args) {

        if (args.length <= 1) {
            System.out.println("Missing arguments: .rem <client name>");
            return;
        }

        try {
            for (User user : UserHandler.userList) {

                if (user.getName().equals(args[1])) {
                    SendUtil.getInstance().writeMsg("STOP", user.getSocket());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
