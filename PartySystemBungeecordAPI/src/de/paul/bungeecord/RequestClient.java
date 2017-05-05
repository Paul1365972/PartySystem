package de.paul.bungeecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

public class RequestClient implements DataConstants {

	private String host;
	private int databaseport;
	private int passport;
	private DataInputStream databasedis;
	private DataOutputStream databasedos;
	private byte type;

	private DataInputStream passdis;
	private DataOutputStream passdos;

	//Sends a Party Request from the source to the target and returns error code
	public byte invite(UUID source, UUID target) throws IOException {
		nonNull(source, target);
		databasedos.writeByte(INVITE);
		sendUUID(source);
		sendUUID(target);
		return databasedis.readByte();
	}

	//Accepts the current Invitation and returns the UUIDs of the party
	public UUID[] accept(UUID source) throws IOException {
		nonNull(source);
		databasedos.writeByte(ACCEPT);
		sendUUID(source);
		return readParty();
	}

	//Declines the current Invitation and returns the UUID of the source
	public UUID decline(UUID source) throws IOException {
		nonNull(source);
		databasedos.writeByte(DECLINE);
		sendUUID(source);
		return readUUID();
	}

	//Leaves the Party and returns owner + members
	public UUID[] leave(UUID source) throws IOException {
		nonNull(source);
		databasedos.writeByte(LEAVE);
		sendUUID(source);
		return readParty();
	}

	//Transfers Ownership and return owner + members
	public UUID[] owner(UUID source, UUID target) throws IOException {
		nonNull(source, target);
		databasedos.writeByte(OWNER);
		sendUUID(source);
		sendUUID(target);
		return readParty();
	}

	//databasedisbands Party and returns owner + members
	public UUID[] disband(UUID source) throws IOException {
		nonNull(source);
		databasedos.writeByte(DISBAND);
		sendUUID(source);
		return readParty();
	}

	//Toggle Invitations and returns new state (true=Allow, false=Decline)
	public boolean toggle(UUID source) throws IOException {
		nonNull(source);
		databasedos.writeByte(TOGGLE);
		sendUUID(source);
		return databasedis.readBoolean();
	}

	//Get All members from owner, owner listed first
	public UUID[] getMembersByOwner(UUID owner) throws IOException {
		nonNull(owner);
		databasedos.writeByte(GET_MEMBERS_BY_OWNER);
		sendUUID(owner);
		return readParty();
	}

	//Get All members from member, owner listed first
	public UUID[] getMembersByMember(UUID member) throws IOException {
		nonNull(member);
		databasedos.writeByte(GET_MEMBERS_BY_MEMBER);
		sendUUID(member);
		return readParty();
	}

	public void moveParty(UUID uuid, String server) throws IOException {
		nonNull(uuid);
		Functions.moveParty(uuid, server, this);
	}

	public void setResourcePack(UUID uuid, String resourcepack) throws IOException {
		nonNull(uuid);
		passdos.writeByte(RESOURCEPACK);
		passdos.writeLong(uuid.getMostSignificantBits());
		passdos.writeLong(uuid.getLeastSignificantBits());
		passdos.writeUTF(resourcepack);
	}

	private void sendUUID(UUID uuid) throws IOException {
		databasedos.writeLong(uuid.getMostSignificantBits());
		databasedos.writeLong(uuid.getLeastSignificantBits());
	}

	private UUID readUUID() throws IOException {
		return new UUID(databasedis.readLong(), databasedis.readLong());
	}

	private UUID[] readParty() throws IOException {
		int length = databasedis.readInt();
		if (length == 0)
			return null;
		UUID[] members = new UUID[length];
		for (int i = 0; i < length; i++) {
			members[i] = readUUID();
		}
		return members;
	}

	public RequestClient(String host) throws UnknownHostException, IOException {
		this.host = host;
		this.databaseport = 8868;
		this.passport = 8869;
		init(new Socket(host, databaseport), new Socket(host, passport), (byte) 0);
	}

	RequestClient(String host, byte type) throws UnknownHostException, IOException {
		this.host = host;
		this.databaseport = 8868;
		this.passport = 8869;
		init(new Socket(host, databaseport), new Socket(host, passport), type);
	}

	private void init(Socket database, Socket pass, byte type) throws IOException {
		this.type = type;
		databasedos = new DataOutputStream(database.getOutputStream());
		databasedis = new DataInputStream(database.getInputStream());
		passdos = new DataOutputStream(pass.getOutputStream());
		passdis = new DataInputStream(pass.getInputStream());
		passdos.writeByte(type);
		System.out.println("Connected");
		if (type > 0)
			requestLoop();
	}

	private void requestLoop() throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (;;) {
						byte operation = passdis.readByte();
						if (operation == MOVE) {
							UUID uuid = new UUID(passdis.readLong(), passdis.readLong());
							Functions.moveParty(uuid, passdis.readUTF(), RequestClient.this);
						} else {
							System.err.println("Bad Operation: " + operation);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void nonNull(UUID uuid) {
		if (uuid == null)
			throw new NullPointerException("UUID is null");
	}

	private void nonNull(UUID uuid1, UUID uuid2) {
		if (uuid1 == null || uuid2 == null)
			throw new NullPointerException("UUID is null");
	}

	public void reconnect() {
		System.out.println("Reconnecting");
		try {
			databasedos.close();
			databasedis.close();
			passdos.close();
			passdis.close();
			init(new Socket(host, databaseport), new Socket(host, passport), type);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
