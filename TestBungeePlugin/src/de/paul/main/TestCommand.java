package de.paul.main;

import de.paul.bungeecord.Functions;
import de.paul.bungeecord.RequestClient;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TestCommand extends Command {
	
	private RequestClient requestClient;
	
	protected TestCommand(RequestClient requestClient) {
		super("test");
		this.requestClient = requestClient;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 2)
			return;
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if (args[0].equalsIgnoreCase("move")) {
				Functions.moveParty(p.getUniqueId(), args[1], requestClient);
				p.sendMessage(new TextComponent("Executed"));
				
			}
			if (args[0].equalsIgnoreCase("resourcepack")) {
				Functions.setResourcePack(p.getUniqueId(), args[1], requestClient);
				p.sendMessage(new TextComponent("Executed"));
			}
		}
	}

}
