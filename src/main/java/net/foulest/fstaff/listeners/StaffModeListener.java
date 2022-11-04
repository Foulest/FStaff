package net.foulest.fstaff.listeners;

import net.foulest.fstaff.data.PlayerData;
import net.foulest.fstaff.events.StaffModeEvent;
import net.foulest.fstaff.reports.menu.ReportMenu;
import net.foulest.fstaff.utils.ItemBuilder;
import net.foulest.fstaff.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Foulest
 * @project FStaff
 */
public class StaffModeListener implements Listener {

    private static final Random RANDOM = new Random();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onToggleStaffMode(StaffModeEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());
        PlayerData playerData = PlayerData.getInstance(player);

        if (event.isInStaffMode()) {
            player.setAllowFlight(true);
            player.setFlying(true);

            playerData.setInStaffMode(true);
            playerData.enableVanish(true);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("fstaff.staff")) {
                    online.hidePlayer(player);
                }
            }

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            player.updateInventory();

            ItemStack randomTeleport = new ItemBuilder(Material.COMPASS).name("&aRandom Teleport &7(Right Click)").getItem();
            player.getInventory().setItem(0, randomTeleport);

            ItemStack reportMenu = new ItemBuilder(Material.BOOK).name("&aReport Menu &7(Right Click)").getItem();
            player.getInventory().setItem(1, reportMenu);

            ItemStack vanishItem = new ItemBuilder(Material.INK_SACK).durability(10).name("&aVanish Enabled &7(Right Click)").getItem();
            player.getInventory().setItem(2, vanishItem);

            ItemStack exitStaffMode = new ItemBuilder(Material.BED).name("&cExit Staff Mode &7(Right Click)").getItem();
            player.getInventory().setItem(8, exitStaffMode);

        } else {
            player.setAllowFlight(false);
            player.setFlying(false);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("fstaff.staff")) {
                    online.showPlayer(player);
                }
            }

            playerData.setInStaffMode(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = PlayerData.getInstance(player);
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null
                || inventory.getTitle().contains("Reporting ")
                || event.getCurrentItem() == null) {
            return;
        }

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerData playerData = PlayerData.getInstance(player);

            if (playerData.isInStaffMode()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnterBed(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerData playerData = PlayerData.getInstance(player);

            if (playerData.isInStaffMode()) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            PlayerData playerData = PlayerData.getInstance(player);

            if (playerData.isInStaffMode()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT") && item != null) {
            switch (item.getType()) {
                case BOOK:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Report Menu")) {
                        event.setCancelled(true);

                        if (!player.hasPermission("fstaff.staff")) {
                            MessageUtil.messagePlayer(player, "&cNo permission.");
                            return;
                        }

                        new ReportMenu(player);
                    }
                    break;

                case EYE_OF_ENDER:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Staff Mode")) {
                        event.setCancelled(true);
                        player.updateInventory();

                        if (!player.hasPermission("fstaff.staff")) {
                            MessageUtil.messagePlayer(player, "&cNo permission.");
                            return;
                        }

                        playerData.enableStaffMode(false);
                    }
                    break;

                case COMPASS:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Random Teleport")) {
                        event.setCancelled(true);
                        player.updateInventory();

                        if (!player.hasPermission("fstaff.staff")) {
                            MessageUtil.messagePlayer(player, "&cNo permission.");
                            return;
                        }

                        List<Player> potentialPlayers = new ArrayList<>();

                        if (Bukkit.getOnlinePlayers().size() > 1) {
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                PlayerData onlineData = PlayerData.getInstance(online);

                                if (!onlineData.isInStaffMode()) {
                                    potentialPlayers.add(online);
                                }
                            }
                        }

                        if (!potentialPlayers.isEmpty()) {
                            Player randomPlayer;

                            if (potentialPlayers.size() == 1) {
                                randomPlayer = potentialPlayers.get(0);
                            } else {
                                randomPlayer = potentialPlayers.get(RANDOM.nextInt(potentialPlayers.size() - 1) + 1);
                            }

                            player.teleport(randomPlayer);
                            MessageUtil.messagePlayer(player, "&eTeleporting to &a" + randomPlayer.getName() + "&e...");

                        } else {
                            MessageUtil.messagePlayer(player, "&cNot enough players online to use this feature.");
                        }
                    }
                    break;

                case BED:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Staff Mode")) {
                        event.setCancelled(true);
                        player.updateInventory();

                        if (!player.hasPermission("fstaff.staff")) {
                            MessageUtil.messagePlayer(player, "&cNo permission.");
                            return;
                        }

                        playerData.disableStaffMode(false);
                    }
                    break;

                default:
                    break;
            }
        }

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
            player.updateInventory();

            if (event.getClickedBlock() != null
                    && event.getClickedBlock().getState() instanceof InventoryHolder) {
                InventoryHolder container = (InventoryHolder) event.getClickedBlock().getState();
                Inventory fakeInv = Bukkit.createInventory(player, container.getInventory().getSize(), "(Silent) Inventory");
                fakeInv.setContents(container.getInventory().getContents());

                player.openInventory(fakeInv);
                MessageUtil.messagePlayer(player, "&7&oSilently opening container...");
            }
        }
    }
}
