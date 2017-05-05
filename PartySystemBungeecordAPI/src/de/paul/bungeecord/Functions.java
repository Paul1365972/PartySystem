package de.paul.bungeecord;

import java.io.IOException;
import java.util.UUID;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Functions {

	public static void moveParty(UUID uuid, String serverName, RequestClient requestClient) {
		try {
			ServerInfo server = BungeeCord.getInstance().getServerInfo(serverName);
			if (server == null)
				return;
			UUID[] members = requestClient.getMembersByOwner(uuid);
			if (members == null)
				return;
			for (int i = 0; i < members.length; i++) {
				ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
				member.connect(server);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setResourcePack(UUID owner, String link, RequestClient requestClient) {
		try {
			requestClient.setResourcePack(owner, link);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
