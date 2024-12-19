package joserodpt.realskywars.plugin.gui.guis;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.PlaceholderMode;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
import joserodpt.realskywars.api.utils.TournamentUtils;
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
import java.util.stream.Collectors;

public class TournamentMapsListGUI {

    private static final Map<UUID, TournamentMapsListGUI> inventories = new HashMap<>();
    int pageNumber = 0;
    private final Pagination<RSWMap> p;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final Inventory inv;
    private final UUID uuid;
    private RSWPlayer gp;
    private final Map<Integer, RSWMap> display = new HashMap<>();

    public TournamentMapsListGUI(RSWPlayer p) {
        this.uuid = p.getUUID();
        this.inv = Bukkit.getServer().createInventory(null, 54, TranslatableLine.MENU_MAPS_TITLE.get(p, false) + ": " + p.getPlayerMapViewerPref().getDisplayName(p));

        this.gp = p;
        List<RSWMap> items = RealSkywarsAPI.getInstance().getMapManagerAPI().getMapsForPlayer(p);

        this.p = new Pagination<>(28, items);
        fillChest(this.p.getPage(pageNumber), p);
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
                        TournamentMapsListGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
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
                            RSWMap a = current.display.get(e.getRawSlot());
                            if (!(a instanceof PlaceholderMode)) {
                                // Put all players into the selected map
                                Collection<RSWPlayer> players = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayers();
                                int playerCount = 0;
                                List<RSWPlayer> spectators = new LinkedList<>();
                                for (RSWPlayer player : players) {
                                    if (player.getPlayer().hasPermission("rsw.tournament.spectator")) {
                                        spectators.add(player);
                                    } else {
                                        a.addPlayer(player);
                                        ++playerCount;
                                    }
                                    if(playerCount >= 12)
                                        break;
                                }
                                for(RSWPlayer player : spectators) {
                                    a.spectate(player, RSWMap.SpectateType.EXTERNAL, null);
                                }

                                TournamentUtils.get().newGame(playerCount, a);
                                p.closeInventory();
                            }
                        }
                    }
                }
            }

            private void backPage(TournamentMapsListGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.gp);
            }

            private void nextPage(TournamentMapsListGUI asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.gp);
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
            register();
        }
    }

    public void fillChest(List<RSWMap> items, RSWPlayer p) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
            inv.setItem(27, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
            inv.setItem(35, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
        }

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    RSWMap s = items.get(0);
                    inv.setItem(slot, makeIcon(p, s));
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
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

    private ItemStack makeIcon(RSWPlayer p, RSWMap g) {
        if (g instanceof PlaceholderMode) {
            return Itens.createItem(Material.DEAD_BUSH, 1, TranslatableLine.ITEM_MAP_NOTFOUND_NAME.get(p));
        } else {
            return Itens.createItem(
                    g.getState().getStateMaterial(g.isRanked()),
                    Math.min(64, Math.max(1, g.getPlayerCount())),
                    TranslatableLine.ITEM_MAP_NAME.get(p)
                            .replace("%map%", g.getName())
                            .replace("%displayname%", g.getDisplayName())
                            .replace("%mode%", g.getGameMode().name()),
                    variableList(TranslatableList.ITEMS_MAP_DESCRIPTION.get(p), g));
        }
    }

    private List<String> variableList(List<String> list, RSWMap g) {
        if (g.isUnregistered()) {
            list.add("&c&lUNREGISTERED");
        }
        return list.stream()
                .map(s -> s.replace("%players%", String.valueOf(g.getPlayerCount()))
                        .replace("%maxplayers%", String.valueOf(g.getMaxPlayers())))
                .collect(Collectors.toList());
    }
}
