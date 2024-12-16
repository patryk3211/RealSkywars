package joserodpt.realskywars.api.utils;

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentUtils {
    private static TournamentUtils instance;

    public static class PlayerResults {
        private final Player owner;
        private final int score;
        private final int place;

        public PlayerResults(Player owner, int score, int place) {
            this.owner = owner;
            this.score = score;
            this.place = place;
        }

        public Player getOwner() {
            return owner;
        }

        public int getPlace() {
            return place;
        }

        public int getScore() {
            return score;
        }
    }

    public static class GameResults {
        private final int gameId;
        private final int startPlayerCount;
        private final Map<Player, PlayerResults> playerResults;
        private int playersLeft;
        private final String mapName;

        public GameResults(int id, int playerCount, String mapName) {
            this.gameId = id;
            this.startPlayerCount = playerCount;
            this.playerResults = new HashMap<>();
            this.playersLeft = playerCount;
            this.mapName = mapName;
        }

        public void rankPlayer(Player player) {
            // First place = 1, score = max(11 - place, 0) - you get 10 points for first place, 9 for second and so on, 11th and 12th place get 0 points
            int place = playersLeft--;
            PlayerResults results = new PlayerResults(player, Math.max(11 - place, 0), place);
            playerResults.put(player, results);
        }

        public int getId() {
            return gameId;
        }

        public int getInitialPlayerCount() {
            return startPlayerCount;
        }

        public Map<Player, PlayerResults> getPlayerResults() {
            return playerResults;
        }

        public Player getWinner() {
            for (PlayerResults result : playerResults.values()) {
                if(result.place == 1)
                    return result.getOwner();
            }
            return null;
        }

        public String getMapName() {
            return mapName;
        }
    }

    private final List<GameResults> games;
    private GameResults currentGame;

    private TournamentUtils() {
        games = new ArrayList<>();
        currentGame = null;
    }

    public void newGame(int playerCount, RSWMap map) {
        currentGame = new GameResults(games.size() + 1, playerCount, map.getDisplayName());
        games.add(currentGame);
    }

    public List<GameResults> getResults() {
        return games;
    }

    public void rankPlayer(RSWPlayer player) {
        currentGame.rankPlayer(player.getPlayer());
    }

    public static TournamentUtils get() {
        return instance;
    }

    public static void init() {
        instance = new TournamentUtils();
    }
}
