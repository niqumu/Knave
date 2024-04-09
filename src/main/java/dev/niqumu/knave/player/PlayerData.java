package dev.niqumu.knave.player;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerData {

	private final Player player;

	@Getter
	private double x, y, z, lastX, lastY, lastZ;

	@Getter
	private double motionX, motionY, motionZ, lastMotionX, lastMotionY, lastMotionZ;

	@Getter
	private float yaw, pitch, lastYaw, lastPitch;

	@Getter
	private boolean onGround, lastOnGround;

	@Getter
	private boolean inLiquid, onLadder, underBlock;

	public PlayerData(Player player) {

		this.player = player;
		Location playerLocation = player.getLocation();

		this.x = this.lastX = playerLocation.getX();
		this.y = this.lastY = playerLocation.getY();
		this.z = this.lastZ = playerLocation.getZ();

		this.motionX = this.lastMotionX = player.getVelocity().getX();
		this.motionY = this.lastMotionY = player.getVelocity().getY();
		this.motionZ = this.lastMotionZ = player.getVelocity().getZ();

		this.yaw = this.lastYaw = playerLocation.getYaw();
		this.pitch =  this.lastPitch = playerLocation.getPitch();

		this.onGround = this.lastOnGround = this.y % 1 == 0;
	}

	public void update(Packet<?> packet) {
		if (packet instanceof PacketPlayInFlying) {
			this.updatePacketPlayInFlying((PacketPlayInFlying) packet);
		}
	}

	private void updatePacketPlayInFlying(PacketPlayInFlying packet) {

		// The packet will always include information on the client-indicated ground state
		this.lastOnGround = this.onGround;
		this.onGround = packet.f();

		// If the packet includes updated position information
		if (packet.g()) {

			// Update position information
			this.lastX = this.x;
			this.x = packet.a();

			this.lastY = this.y;
			this.y = packet.b();

			this.lastZ = this.z;
			this.z = packet.c();

			// Update velocity information
			this.lastMotionX = this.motionX;
			this.motionX = this.x - this.lastX;

			this.lastMotionY = this.motionY;
			this.motionY = this.y - this.lastY;

			this.lastMotionZ = this.motionZ;
			this.motionZ = this.z - this.lastZ;

			// Update the information on the player's whereabouts - water/ladder states, etc.
			this.updateEnvironment();
		}

		// If the packet includes updated rotation information
		if (packet.h()) {
			this.lastYaw = this.yaw;
			this.yaw = packet.d();

			this.lastPitch = this.pitch;
			this.pitch = packet.e();
		}
	}

	// todo - this needs a lot of improvement
	private void updateEnvironment() {

		Block block = player.getLocation().getBlock();

		this.inLiquid = block.isLiquid();
		this.onLadder = block.getType().equals(Material.VINE) || block.getType().equals(Material.LADDER);
		this.underBlock = !player.getLocation().add(0, 2, 0).getBlock().getType().equals(Material.AIR);
	}
}
