package dev.niqumu.knave.listener;

import dev.niqumu.knave.Knave;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	public PlayerListener() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Knave.INSTANCE);
	}

	/**
	 * Called when a player connects to the server. Register the player properly, setting up hooks, checks, and
	 * player data
	 * @param event The PlayerJoinEvent passed when the player connected to this server
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoin(PlayerJoinEvent event) {

		// Register the player in the player manager and create their checks
		Knave.INSTANCE.getPlayerManager().register(event.getPlayer());
		Knave.INSTANCE.getCheckManager().register(event.getPlayer());

		// Create the hooks in the player's connection with the server to read/write packets
		Knave.INSTANCE.getNetworkManager().injectPlayer(event.getPlayer());
	}

	/**
	 * Called when a player voluntarily disconnects from the server. Unregisters the player properly, removing the
	 * network hooks, as well as freeing checks and player data
	 * @param event The PlayerJoinEvent passed when the player connected to this server
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerQuit(PlayerQuitEvent event) {

		// Unregister the player in the player manager
		Knave.INSTANCE.getPlayerManager().remove(event.getPlayer());

		// Remove the hooks in the player's connection with the server
		Knave.INSTANCE.getNetworkManager().ejectPlayer(event.getPlayer());
	}

	/**
	 * Called when a player is kicked from the server. Unregisters the player properly, removing the network hooks,
	 * as well as freeing checks and player data
	 * @param event The PlayerJoinEvent passed when the player connected to this server
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerKick(PlayerKickEvent event) {

		// Unregister the player in the player manager
		Knave.INSTANCE.getPlayerManager().remove(event.getPlayer());

		// Remove the hooks in the player's connection with the server
		Knave.INSTANCE.getNetworkManager().ejectPlayer(event.getPlayer());
	}
}
