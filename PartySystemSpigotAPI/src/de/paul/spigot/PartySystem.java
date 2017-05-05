package de.paul.spigot;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

public class PartySystem extends JavaPlugin {
	
	@Override
	public void onEnable() {
		System.out.println("[PartySystem] Enabled");
		try {
			new RequestClient("localhost", (byte) 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		System.out.println("[PartySystem] Disabled");
	}
}
