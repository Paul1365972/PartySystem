package de.paul.bungeecord;

import java.io.IOException;

import de.paul.commands.PartyCommand;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class PartySystem extends Plugin {

	private RequestClient requestClient;

	@Override
	public void onEnable() {
		System.out.println("[PartySystem] Enabled");
		try {
			requestClient = new RequestClient("localhost", (byte) 2);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("[PartySystem] Error couldnt connect to Server");
		}
		BungeeCord.getInstance().pluginManager.registerCommand(this, new PartyCommand(requestClient));
		BungeeCord.getInstance().pluginManager.registerListener(this, new LeaveListener(requestClient));
	}

	@Override
	public void onDisable() {
		System.out.println("[PartySystem] Disabled");
	}

	public RequestClient getRequestClient() {
		return requestClient;
	}

}
