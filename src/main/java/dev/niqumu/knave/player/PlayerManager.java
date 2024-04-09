package dev.niqumu.knave.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

	private final HashMap<Player, KnavePlayer> playerMap;

	public PlayerManager() {
		this.playerMap = new HashMap<>();

		// Register all currently connected players
		Bukkit.getOnlinePlayers().forEach(this::register);
	}

	public void register(Player player) {
		playerMap.put(player, new KnavePlayer(player));
	}

	public void remove(Player player) {
		playerMap.remove(player);
	}

	public KnavePlayer get(Player player) {
		return playerMap.get(player);
	}
}
