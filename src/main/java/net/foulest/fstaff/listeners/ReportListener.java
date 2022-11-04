package net.foulest.fstaff.listeners;

import net.foulest.fstaff.events.ReportEvent;
import net.foulest.fstaff.reports.Report;
import net.foulest.fstaff.reports.ReportManager;
import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.Settings;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author Foulest
 * @project FStaff
 */
public class ReportListener implements Listener {

    @EventHandler
    public void onPlayerReport(ReportEvent event) {
        Report report = event.getReport();
        String reportMessage = Settings.config.getString("reports.report-message")
                .replace("%target%", report.getTargetName())
                .replace("%reporter%", report.getReporterName())
                .replace("%reason%", report.getReason());

        MessageUtil.messagePlayer(Bukkit.getConsoleSender(), reportMessage);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("fstaff.staff")) {
                TextComponent message = new TextComponent(MessageUtil.colorize(reportMessage));
                TextComponent hoverMessage = new TextComponent(new ComponentBuilder("").create());

                hoverMessage.addExtra(new TextComponent(MessageUtil.colorize("&aClick to open the report menu.")));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{hoverMessage}));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reportmenu"));

                online.spigot().sendMessage(message);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (inventory == null || inventory.getTitle() == null) {
            return;
        }

        if (inventory.getTitle().contains("Reporting ")) {
            MessageUtil.messagePlayer(player, "&cYour report has been cancelled.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null || event.getCurrentItem() == null) {
            return;
        }

        if (inventory.getTitle().contains("Reporting ")) {
            if (!(event.getClick().isLeftClick() || event.getClick().isRightClick())) {
                event.setCancelled(true);
                return;
            }

            if (event.getCurrentItem().getItemMeta() == null) {
                event.setCancelled(true);
                return;
            }

            ItemStack nmsStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
            NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
            String reason = compound.getString("reason");
            String targetName = compound.getString("targetName");

            if (reason == null || targetName == null || reason.trim().length() == 0 || targetName.trim().length() == 0) {
                event.setCancelled(true);
                return;
            }

            if (event.getCurrentItem().getType() == Material.BARRIER
                    && event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel Report")) {
                player.closeInventory();
                MessageUtil.messagePlayer(player, "&cYour report has been cancelled.");
                return;
            }

            if (!Bukkit.getPlayer(targetName).isOnline()) {
                player.closeInventory();
                MessageUtil.messagePlayer(player, "&cPlayer not found.");
                return;
            }

            player.closeInventory();
            ReportManager.createReport(reason, targetName, player.getName());
            ReportManager.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
            MessageUtil.messagePlayer(player, "&aYour report has been submitted.");
        }

        if (inventory.getTitle().equals("Report Menu")) {
            if (event.getCurrentItem().getItemMeta() == null) {
                event.setCancelled(true);
                return;
            }

            if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                String targetName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                String reporterName = event.getCurrentItem().getItemMeta().getLore().get(1).replace("Reporter: ", "");

                if (event.getClick().isLeftClick()) {
                    player.closeInventory();
                    player.teleport(Bukkit.getPlayer(targetName));
                    MessageUtil.messagePlayer(player, "&eTeleporting to &a" + targetName + "&e...");
                    return;
                }

                if (event.getClick().isRightClick()) {
                    player.closeInventory();
                    ReportManager.deleteReport(targetName, reporterName, player.getName());
                    MessageUtil.messageStaff("&a" + targetName + "'s report has been deleted by &a" + player.getName() + ".");
                }
            }
        }
    }
}

