/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.events;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.daboross.bukkitdev.skywars.game.ArenaGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Dabo Ross <http://www.daboross.net/>
 */
@EqualsAndHashCode
public class GameStartInfo {

    @Getter
    private final List<Player> players;
    @Getter
    private final ArenaGame game;

    public GameStartInfo(ArenaGame game) {
        if (game == null) {
            throw new IllegalArgumentException();
        }
        this.game = game;
        List<String> playersList = game.getAlivePlayers();
        this.players = new ArrayList<>(playersList.size());
        for (int i = 0; i < playersList.size(); i++) {
            Player p = Bukkit.getPlayer(playersList.get(i));
            if (p == null) {
                throw new IllegalArgumentException("Player " + playersList.get(i) + " not online");
            }
            players.add(p);
        }
    }
}
