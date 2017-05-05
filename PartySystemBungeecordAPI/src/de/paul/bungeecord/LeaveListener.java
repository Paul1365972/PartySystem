package de.paul.bungeecord;

import java.io.IOException;
import java.util.UUID;

import de.paul.commands.TextMessages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LeaveListener implements Listener, TextMessages {

	private RequestClient requestClient;

	public LeaveListener(RequestClient requestClient) {
		this.requestClient = requestClient;
	}

	@EventHandler
	public void leave(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		try {
			UUID[] members = requestClient.getMembersByOwner(p.getUniqueId());
			if (members == null) {
				members = requestClient.leave(p.getUniqueId());
				if (members == null)
					return;
				for (int i = 0; i < members.length; i++) {
					ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
					member.sendMessage(new TextComponent(PLAYER_LEFT_PRE + p.getDisplayName() + PLAYER_LEFT_POST));
				}
			} else {
				requestClient.disband(p.getUniqueId());
				for (int i = 0; i < members.length; i++) {
					ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
					member.sendMessage(new TextComponent(DISBAND));
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
