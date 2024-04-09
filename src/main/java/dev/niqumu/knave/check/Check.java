package dev.niqumu.knave.check;

import dev.niqumu.knave.player.KnavePlayer;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;

public abstract class Check {

	/**
	 * The type of this check
	 * @see CheckType
	 */
	@Getter
	private final CheckType type;

	@Getter
	private final String id;

	@Getter
	protected final KnavePlayer player;

	public Check(CheckType type, String id, KnavePlayer player) {
		this.type = type;
		this.id = id;
		this.player = player;
	}

	/**
	 * Whether this check's target player is currently exempt from this check
	 * @return True if this check's player is currently exempt
	 */
	public abstract boolean isExempt();

	/**
	 * Called whenever a packet is received from this check's target player
	 * @param packet The packet sent by this check's player
	 * @return The result of the check
	 */
	public abstract CheckResult onReceivePacket(Packet<?> packet);
}
