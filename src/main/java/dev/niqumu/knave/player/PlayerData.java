package dev.niqumu.knave.player;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * A container to store the state of a player's position, rotation, and ground state.
 * <p>
 * The {@link KnavePlayer} class updates the player's current PlayerData instance every time the client sends a
 * flying packet. Additionally, the last 20 PlayerData snapshots are stored.
 *
 * @author niqumu
 */
public class PlayerData {

	private final Player player;

	@Getter
	private double x, y, z;

	@Getter
	private double motionX, motionY, motionZ;

	@Getter
	private float yaw, pitch;

	@Getter
	private boolean onGround;

	@Getter
	private boolean inLiquid, onLadder, underBlock;

	public PlayerData(Player player) {

		this.player = player;
		Location playerLocation = player.getLocation();

		this.x = playerLocation.getX();
		this.y = playerLocation.getY();
		this.z = playerLocation.getZ();

		this.motionX = player.getVelocity().getX();
		this.motionY = player.getVelocity().getY();
		this.motionZ = player.getVelocity().getZ();

		this.yaw = playerLocation.getYaw();
		this.pitch = playerLocation.getPitch();

		this.onGround = this.y % 1 == 0;
	}

	public void update(Packet<?> packet) {
		if (packet instanceof PacketPlayInFlying) {
			this.updatePacketPlayInFlying((PacketPlayInFlying) packet);
		}
	}

	private void updatePacketPlayInFlying(PacketPlayInFlying packet) {

		// The packet will always include information on the client-indicated ground state
		this.onGround = packet.f();

		// If the packet includes updated position information
		if (packet.g()) {

			// Update velocity information
			this.motionX = packet.a() - this.x;
			this.motionY = packet.b() - this.y;
			this.motionZ = packet.c() - this.z;

			// Update position information
			this.x = packet.a();
			this.y = packet.b();
			this.z = packet.c();

			// Update the information on the player's whereabouts - water/ladder states, etc.
			this.updateEnvironment();
		}

		// If the packet includes updated rotation information
		if (packet.h()) {
			this.yaw = packet.d();
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
