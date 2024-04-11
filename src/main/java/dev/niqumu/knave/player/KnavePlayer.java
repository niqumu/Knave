package dev.niqumu.knave.player;

import dev.niqumu.knave.check.Check;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;

public class KnavePlayer {

	@Getter
	private final Player player;

	@Getter
	private final ArrayList<Check> checks;

	/**
	 * The last 20 {@link PlayerData} objects, representing the last 20 ticks of this player's flying packets
	 */
	@Getter
	private final LinkedList<PlayerData> history = new LinkedList<>();

	/**
	 * The current {@link PlayerData} object, representing the data of this player's last flying packet
	 */
	@Getter
	private PlayerData data;

	public KnavePlayer(Player player) {
		this.player = player;
		this.checks = new ArrayList<>();
		this.data = new PlayerData(player);
	}

	/**
	 * Called every time the player sends a flying packet. Analyze it, and store it in the data history, bumping
	 *   the last element if necessary
	 * @param packet The packet sent by the client
	 */
	public void update(PacketPlayInFlying packet) {
		PlayerData latestData = new PlayerData(this.player);
		latestData.update(packet);

		this.history.addFirst(latestData);
		this.data = latestData;

		if (this.history.size() > 20) {
			this.history.removeLast();
		}
	}
}
