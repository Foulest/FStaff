package net.foulest.fstaff.reports.menu;

import net.foulest.fstaff.utils.ItemBuilder;
import net.foulest.fstaff.utils.Settings;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Foulest
 * @project FStaff
 */
public class ReasonMenu {

    private static final String REASONS_PATH = "reports.report-menu.reasons";
    private final Inventory inv;

    public ReasonMenu(Player reporter, String targetName) {
        inv = Bukkit.createInventory(reporter, Settings.reportMenuInvSize, "Reporting " + targetName);

        populateInventory(targetName);
        reporter.closeInventory();
        reporter.openInventory(inv);
    }

    private void populateInventory(String targetName) {
        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(" ").getItem();

        if (Settings.fillWithGlass) {
            for (int i = 0; i < Settings.reportMenuInvSize; i++) {
                inv.setItem(i, glass);
            }
        }

        for (String key : Settings.config.getConfigurationSection(REASONS_PATH).getKeys(false)) {
            int slot = Settings.config.getInt(REASONS_PATH + "." + key + ".slot");
            String reason = Settings.config.getString(REASONS_PATH + "." + key + ".reason");
            String itemName = Settings.config.getString(REASONS_PATH + "." + key + ".item-name");
            Material itemType = Material.getMaterial(Settings.config.getString(REASONS_PATH + "." + key + ".item-type"));

            List<String> itemLore = new ArrayList<>();

            for (String loreLine : Settings.config.getStringList(REASONS_PATH + "." + key + ".item-lore")) {
                loreLine = loreLine.replace("%target%", targetName);
                itemLore.add(loreLine);
            }

            // Creates the ItemStack.
            ItemStack reasonItem = new ItemBuilder(itemType).hideInfo().name(itemName).lore(itemLore).getItem();

            // Embeds information into the item via NMS.
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(reasonItem);
            NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
            compound.setString("targetName", targetName);
            compound.setString("reason", reason);
            nmsStack.setTag(compound);
            reasonItem = CraftItemStack.asBukkitCopy(nmsStack);

            // Places the ItemStack in the menu.
            inv.setItem(slot, reasonItem);
        }
    }
}
