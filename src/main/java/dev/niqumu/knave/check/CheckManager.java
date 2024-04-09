package dev.niqumu.knave.check;

import com.avaje.ebean.Update;
import dev.niqumu.knave.Knave;
import dev.niqumu.knave.check.impl.flight.SurvivalFlightA;
import dev.niqumu.knave.player.KnavePlayer;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class CheckManager implements Listener {

	public static final ArrayList<Class<? extends Check>> CHECK_CLASSES = new ArrayList<>();
	static {

		// Flight
		CHECK_CLASSES.add(SurvivalFlightA.class);
	}

	public CheckManager() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Knave.INSTANCE);

		// Register all currently connected players
		Bukkit.getOnlinePlayers().forEach(this::register);
	}

	public void register(Player player) {
		this.createChecks(Knave.INSTANCE.getPlayerManager().get(player));
	}

	@SneakyThrows
	private void createChecks(KnavePlayer player) {
		for (Class<? extends Check> checkClass : CHECK_CLASSES) {
			Check check = checkClass.getDeclaredConstructor(KnavePlayer.class).newInstance(player);
			player.getChecks().add(check);
		}
	}

	public Check getCheck(KnavePlayer player, Class<? extends Check> check) {
		for (Check playerCheck : player.getChecks()) {
			if (playerCheck.getClass().equals(check)) {
				return playerCheck;
			}
		}

		return null; // No matching check was found
	}

	public void onReceivePacket(Object packet, Player player) {

		KnavePlayer knavePlayer = Knave.INSTANCE.getPlayerManager().get(player);

		// Update the player's data
		knavePlayer.getData().update((Packet<?>) packet);

		// Iterate over all the player's checks
		knavePlayer.getChecks().forEach(check -> {

			// Do nothing if the player is currently exempt from the check
			if (check.isExempt()) {
				return;
			}

			// Run the check and store the result
			CheckResult result = check.onReceivePacket((Packet<?>) packet);

			// todo - do more than just log failed checks
			if (!result.isPassed()) {
				Knave.broadcast("%s failed §4%s.§4%s§f: §7%s", player.getName(),
					check.getType().getName(), check.getId(), result.getMessage());
			}
		});
	}

	public void onSendPacket(Object packet, Player player) {
		// todo - is this even needed?
	}
}
