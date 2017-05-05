package de.paul.main;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import de.paul.spigot.RequestClient;

public class Test extends JavaPlugin {
	private RequestClient requestClient;
	
	@Override
	public void onEnable() {
		System.out.println("TEST PLUGIN ENABLED");
		try {
			requestClient = new RequestClient("localhost");
		} catch (IOException e) {
			e.printStackTrace();
		}
		getCommand("testy").setExecutor(new TestCommand(requestClient));
	}
	
}
