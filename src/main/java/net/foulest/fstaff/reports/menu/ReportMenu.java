package net.foulest.fstaff.reports.menu;

import net.foulest.fstaff.reports.Report;
import net.foulest.fstaff.reports.ReportManager;
import net.foulest.fstaff.utils.ItemBuilder;
import net.foulest.fstaff.utils.MessageUtil;
import net.foulest.fstaff.utils.SkullCreatorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Foulest
 * @project FStaff
 */
public class ReportMenu {

    private final Inventory inv;

    public ReportMenu(Player player) {
        inv = Bukkit.createInventory(player, 45, "Report Menu");
        populateInventory(player);
    }

    private void populateInventory(Player player) {
        List<Report> openReports = new ArrayList<>();

        if (!ReportManager.getReports().isEmpty()) {
            for (Report report : ReportManager.getReports()) {
                Player target = Bukkit.getPlayer(report.getTargetName());

                if (target != null && target.isOnline()) {
                    if (System.currentTimeMillis() - report.getTimestamp() > 86400000) {
                        ReportManager.deleteReport(report.getTargetName(), report.getReporterName(), "Console");
                    } else {
                        openReports.add(report);
                    }
                }
            }
        }

        if (openReports.isEmpty()) {
            MessageUtil.messagePlayer(player, "&cThere are no active open reports.");
            return;
        }

        int index = 0;
        for (Report report : openReports) {
            Player target = Bukkit.getPlayer(report.getTargetName());
            String itemName = "&a" + report.getTargetName();
            List<String> itemLore = Arrays.asList("&7Reason: &f" + report.getReason(),
                    "&7Reporter: &f" + report.getReporterName(),
                    "&7Timestamp: &f" + (String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - report.getTimestamp()),
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - report.getTimestamp()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - report.getTimestamp())))
                    ), "", "&aLeft Click to spectate the player.", "&cRight Click to delete the report.");

            // Creates the ItemStack.
            ItemStack reportItem = new ItemBuilder(SkullCreatorUtil.itemFromUuid(target.getUniqueId())).name(itemName).lore(itemLore).getItem();

            // Places the ItemStack in the menu.
            inv.setItem(index++, reportItem);
        }

        player.closeInventory();
        player.openInventory(inv);
    }
}
