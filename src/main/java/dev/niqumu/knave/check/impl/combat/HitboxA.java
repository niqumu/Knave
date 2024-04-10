package dev.niqumu.knave.check.impl.combat;

import dev.niqumu.knave.Knave;
import dev.niqumu.knave.check.Check;
import dev.niqumu.knave.check.CheckResult;
import dev.niqumu.knave.check.CheckType;
import dev.niqumu.knave.player.KnavePlayer;
import dev.niqumu.knave.util.client.AxisAlignedBB;
import dev.niqumu.knave.util.client.MovingObjectPosition;
import dev.niqumu.knave.util.client.Vec3;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Reach check, strongly based on vanilla client-side code.
 * <p>
 * TODO: This might not work very well on high ping. Some lag compensation is needed.
 *
 * @author niqumu
 */
public class HitboxA extends Check {

	public HitboxA(KnavePlayer player) {
		super(CheckType.COMBAT, "HitboxA", player);
	}

	/**
	 * Whether this check's target player is currently exempt from this check
	 *
	 * @return True if this check's player is currently exempt
	 */
	@Override
	public boolean isExempt() {
		return false;
	}

	/**
	 * Called whenever a packet is received from this check's target player
	 *
	 * @param packet The packet sent by this check's player
	 * @return The result of the check
	 */
	@Override
	@SneakyThrows
	public CheckResult onReceivePacket(Packet<?> packet) {

		if (!(packet instanceof PacketPlayInUseEntity)) {
			return null;
		}

		PacketPlayInUseEntity usePacket = (PacketPlayInUseEntity) packet;

		Field targetIDField = PacketPlayInUseEntity.class.getDeclaredField("a");
		targetIDField.setAccessible(true);

		int targetID = targetIDField.getInt(usePacket);
		Entity target = null;

		for (Entity entity : player.getPlayer().getWorld().getEntities()) {
			if (entity.getEntityId() == targetID) {
				target = entity;
				break;
			}
		}

		// Ensure we got the target entity okay
		if (target == null) {
			Knave.log(ChatColor.RED + "HitboxA: Failed to get entity %d", targetID);
			return null;
		}

		// We're only concerned about PvP (todo - this might change later)
		if (!(target instanceof Player)) {
			return null;
		}

		// We're only concerned with attack packets here
		if (!usePacket.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {
			return null;
		}

		// Define the attacker's view vector
		double eyeHeight = player.getPlayer().isSneaking() ? EYE_HEIGHT_SNEAKING : EYE_HEIGHT_STANDING;
		Vec3 eyeRotation = getRotationVector(player.getData().getYaw(), player.getData().getPitch());
		Vec3 eyePosition = new Vec3(player.getData().getX(), player.getData().getY() + eyeHeight, player.getData().getZ());
		Vec3 eyeRayEnd = eyePosition.addVector(eyeRotation.xCoord * 6, eyeRotation.yCoord * 6, eyeRotation.zCoord * 6);

		// Define the target's hitbox
		AxisAlignedBB hitbox = new AxisAlignedBB(
			target.getLocation().getX() - PLAYER_HITBOX_WIDTH / 2D,
			target.getLocation().getY(),
			target.getLocation().getZ() - PLAYER_HITBOX_WIDTH / 2D,
			target.getLocation().getX() + PLAYER_HITBOX_WIDTH / 2D,
			target.getLocation().getY() + PLAYER_HITBOX_HEIGHT,
			target.getLocation().getZ() + PLAYER_HITBOX_WIDTH / 2D
		).expand(0.1f, 0.1f, 0.1f);

		MovingObjectPosition intercept = hitbox.calculateIntercept(eyePosition, eyeRayEnd);

		// If the ray trace intersects with the hitbox, ensure the distance is possible on vanilla
		if (intercept != null) {

			double reach = intercept.hitVec.distanceTo(eyePosition);

			boolean creative = player.getPlayer().getGameMode().equals(GameMode.CREATIVE);
			float maxReach = creative ? 4.5f : 3f;

			// The player is using reach
			if (reach > maxReach) {
				return new CheckResult(false, String.format("Attacked from too far " +
					"(Maximum reach: %f, observed reach: %f)", maxReach, reach));
			}

		}

		// If the ray trace does not intersect with the hitbox, the client has expanded the hitbox illegally
		else {
			return new CheckResult(false, "Attacked while looking outside of the target's hitbox");
		}

		return new CheckResult(true, null);
	}

	private Vec3 getRotationVector(float yaw, float pitch) {
		double f = Math.cos(-yaw * 0.017453292F - (float) Math.PI);
		double f1 = Math.sin(-yaw * 0.017453292F - (float) Math.PI);
		double f2 = -Math.cos(-pitch * 0.017453292F);
		double f3 = Math.sin(-pitch * 0.017453292F);

		return new Vec3(f1 * f2, f3, f * f2);
	}
}
