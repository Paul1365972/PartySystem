package de.paul.commands;

import java.io.IOException;
import java.util.UUID;

import de.paul.bungeecord.ErrorCodes;
import de.paul.bungeecord.RequestClient;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command implements TextMessages {

	private RequestClient requestClient;

	public PartyCommand(RequestClient requestClient) {
		super("party", null, "p");
		this.requestClient = requestClient;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			execute((ProxiedPlayer) sender, args, requestClient);
		} else {
			requestClient.reconnect();
		}
	}

	public static void execute(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		if (args.length == 0) {
			p.sendMessage(new TextComponent(SYNTAX));
			return;
		}
		if (args[0].equalsIgnoreCase("invite")) {
			invite(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("accept")) {
			accept(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("decline")) {
			decline(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("leave")) {
			leave(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("owner")) {
			owner(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("disband")) {
			disband(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("toggle")) {
			toggle(p, args, requestClient);
		} else if (args[0].equalsIgnoreCase("list")) {
			list(p, args, requestClient);
		} else {
			p.sendMessage(new TextComponent(SYNTAX));
		}
	}

	public static void list(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		try {
			UUID[] members = requestClient.getMembersByMember(p.getUniqueId());
			if (members == null) {
				p.sendMessage(new TextComponent(NO_PARTY));
				return;
			}
			p.sendMessage(new TextComponent(LIST_OWNER.concat(BungeeCord.getInstance().getPlayer(members[0]).getDisplayName())));
			for (int i = 1; i < members.length; i++) {
				p.sendMessage(new TextComponent(LIST_MEMBER.concat(BungeeCord.getInstance().getPlayer(members[i]).getDisplayName())));
			}
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void toggle(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		try {
			boolean state = requestClient.toggle(p.getUniqueId());
			p.sendMessage(new TextComponent(state ? TOGGLE_ON : TOGGLE_OFF));
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void disband(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		try {
			UUID[] members = requestClient.disband(p.getUniqueId());
			if (members == null) {
				p.sendMessage(new TextComponent(NOT_OWNER));
				return;
			}
			for (int i = 0; i < members.length; i++) {
				ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
				member.sendMessage(new TextComponent(DISBAND));
			}
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void owner(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		if (args.length < 2) {
			p.sendMessage(new TextComponent(SYNTAX_OWNER));
			return;
		}
		ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[1]);
		if (target == null) {
			p.sendMessage(new TextComponent(PLAYER_NOT_FOUND));
			return;
		}
		try {
			UUID[] members = requestClient.owner(p.getUniqueId(), target.getUniqueId());
			if (members == null) {
				p.sendMessage(new TextComponent(NOT_OWNER));
				return;
			}
			for (int i = 0; i < members.length; i++) {
				ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
				member.sendMessage(new TextComponent(NEW_OWNER.concat(p.getDisplayName())));
			}
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void leave(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		try {
			UUID[] members = requestClient.leave(p.getUniqueId());
			if (members == null) {
				p.sendMessage(new TextComponent(CANT_LEAVE));
				return;
			}
			p.sendMessage(new TextComponent(YOU_LEFT));
			for (int i = 0; i < members.length; i++) {
				ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
				member.sendMessage(new TextComponent(PLAYER_LEFT_PRE + p.getDisplayName() + PLAYER_LEFT_POST));
			}
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void decline(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		try {
			UUID source = requestClient.decline(p.getUniqueId());
			if (source.getLeastSignificantBits() == 0 && source.getMostSignificantBits() == 0) {
				p.sendMessage(new TextComponent(NO_INVITATION));
			} else {
				p.sendMessage(new TextComponent(DECLINED));
			}
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void accept(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		try {
			UUID[] members = requestClient.accept(p.getUniqueId());
			if (members == null) {
				p.sendMessage(new TextComponent(NO_INVITATION));
				return;
			}
			for (int i = 0; i < members.length; i++) {
				ProxiedPlayer member = BungeeCord.getInstance().getPlayer(members[i]);
				if (i == 0)
					p.sendMessage(new TextComponent(YOU_JOINED.concat(member.getDisplayName())));
				member.sendMessage(new TextComponent(PLAYER_JOINED.concat(p.getDisplayName())));
			}
		} catch (IOException e) {
			printError(e, p);
		}
	}

	public static void invite(ProxiedPlayer p, String[] args, RequestClient requestClient) {
		if (args.length < 2) {
			p.sendMessage(new TextComponent(SYNTAX_INVITE));
			return;
		}
		ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[1]);
		if (target == null) {
			p.sendMessage(new TextComponent(PLAYER_NOT_FOUND));
			return;
		}
		byte errorcode = -1;
		try {
			errorcode = requestClient.invite(p.getUniqueId(), target.getUniqueId());
		} catch (IOException e) {
			printError(e, p);
			return;
		}
		switch (errorcode) {
		case ErrorCodes.SUCCESS:
			p.sendMessage(new TextComponent(INVITED_TO_PARTY));
			TextComponent accept = new TextComponent(" §f[§2ACCEPT§f]");
			accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"));
			TextComponent decline = new TextComponent(" §f[§4DECLINE§f]");
			decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party decline"));
			target.sendMessage(new TextComponent(GOT_INVITED_TO_PARTY.concat(p.getDisplayName())), accept, decline);
			break;
		case ErrorCodes.INVITES_DISABLED:
			p.sendMessage(new TextComponent(INVITES_DISABLED));
			break;
		case ErrorCodes.TARGET_ALREADY_IN_PARTY:
			p.sendMessage(new TextComponent(TARGET_ALREADY_IN_PARTY));
			break;
		case ErrorCodes.YOU_ALREADY_REQUESTED:
			p.sendMessage(new TextComponent(ALREADY_INVITED));
			break;
		case ErrorCodes.OTHER_ALREADY_REQUESTED:
			p.sendMessage(new TextComponent(OTHER_ALREADY_INVITED));
			break;
		case ErrorCodes.SELF_INVITE:
			p.sendMessage(new TextComponent(SELF_INVITE));
			break;
		case ErrorCodes.NOT_OWNER:
			p.sendMessage(new TextComponent(NOT_OWNER));
			break;
		default:
			p.sendMessage(new TextComponent(UNKNOWN_ERRORCODE));
			break;
		}
	}

	private static void printError(Exception e, ProxiedPlayer p) {
		p.sendMessage(new TextComponent(UNKNOWN_ERROR_DEF.concat(e.getMessage())));
		e.printStackTrace();
	}
}
