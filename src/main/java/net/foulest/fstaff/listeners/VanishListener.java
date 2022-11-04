package net.foulest.fstaff.listeners;

import net.foulest.fstaff.FStaff;
import net.foulest.fstaff.data.PlayerData;
import net.foulest.fstaff.events.VanishEvent;
import net.foulest.fstaff.utils.ItemBuilder;
import net.foulest.fstaff.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Foulest
 * @project FStaff
 */
public class VanishListener implements Listener {

    @EventHandler
    public void onToggleVanish(VanishEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());
        PlayerData playerData = PlayerData.getInstance(player);

        if (event.isVanished()) {
            playerData.setVanished(true);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("fstaff.staff")) {
                    online.hidePlayer(player);
                }
            }

        } else {
            playerData.setVanished(false);

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerData onlineData = PlayerData.getInstance(online);

            if (!player.hasPermission("fstaff.staff") && onlineData.isVanished()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.hidePlayer(online);
                    }
                }.runTaskLater(FStaff.instance, 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT") && item != null) {
            if (item.getType() == Material.INK_SACK && item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName().contains("Vanish Enabled")) {
                    event.setCancelled(true);

                    if (!player.hasPermission("fstaff.staff")) {
                        MessageUtil.messagePlayer(player, "&cNo permission.");
                        return;
                    }

                    playerData.disableVanish(false);

                    ItemStack vanishItem = new ItemBuilder(Material.INK_SACK).durability(8).name("&cVanish Disabled &7(Right Click)").getItem();
                    player.getInventory().setItem(2, vanishItem);
                    return;
                }

                if (item.getItemMeta().getDisplayName().contains("Vanish Disabled")) {
                    event.setCancelled(true);

                    if (!player.hasPermission("fstaff.staff")) {
                        MessageUtil.messagePlayer(player, "&cNo permission.");
                        return;
                    }

                    playerData.enableVanish(false);

                    ItemStack vanishItem = new ItemBuilder(Material.INK_SACK).durability(10).name("&aVanish Enabled &7(Right Click)").getItem();
                    player.getInventory().setItem(2, vanishItem);
                }
            }
        }
    }
}
