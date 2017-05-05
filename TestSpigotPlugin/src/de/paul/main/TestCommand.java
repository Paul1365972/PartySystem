package de.paul.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.paul.spigot.RequestClient;
import de.paul.spigot.Functions;

public class TestCommand implements CommandExecutor {
	
	private RequestClient requestClient;
	
	protected TestCommand(RequestClient requestClient) {
		this.requestClient = requestClient;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("testy"))
			return true;
		if (args.length != 2)
			return true;
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args[0].equalsIgnoreCase("move")) {
				Functions.moveParty(p.getUniqueId(), args[1], requestClient);
				p.sendMessage("Executed");
			}
			if (args[0].equalsIgnoreCase("resourcepack")) {
				Functions.setResourcePack(p.getUniqueId(), args[1], requestClient);
				p.sendMessage("Executed");
			}
		}
		return true;
	}

}
