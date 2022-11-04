package net.foulest.fstaff.cmds;

import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.Settings;
import net.foulest.fstaff.utils.command.Command;
import net.foulest.fstaff.utils.command.CommandArgs;

/**
 * @author Foulest
 * @project FStaff
 */
public class MainCmd {

    @Command(name = "fstaff", description = "Main command.", usage = "/fstaff")
    public void onCommand(CommandArgs args) {
        if (!args.getSender().hasPermission("fstaff.admin")) {
            MessageUtil.messagePlayer(args.getSender(), "Unknown command. Type \"/help\" for help.");
            return;
        }

        if (args.length() == 0) {
            MessageUtil.messagePlayer(args.getSender(), "");
            MessageUtil.messagePlayer(args.getSender(), "&e&lFStaff Help");
            MessageUtil.messagePlayer(args.getSender(), "&7* &f/fstaff reload &7- Reloads the config.");
            MessageUtil.messagePlayer(args.getSender(), "");
            return;
        }

        if (args.length() == 1 && args.getArgs(0).equals("reload")) {
            Settings.loadSettings();
            MessageUtil.messagePlayer(args.getSender(), "&aReloaded successfully.");
            return;
        }

        if (args.length() >= 1) {
            MessageUtil.messagePlayer(args.getSender(), "");
            MessageUtil.messagePlayer(args.getSender(), "&e&lFStaff Help");
            MessageUtil.messagePlayer(args.getSender(), "&7* &f/fstaff reload &7- Reloads the config.");
            MessageUtil.messagePlayer(args.getSender(), "");
        }
    }
}
