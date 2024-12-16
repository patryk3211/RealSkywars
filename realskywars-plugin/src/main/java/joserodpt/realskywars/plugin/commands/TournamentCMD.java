package joserodpt.realskywars.plugin.commands;

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.api.utils.TournamentUtils;
import joserodpt.realskywars.plugin.gui.guis.TournamentMapsListGUI;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command("tournament")
@Alias({ "trt" })
public class TournamentCMD extends CommandBase {
    public RealSkywarsAPI rs;

    public TournamentCMD(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Default
    @SuppressWarnings("unused")
    public void defaultCommand(final CommandSender commandSender) {
        Text.sendList(commandSender,
                Arrays.asList(
                        " &3/tournament start - Start tournament match on a map (opens map selector GUI)",
                        " &3/tournament stop - Forces the current match to stop",
                        " &3/tournament score - Tournament game history (reset after a server restart)",
                        " &3/tournament playerscore - Prints total scores for all players"));
    }

    @SubCommand("start")
    @Permission("rsw.tournament")
    public void startcmd(final CommandSender commandSender) {
        if(commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            Text.send(commandSender, "Select map");

            TournamentMapsListGUI v = new TournamentMapsListGUI(p);
            v.openInventory(p);
        }
    }

    @SubCommand("stop")
    @Permission("rsw.tournament")
    public void stopcmd(final CommandSender commandSender) {
        // Stop for all players
        Collection<RSWPlayer> players = rs.getPlayerManagerAPI().getPlayers();
        for (RSWPlayer player : players) {
            if (player.getMatch() != null) {
                player.getMatch().removePlayer(player);
            }
        }
    }

    @SubCommand("score")
    @Permission("rsw.tournament")
    public void score(final CommandSender commandSender, @Optional Integer gameId) {
        List<String> lines = new LinkedList<>();

        if(gameId == null) {
            // Show game ids
            List<TournamentUtils.GameResults> results = TournamentUtils.get().getResults();

            lines.add("A total of " + results.size() + " have been played:");
            for(TournamentUtils.GameResults game : results) {
                Player winner = game.getWinner();
                lines.add("  Id " + game.getId() + ": Map - " + game.getMapName() + ", Winner - " + (winner != null ? winner.getDisplayName() : "<NULL>"));
            }
        } else {
            // Show game results
            List<TournamentUtils.GameResults> resultsList = TournamentUtils.get().getResults();
            if(gameId < 1 || gameId > resultsList.size()) {
                Text.send(commandSender, "Game doesn't exist");
                return;
            }

            TournamentUtils.GameResults game = resultsList.get(gameId - 1);

            Player winner = game.getWinner();
            lines.add("Game map - " + game.getMapName());
            lines.add("Game winner - " + (winner != null ? winner.getDisplayName() : "<NULL>"));
            lines.add("Start player count - " + game.getInitialPlayerCount());
            lines.add("Player places:");

            lines.addAll(game.getPlayerResults().values()
                    .stream()
                    .sorted((a, b) -> a.getPlace() > b.getPlace() ? 1 : -1) // There are no equal items
                    .map(result -> "  " + result.getPlace() + ": " + result.getOwner().getDisplayName() + ", score = " + result.getScore())
                    .collect(Collectors.toList()));
        }

        Text.sendList(commandSender, lines);
    }

    @SubCommand("playerscore")
    @Alias("pscore")
    @Permission("rsw.tournament")
    public void playerScore(final CommandSender commandSender) {
        Map<Player, Integer> playerScores = new HashMap<>();
        for (TournamentUtils.GameResults result : TournamentUtils.get().getResults()) {
            for (TournamentUtils.PlayerResults playerResults : result.getPlayerResults().values()) {
                playerScores.compute(playerResults.getOwner(),
                        (player, current) -> current == null ? playerResults.getScore() : playerResults.getScore() + current);
            }
        }

        List<String> lines = new LinkedList<>();
        playerScores.forEach((p, v) -> {
            lines.add(p.getDisplayName() + " - Score: " + v);
        });
        Text.sendList(commandSender, lines);
    }
}
