package joserodpt.realskywars.plugin.managers;

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

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWKitsConfig;
import joserodpt.realskywars.api.database.PlayerBoughtItemsRow;
import joserodpt.realskywars.api.kits.KitInventory;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.KitManagerAPI;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitManager extends KitManagerAPI {

    private final Map<String, RSWKit> kits = new HashMap<>();

    @Override
    public void loadKits() {
        this.kits.clear();
        // No kits are allowed on this server.
    }

    @Override
    public void registerKit(RSWKit k) {
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Display-Name", k.getDisplayName());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Price", k.getPrice());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Icon", k.getMaterial().name());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Permission", k.getPermission());

        if (!k.getKitPerks().isEmpty()) {
            RSWKitsConfig.file().set("Kits." + k.getName() + ".Perks", k.getKitPerks().stream().map(Enum::name).collect(Collectors.toList()));
        }

        RSWKitsConfig.file().set("Kits." + k.getName() + ".Contents", k.getKitInventory().getSerialized());
        RSWKitsConfig.save();
    }

    @Override
    public void unregisterKit(RSWKit k) {
        this.getKits().remove(k);
        RSWKitsConfig.file().remove("Kits");
        this.getKits().forEach(this::registerKit);
        RSWKitsConfig.save();
    }

    @Override
    public Collection<RSWKit> getKits() {
        return this.kits.values();
    }

    @Override
    public Collection<RSWBuyableItem> getKitsAsBuyables() {
        return new ArrayList<>(this.kits.values());
    }

    @Override
    public RSWKit getKit(String string) {
        return this.kits.get(string);
    }

    @Override
    public RSWKit getKit(PlayerBoughtItemsRow playerBoughtItemsRow) {
        return getKit(playerBoughtItemsRow.getItemID());
    }
}