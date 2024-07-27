package joserodpt.realskywars.api.player;

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
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RSWPlayerTab {

    private final RSWPlayer player;
    private final List<Player> show = new ArrayList<>();

    public RSWPlayerTab(RSWPlayer player) {
        this.player = player;
        clear();
        updateRoomTAB();
    }

    public void add(Player p) {
        if (p.getUniqueId() != this.player.getUUID() && !this.show.contains(p)) {
            this.show.add(p);
        }
    }

    public void add(List<Player> p) {
        this.show.addAll(p);
    }

    public void remove(Player p) {
        this.show.remove(p);
    }

    public void reset() {
        this.show.addAll(Bukkit.getOnlinePlayers());
    }

    public void clear() {
        this.show.clear();
    }

    public void setHeaderFooter(String h, String f) {
        if (!this.player.isBot()) {
            this.player.getPlayer().setPlayerListHeaderFooter(Text.color(h), Text.color(f));
        }
    }


    public void updateRoomTAB() {
        if (!this.player.isBot()) {
            Bukkit.getOnlinePlayers().forEach(pl -> this.player.hidePlayer(RealSkywarsAPI.getInstance().getPlugin(), pl));
            this.show.forEach(rswPlayer -> this.player.showPlayer(RealSkywarsAPI.getInstance().getPlugin(), rswPlayer));

            String header, footer;
            if (this.player.isInMatch()) {
                header = String.join("\n", TranslatableList.TAB_HEADER_MATCH.get(this.player)).replace("%map%", this.player.getMatch().getName()).replace("%displayname%", this.player.getMatch().getDisplayName()).replace("%players%", this.player.getMatch().getPlayers().size() + "").replace("%space%", Text.makeSpace());
                footer = String.join("\n", TranslatableList.TAB_FOOTER_MATCH.get(this.player)).replace("%map%", this.player.getMatch().getName()).replace("%displayname%", this.player.getMatch().getDisplayName()).replace("%players%", this.player.getMatch().getPlayers().size() + "").replace("%space%", Text.makeSpace());
            } else {
                header = String.join("\n", TranslatableList.TAB_HEADER_OTHER.get(this.player)).replace("%players%", RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.ALL) + "").replace("%space%", Text.makeSpace());
                footer = String.join("\n", TranslatableList.TAB_FOOTER_OTHER.get(this.player)).replace("%players%", RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.ALL) + "").replace("%space%", Text.makeSpace());
            }

            this.setHeaderFooter(header, footer);
        }
    }
}
