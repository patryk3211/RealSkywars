package joserodpt.realskywars.api.database;

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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_playerdata")
public class PlayerData {
    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private @NotNull UUID uuid;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "language")
    private String lang;

    @DatabaseField(columnName = "coins")
    private Double coins;

    @DatabaseField(columnName = "prefs_mapviewer")
    private String prefs_mapviewer;

    @DatabaseField(columnName = "choosen_kit")
    private String choosen_kit;

    @DatabaseField(columnName = "prefs_cage_material")
    private String prefs_cage_material;

    @DatabaseField(columnName = "stats_wins_solo")
    private int stats_wins_solo;

    @DatabaseField(columnName = "stats_wins_ranked_solo")
    private int stats_wins_ranked_solo;

    @DatabaseField(columnName = "stats_wins_teams")
    private int stats_wins_teams;

    @DatabaseField(columnName = "stats_wins_ranked_teams")
    private int stats_wins_ranked_teams;

    @DatabaseField(columnName = "kills")
    private int kills;

    @DatabaseField(columnName = "ranked_kills")
    private int ranked_kills;

    @DatabaseField(columnName = "deaths")
    private int deaths;

    @DatabaseField(columnName = "ranked_deaths")
    private int ranked_deaths;

    @DatabaseField(columnName = "loses")
    private int loses;

    @DatabaseField(columnName = "loses_ranked")
    private int ranked_loses;

    @DatabaseField(columnName = "games_played")
    private int games_played;

    @DatabaseField(columnName = "ranked_games_played")
    private int ranked_games_played;

    @DatabaseField(columnName = "bought_items")
    private String bought_items;

    @DatabaseField(columnName = "games_list")
    private String games_list;

    public String getGames_list() {
        return this.games_list;
    }

    public String getChoosen_kit() {
        return choosen_kit;
    }

    public void setGames_list(String games_list) {
        this.games_list = games_list;
    }

    public String getName() {
        return this.name;
    }

    public String getMapViewerPref() {
        return this.prefs_mapviewer;
    }

    public String getCageMaterial() {
        return this.prefs_cage_material;
    }

    public int getStats_wins_ranked_solo() {
        return this.stats_wins_ranked_solo;
    }

    public int getStats_wins_teams() {
        return this.stats_wins_teams;
    }

    public int getStats_wins_ranked_teams() {
        return this.stats_wins_ranked_teams;
    }

    public int getKills() {
        return this.kills;
    }

    public int getRanked_kills() {
        return this.ranked_kills;
    }

    public int getLoses() {
        return this.loses;
    }

    public int getLoses_ranked() {
        return this.ranked_loses;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getStats_wins_solo() {
        return this.stats_wins_solo;
    }

    public int getRanked_deaths() {
        return this.ranked_deaths;
    }

    public int getGames_played() {
        return this.games_played;
    }

    public int getRanked_games_played() {
        return this.ranked_games_played;
    }

    public Collection<String> getBought_items() {
        return Arrays.asList(this.bought_items.split("/"));
    }

    public Double getCoins() {
        return this.coins;
    }

    public String getLanguage() {
        return this.lang;
    }

    public PlayerData(Player p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.coins = 0D;
        this.lang = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getDefaultLanguage();
        this.bought_items = "";
        this.prefs_mapviewer = "MAPV_ALL";
        this.prefs_cage_material = "GLASS";
    }

    public PlayerData() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) {
        this.lang = language;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void setCageBlock(String name) {
        this.prefs_cage_material = name;
    }

    public void setMapViewerPref(String name) {
        this.prefs_mapviewer = name;
    }

    public void setBoughtItems(List<String> boughtItems) {
        this.bought_items = String.join("/", boughtItems);
    }

    public void setWinsSolo(Object a, boolean b) {
        if (b) {
            this.stats_wins_ranked_solo = (int) a;
        } else {
            this.stats_wins_solo = (int) a;
        }
    }

    public void setWinsTeams(Object a, boolean b) {
        if (b) {
            this.stats_wins_ranked_teams = (int) a;
        } else {
            this.stats_wins_teams = (int) a;
        }
    }

    public void setKills(Object a, boolean b) {
        if (b) {
            this.ranked_kills = (int) a;
        } else {
            this.kills = (int) a;
        }
    }

    public void setDeaths(Object a, boolean b) {
        if (b) {
            this.ranked_deaths = (int) a;
        } else {
            this.deaths = (int) a;
        }
    }

    public void setLoses(Object a, boolean b) {
        if (b) {
            this.ranked_loses = (int) a;
        } else {
            this.loses = (int) a;
        }
    }

    public void setGamesPlayed(Object a, boolean b) {
        if (b) {
            this.ranked_games_played = (int) a;
        } else {
            this.games_played = (int) a;
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setKit(String name) {
        this.choosen_kit = name;
    }
}