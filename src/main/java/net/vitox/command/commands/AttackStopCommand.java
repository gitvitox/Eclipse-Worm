package net.vitox.command.commands;

import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

public class AttackStopCommand extends Command {

    public AttackStopCommand() {
        super("stop", "Stopping the eclipse.");
    }

    @Override
    public void runCommand(String[] args) {
        try {

            for (User user : UserHandler.userList) {
                SendUtil.getInstance().writeMsg("ATTACK_STOP", user.getSocket());
            }

            System.out.println("Succesfully stopped attack.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
