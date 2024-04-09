package dev.niqumu.knave;

import dev.niqumu.knave.check.CheckManager;
import dev.niqumu.knave.listener.PlayerListener;
import dev.niqumu.knave.networking.NetworkManager;
import dev.niqumu.knave.player.PlayerManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author niqumu
 */
public class Knave extends JavaPlugin {

	public static final String VERSION = "0.0.1";
	public static final String CHAT_PREFIX = "§7[§4K§8nave§7]";

	public static Knave INSTANCE;

	@Getter
	private PlayerListener playerListener;

	@Getter
	private PlayerManager playerManager;

	@Getter
	private CheckManager checkManager;

	@Getter
	private NetworkManager networkManager;

	@Override
	public void onEnable() {
		INSTANCE = this;

		this.playerListener = new PlayerListener();
		this.playerManager = new PlayerManager();
		this.checkManager = new CheckManager();
		this.networkManager = new NetworkManager();

		broadcast("Enabled Knave version " + VERSION);
	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach(this.networkManager::ejectPlayer);
	}

	public static void log(Object message, Object... args) {
		Bukkit.getServer().getConsoleSender().sendMessage(CHAT_PREFIX + " §f" +
			String.format(message.toString(), args));
	}

	public static void broadcast(Object message, Object... args) {
		Bukkit.getServer().broadcast(CHAT_PREFIX + " §f" +
			String.format(message.toString(), args), "bukkit.broadcast.admin");
	}
}
