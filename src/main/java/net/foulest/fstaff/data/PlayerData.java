package net.foulest.fstaff.data;

import lombok.Getter;
import lombok.Setter;
import net.foulest.fstaff.events.StaffModeEvent;
import net.foulest.fstaff.events.VanishEvent;
import net.foulest.fstaff.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Foulest
 * @project FStaff
 */
@Getter
@Setter
public final class PlayerData {

    private static final Set<PlayerData> INSTANCES = new HashSet<>();
    private final Player player;
    private boolean vanished;
    private boolean inStaffMode;

    private PlayerData(Player player) {
        this.player = player;
        INSTANCES.add(this);
    }

    /**
     * Returns the player's PlayerData.
     */
    public static PlayerData getInstance(Player player) {
        if (INSTANCES.isEmpty()) {
            new PlayerData(player);
        }

        for (PlayerData playerData : INSTANCES) {
            if (playerData == null || playerData.getPlayer() == null
                    || playerData.getPlayer().getUniqueId() == null
                    || player == null || player.getUniqueId() == null) {
                MessageUtil.log(Level.WARNING, "Player data for player '" + player + "' is null");
                return null;
            }

            if (playerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return playerData;
            }
        }

        return new PlayerData(player);
    }

    public void enableStaffMode(boolean silent) {
        Bukkit.getServer().getPluginManager().callEvent(new StaffModeEvent(player.getUniqueId(), true));

        if (!silent) {
            MessageUtil.messagePlayer(player, "&aStaff mode has been enabled.");
        }
    }

    public void disableStaffMode(boolean silent) {
        Bukkit.getServer().getPluginManager().callEvent(new StaffModeEvent(player.getUniqueId(), false));

        if (!silent) {
            MessageUtil.messagePlayer(player, "&cStaff mode has been disabled.");
        }
    }

    public void enableVanish(boolean silent) {
        Bukkit.getServer().getPluginManager().callEvent(new VanishEvent(player.getUniqueId(), true));

        if (!silent) {
            MessageUtil.messagePlayer(player, "&aVanish has been enabled.");
        }
    }

    public void disableVanish(boolean silent) {
        Bukkit.getServer().getPluginManager().callEvent(new VanishEvent(player.getUniqueId(), false));

        if (!silent) {
            MessageUtil.messagePlayer(player, "&cVanish has been disabled.");
        }
    }
}
