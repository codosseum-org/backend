package org.developerden.codosseum.mode;

import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.PlayerRoundResult;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FastestGameMode implements GameMode {
    @Override
    public GameModeType getType() {
        return GameModeType.FASTEST;
    }

    @Override
    public double computeScore(Player player, List<PlayerRoundResult> results) {
        var orderedScores = results.stream()
                .sorted(Comparator.comparing(PlayerRoundResult::timeLeft).reversed())
                .toList();

        // find the result of the player
        Optional<PlayerRoundResult> any = results.stream()
                .filter(res -> res.player().equals(player))
                .findAny();

        // warn?
        return any.map(playerRoundResult -> orderedScores.indexOf(playerRoundResult) + 1.0)
                .orElse(0.0);
    }
}
