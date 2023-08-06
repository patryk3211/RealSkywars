package josegamerpt.realskywars.gui.guis;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.gui.GUIManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWGameLog;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class GameLogViewer {

    static final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private static final Map<UUID, GameLogViewer> inventories = new HashMap<>();
    private final Inventory inv;
    private final UUID uuid;
    private final HashMap<Integer, RSWGameLog> display = new HashMap<>();
    int pageNumber = 0;
    Pagination<RSWGameLog> p;

    public GameLogViewer(RSWPlayer p) {
        this.uuid = p.getUUID();
        this.inv = Bukkit.getServer().createInventory(null, 54, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_GAME_HISTORY, false));

        ArrayList<RSWGameLog> items = p.getGamesList();

        if (items.isEmpty()) {
            items.add(new RSWGameLog());
        }

        this.p = new Pagination<>(28, items);
        fillChest(this.p.getPage(this.pageNumber));

        this.register();
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
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        GameLogViewer current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywars.getPlugin().getPlayerManager().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                clicker.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();
                                }
                                GUIManager.openPlayerMenu(p);
                                break;
                            case 26:
                            case 35:
                                if (!current.lastPage()) {
                                    nextPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                            case 18:
                            case 27:
                                if (!current.firstPage()) {
                                    backPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            RSWGameLog a = current.display.get(e.getRawSlot());

                        }
                    }
                }
            }

            private void backPage(GameLogViewer asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(GameLogViewer asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
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

    private boolean lastPage() {
        return pageNumber == (p.totalPages() - 1);
    }

    private boolean firstPage() {
        return pageNumber == 0;
    }

    public void fillChest(List<RSWGameLog> items) {

        inv.clear();

        for (int i = 0; i < 9; ++i) {
            inv.setItem(i, placeholder);
        }

        display.clear();

        inv.setItem(45, placeholder);
        inv.setItem(46, placeholder);
        inv.setItem(47, placeholder);
        inv.setItem(48, placeholder);
        inv.setItem(49, placeholder);
        inv.setItem(50, placeholder);
        inv.setItem(51, placeholder);
        inv.setItem(52, placeholder);
        inv.setItem(53, placeholder);
        inv.setItem(36, placeholder);
        inv.setItem(44, placeholder);
        inv.setItem(9, placeholder);
        inv.setItem(17, placeholder);

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
            inv.setItem(27, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
            inv.setItem(35, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
        }

        inv.setItem(49, Itens.createItemLore(Material.CHEST, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))));

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    RSWGameLog s = items.get(0);
                    inv.setItem(slot, s.getItem());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            slot++;
        }
    }

    public void openInventory(RSWPlayer player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getPlayer().getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getPlayer().getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.getPlayer().openInventory(inv);
            }
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}