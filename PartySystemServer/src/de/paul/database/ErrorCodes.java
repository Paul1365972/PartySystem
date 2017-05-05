package de.paul.database;

public abstract interface ErrorCodes {
	public static final byte SUCCESS = 0;
	public static final byte INVITES_DISABLED = 1;
	public static final byte TARGET_ALREADY_IN_PARTY = 2;
	public static final byte YOU_ALREADY_REQUESTED = 3;
	public static final byte OTHER_ALREADY_REQUESTED = 4;
	public static final byte SELF_INVITE = 5;
	public static final byte NOT_OWNER = 6;
}