package net.foulest.fstaff.cmds;

import net.foulest.fstaff.data.PlayerData;
import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.command.Command;
import net.foulest.fstaff.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * @author Foulest
 * @project FStaff
 */
public class StaffModeCmd {

    @Command(name = "staff", aliases = {"staffmode"}, description = "Toggle staff mode.", usage = "/staff", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        if (!(args.getSender() instanceof Player)) {
            MessageUtil.messagePlayer(args.getSender(), "Only players can execute this command.");
            return;
        }

        Player player = args.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (args.length() > 0) {
            MessageUtil.messagePlayer(player, "&cUsage: /staff");
            return;
        }

        if (playerData.isInStaffMode()) {
            playerData.disableStaffMode(false);
        } else {
            playerData.enableStaffMode(false);
        }
    }
}
