package de.paul.main;

import java.io.IOException;

import de.paul.bungeecord.RequestClient;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class Test extends Plugin {
	private RequestClient requestClient;
	
	@Override
	public void onEnable() {
		System.out.println("TEST PLUGIN ENABLED");
		try {
			requestClient = new RequestClient("localhost");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BungeeCord.getInstance().pluginManager.registerCommand(this, new TestCommand(requestClient));
	}
	
}
