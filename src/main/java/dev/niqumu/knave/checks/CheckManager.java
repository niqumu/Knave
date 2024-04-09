package dev.niqumu.knave.checks;

import dev.niqumu.knave.Knave;
import dev.niqumu.knave.player.KnavePlayer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class CheckManager implements Listener {

	public static final ArrayList<Class<? extends Check>> CHECK_CLASSES = new ArrayList<>();

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
			player.getChecks().add(checkClass.newInstance());
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

	}

	public void onSendPacket(Object packet, Player player) {

	}
}
