package de.paul.spigot;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Functions {

	public static void moveParty(UUID uuid, String server, RequestClient requestClient) {
		try {
			requestClient.moveParty(uuid, server);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setResourcePack(UUID owner, String link, RequestClient requestClient) {
		try {
			UUID[] members = requestClient.getMembersByOwner(owner);
			if (members == null)
				return;
			for (int i = 0; i < members.length; i++) {
				Player member = Bukkit.getPlayer(members[i]);
				if (member != null)
					member.setResourcePack(link);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
