package de.paul.commands;

public interface TextMessages {
	
	public static final String GREEN = "§2[PartySystem] ";
	public static final String RED = "§4[PartySystem] ";
	public static final String BLUE = "§b[PartySystem] ";
	
	public static final String SYNTAX = RED + "§cSyntax: /party <invite/accept/decline/leave/owner/disband/toggle/list>";
	
	public static final String PLAYER_NOT_FOUND = RED  + "§cPlayer not found!";
	public static final String UNKNOWN_ERROR_DEF = RED + "§4Unknown Error: ";
	public static final String UNKNOWN_ERROR = RED + "§4Unknown Error";
	
	public static final String SYNTAX_INVITE = RED + "§cSyntax: /party invite <player>";
	public static final String INVITES_DISABLED = RED + "§cInvites are disabled for this Player";
	public static final String ALREADY_INVITED = RED + "§cYou already invited this Player";
	public static final String OTHER_ALREADY_INVITED = RED + "§cYou already got invited this Player. Accept it with /party accept";
	public static final String TARGET_ALREADY_IN_PARTY = RED + "§cThis Player is already in a party";
	public static final String UNKNOWN_ERRORCODE = RED + "§cUnknown Errorcode. Blame the Devs";
	public static final String INVITED_TO_PARTY = GREEN + "§eInvited Player to your Party";
	public static final String GOT_INVITED_TO_PARTY = GREEN + "§eYou got invited to a Party by §6";
	public static final String SELF_INVITE = RED + "§cYou cant invite yourself to a party";
	
	public static final String PLAYER_JOINED = GREEN + "§aNew Member §6";
	public static final String YOU_JOINED = GREEN + "§aYou joined the party of §6";
	public static final String NOT_OWNER = RED + "§cOnly a party owner is allowed to do that";
	public static final String NO_INVITATION = RED + "§eYou got no invitation requests pending";
	
	public static final String DECLINED = GREEN + "§aDeclined Invitation";
	
	public static final String CANT_LEAVE = RED + "§cCant leave Party. If you are the owner try /party disband";
	public static final String YOU_LEFT = GREEN + "§aLeft Party";
	public static final String PLAYER_LEFT_PRE = GREEN + "§aMember §6";
	public static final String PLAYER_LEFT_POST = "§a left the Party";
	
	public static final String NEW_OWNER = GREEN + "§aNow Party owner is §6";
	
	public static final String DISBAND = GREEN + "§cParty disbanded";
	
	public static final String TOGGLE_ON = GREEN + "§aYou will receive invitations again";
	public static final String TOGGLE_OFF = GREEN + "§cAll invitations will be blocked";
	
	public static final String NO_PARTY = RED + "§cYou are in no Party";
	
	public static final String LIST_OWNER = GREEN + "§4Owner: §6";
	public static final String LIST_MEMBER = GREEN + "§bMembers: §6";
	
	public static final String MOVE = GREEN + "§eParty moved to §6";
	
	public static final String SERVER_NOT_FOUND =RED + "§cServer not found";
	
	public static final String SYNTAX_OWNER = RED + "§cSyntax: /party owner <player>";
}
