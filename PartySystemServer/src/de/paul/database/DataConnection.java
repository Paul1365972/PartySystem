package de.paul.database;

import de.paul.main.DataConstants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

class DataConnection extends Thread implements DataConstants {
	private static ReentrantLock lock = new ReentrantLock();
	private boolean running = true;
	private DataOutputStream dos;
	private DataInputStream dis;
	private static final UUID nullUUID = new UUID(0L, 0L);

	DataConnection(Socket socket) throws IOException {
		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());
	}

	public void run() {
		while (running)
			try {
				byte operation = dis.readByte();
				execute(operation);
			} catch (IOException e) {
				e.printStackTrace();
				close();
			}
	}

	private void execute(byte operation) {
		lock.lock();
		System.out.println("Executing ".concat(String.valueOf(operation)));
		try {
			UUID source;
			UUID target;
			Party party;
			switch (operation) {
			case INVITE:
				source = readUUID();
				target = readUUID();
				byte errorcode = Database.get().invite(source, target);
				dos.writeByte(errorcode);
				break;
			case ACCEPT:
				source = readUUID();
				party = Database.get().accept(source);
				sendParty(party);
				break;
			case DECLINE:
				source = readUUID();
				UUID uuid = Database.get().decline(source);
				sendUUID(uuid);
				break;
			case LEAVE:
				source = readUUID();
				party = Database.get().leave(source);
				sendParty(party);
				break;
			case OWNER:
				source = readUUID();
				target = readUUID();
				party = Database.get().owner(source, target);
				sendParty(party);
				break;
			case DISBAND:
				source = readUUID();
				party = Database.get().disband(source);
				sendParty(party);
				break;
			case TOGGLE:
				source = readUUID();
				boolean state = Database.get().toggle(source);
				dos.writeBoolean(state);
				break;
			case GET_MEMBERS_BY_OWNER:
				source = readUUID();
				party = Database.get().getPartyByOwner(source);
				sendParty(party);
				break;
			case GET_MEMBERS_BY_MEMBER:
				source = readUUID();
				party = Database.get().getPartyByMember(source);
				sendParty(party);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		lock.unlock();
	}

	private void sendParty(Party party) throws IOException {
		if (party == null) {
			dos.writeInt(0);
			return;
		}
		int length = party.getMembers().size();
		dos.writeInt(length + 1);
		sendUUID(party.getOwner());
		for (int i = 0; i < length; i++)
			sendUUID((UUID) party.getMembers().get(i));
	}

	private void sendUUID(UUID uuid) throws IOException {
		if (uuid == null)
			uuid = nullUUID;
		dos.writeLong(uuid.getMostSignificantBits());
		dos.writeLong(uuid.getLeastSignificantBits());
	}

	private UUID readUUID() throws IOException {
		return new UUID(dis.readLong(), dis.readLong());
	}

	void close() {
		if (running) {
			running = false;
			try {
				dos.close();
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	boolean isRunning() {
		return running;
	}
}