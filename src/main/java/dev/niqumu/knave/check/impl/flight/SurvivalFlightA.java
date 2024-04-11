package dev.niqumu.knave.check.impl.flight;

import dev.niqumu.knave.check.Check;
import dev.niqumu.knave.check.CheckResult;
import dev.niqumu.knave.check.CheckType;
import dev.niqumu.knave.player.KnavePlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Very simple check that enforces vanilla jumping motion.
 * <p>
 * The player is exempt from the check if they:
 * <ul>
 *         <li>are currently in water or lava,</li>
 *         <li>are currently on a ladder,</li>
 *         <li>are currently underneath a block,</li>
 *         <li>have recently taken damage,</li>
 *         <li>are currently falling, rather than jumping,</li>
 *         <li>or did not just change from on-ground to off-ground.</li>
 * </ul>
 * <p>
 * The check calculates the expected jumping velocity, factoring in the player's jump boost level, and compares it
 * to the actual velocity that was observed. If the difference is larger than the vanilla margin of error, the check
 * fails.
 *
 * @author niqumu
 */
public class SurvivalFlightA extends Check {

	public SurvivalFlightA(KnavePlayer player) {
		super(CheckType.FLIGHT, "SurvivalA", player);
	}

	/**
	 * Whether this check's target player is currently exempt from this check
	 *
	 * @return True if this check's player is currently exempt
	 */
	@Override
	public boolean isExempt() {

		// Ensure that the player isn't in liquid or on a ladder
		if (player.getData().isInLiquid() || player.getData().isOnLadder()) {
			return true;
		}

		// Ensure that the player isn't under a block
		if (player.getData().isUnderBlock()) {
			return true;
		}

		// Ensure that the player hasn't recently taken knockback
		if (player.getPlayer().getNoDamageTicks() > 5) {
			return true;
		}

		// Ensure that the player is actually jumping, rather than just walking off of a ledge
		if (player.getData().getMotionY() < 0) {
			return true; // The player isn't jumping and the check doesn't apply
		}

		// The check is only run when the player's ground state changes from true -> false
		return player.getData().isOnGround() || !player.getHistory().get(1).isOnGround();
	}

	/**
	 * Called whenever a packet is received from this check's target player
	 *
	 * @param packet The packet sent by this check's player
	 * @return The result of the check
	 */
	@Override
	public CheckResult onReceivePacket(Packet<?> packet) {

		if (packet instanceof PacketPlayInFlying) {

			// todo - investigate, sometimes happens when against a wall
			if (player.getData().getMotionY() - 0.404445 < 0.01) {
				return new CheckResult(true, null);
			}

			// The expected vertical motion of the player, calculated using vanilla constants
			double expectedMotionY = JUMP_MOTION_BASE;

			// If the player has jump boost, we need to account for it when calculating the expected height
			//   of the jump, as it will increase their base jumping motion
			if (player.getPlayer().hasPotionEffect(PotionEffectType.JUMP)) {

				// Iterate over potion effects to find jump boost
				for (PotionEffect effect : player.getPlayer().getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.JUMP)) {
						int level = effect.getAmplifier() + 1;
						expectedMotionY += level * JUMP_MOTION_POTION_MODIFIER;
					}
				}
			}

			// How much the expected vertical motion differs from the actual vertical motion
			double difference = Math.abs(expectedMotionY - player.getData().getMotionY());

			if (difference > JUMP_003) {
				return new CheckResult(false, String.format("Unexpected jumping vertical motion" +
					"(expected %f, got %f instead)", expectedMotionY, player.getData().getMotionY()));
			}
		}

		return new CheckResult(true, null);
	}
}
