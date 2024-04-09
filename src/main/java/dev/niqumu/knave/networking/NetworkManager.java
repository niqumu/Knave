package dev.niqumu.knave.networking;

import dev.niqumu.knave.Knave;
import io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NetworkManager {

	public NetworkManager() {

		// Inject all currently connected players
		Bukkit.getOnlinePlayers().forEach(this::ejectPlayer);
		Bukkit.getOnlinePlayers().forEach(this::injectPlayer);
	}

	public void injectPlayer(Player player) {

		// Create a new netty handler that sends the check manager a copy of the packet
		ChannelDuplexHandler handler = new ChannelDuplexHandler() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {

				// Call the ReceivePacket event and pass the packet along as normal
				Knave.INSTANCE.getCheckManager().onReceivePacket(packet, player);
				super.channelRead(ctx, packet);
			}

			@Override
			public void write(ChannelHandlerContext ctx, Object packet,
			                  ChannelPromise promise) throws Exception {

				// Call the SendPacket event and pass the packet along as normal
				Knave.INSTANCE.getCheckManager().onSendPacket(packet, player);
				super.write(ctx, packet, promise);
			}
		};

		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().
			playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), handler);
	}

	public void ejectPlayer(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> channel.pipeline().remove(player.getName()));
	}
}
