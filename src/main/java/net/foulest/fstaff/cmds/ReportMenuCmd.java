package net.foulest.fstaff.cmds;

import net.foulest.fstaff.reports.menu.ReportMenu;
import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.command.Command;
import net.foulest.fstaff.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * @author Foulest
 * @project FStaff
 */
public class ReportMenuCmd {

    @Command(name = "reportmenu", description = "Opens the report menu.", usage = "/reportmenu", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        if (!player.hasPermission("fstaff.staff")) {
            MessageUtil.messagePlayer(args.getSender(), "Unknown command. Type \"/help\" for help.");
            return;
        }

        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, "&cUsage: /reportmenu");
            return;
        }

        new ReportMenu(player);
    }
}
