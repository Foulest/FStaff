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
public class VanishCmd {

    @Command(name = "vanish", description = "Toggle vanish.", usage = "/vanish", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        if (!(args.getSender() instanceof Player)) {
            MessageUtil.messagePlayer(args.getSender(), "Only players can execute this command.");
            return;
        }

        Player player = args.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (args.length() > 0) {
            MessageUtil.messagePlayer(player, "&cUsage: /vanish");
            return;
        }

        if (playerData.isVanished()) {
            playerData.disableVanish(false);
        } else {
            playerData.enableVanish(false);
        }
    }
}
