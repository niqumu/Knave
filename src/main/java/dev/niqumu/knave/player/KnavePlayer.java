package dev.niqumu.knave.player;

import dev.niqumu.knave.check.Check;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class KnavePlayer {

	@Getter
	private final Player player;

	@Getter
	private final ArrayList<Check> checks;

	@Getter
	private final PlayerData data;

	public KnavePlayer(Player player) {
		this.player = player;
		this.checks = new ArrayList<>();
		this.data = new PlayerData(player);
	}
}
