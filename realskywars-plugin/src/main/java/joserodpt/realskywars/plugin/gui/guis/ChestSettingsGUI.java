package joserodpt.realskywars.plugin.gui.guis;

import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.chests.RSWGroupedChest;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.PlayerInput;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChestSettingsGUI {
    private static final Map<UUID, ChestSettingsGUI> inventories = new HashMap<>();
    private Inventory inv;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack confirm = Itens.createItem(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to confirm your settings."));

    private final UUID uuid;
    private final RSWMap map;
    private RSWChest chest;

    public ChestSettingsGUI(Player p, RSWMap map, RSWChest chest) {
        this.uuid = p.getUniqueId();
        this.map = map;
        this.chest = chest;

        inv = Bukkit.getServer().createInventory(null, 45, Text.color("Chest settings"));

        loadInv();
    }

    public ChestSettingsGUI(RSWPlayer p, RSWMap map, RSWChest chest) {
        this.uuid = p.getUUID();
        this.map = map;
        this.chest = chest;

        inv = Bukkit.getServer().createInventory(null, 45, Text.color("Chest settings"));

        loadInv();
    }

    private void loadInv() {
        inv.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 17, 26, 35, 44, 37, 38, 39, 41, 42, 43}) {
            inv.setItem(slot, placeholder);
        }

        int chestGroupId = (this.chest instanceof RSWGroupedChest) ? ((RSWGroupedChest) this.chest).getGroup().id : -1;
        for(int i = 0; i < 6; ++i) {
            if(chestGroupId == i + 1)
                inv.setItem(10 + i, Itens.createItem(Material.TORCH, i + 1, "Group " + (i + 1)));
            else
                inv.setItem(10 + i, Itens.createItem(Material.REDSTONE_TORCH, i + 1, "Group " + (i + 1)));
        }
        if(chestGroupId == -1)
            inv.setItem(16, Itens.createItem(Material.GLOWSTONE, 1, "No group"));
        else
            inv.setItem(16, Itens.createItem(Material.BARRIER, 1, "No group"));

        inv.setItem(40, confirm);
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    if (e.getCurrentItem() == null) {
                        return;
                    }
                    Player p = (Player) clicker;
                    if (p != null) {
                        UUID uuid = p.getUniqueId();
                        if (inventories.containsKey(uuid)) {
                            ChestSettingsGUI current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

                            //Settings
                            switch (e.getRawSlot()) {
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                    if(current.chest instanceof RSWGroupedChest) {
                                        RSWGroupedChest groupChest = (RSWGroupedChest) current.chest;
                                        groupChest.setGroup(current.map.getChestGroup(e.getRawSlot() - 10 + 1));
                                    } else {
                                        RSWGroupedChest groupChest = new RSWGroupedChest(current.chest.getType(), current.chest.getLocation(), current.chest.getFace(), current.map.getChestGroup(e.getRawSlot() - 10 + 1));
                                        current.map.chests.remove(current.chest.getLocation());
                                        current.map.chests.put(groupChest.getLocation(), groupChest);
                                        current.chest = groupChest;
                                    }
                                    break;
                                case 16:
                                    if(current.chest instanceof RSWGroupedChest) {
                                        RSWChest newChest = new RSWChest(current.chest.getType(), current.chest.getLocation(), current.chest.getFace());
                                        current.map.chests.remove(current.chest.getLocation());
                                        current.map.chests.put(newChest.getLocation(), newChest);
                                        current.chest = newChest;
                                    }
                                    break;
                                case 40:
                                    current.map.save(RSWMap.Data.CHESTS, true);
                                    p.closeInventory();
                                    break;

                            }
                            current.loadInv();
                        }
                    }
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unregister();
                    }
                }
            }
        };
    }

    public void openInventory(Player player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.openInventory(inv);
            }
            register();
        }
    }

    public void openInventory(RSWPlayer player) {
        openInventory(player.getPlayer());
    }

    private Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
