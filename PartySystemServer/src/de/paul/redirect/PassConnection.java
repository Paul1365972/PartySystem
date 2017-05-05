package de.paul.redirect;

import de.paul.main.DataConstants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class PassConnection extends Thread implements DataConstants {
	private boolean running = true;
	private DataOutputStream dos;
	private DataInputStream dis;
	private PassServer passServer;
	private ReentrantLock lock = new ReentrantLock();

	PassConnection(DataOutputStream dos, DataInputStream dis, PassServer passServer) throws IOException {
		this.dos = dos;
		this.dis = dis;
		this.passServer = passServer;
	}

	private void execute(byte operation) throws IOException {
		System.out.println("Passing: " + operation);
		switch (operation) {
		case RESOURCEPACK:
			long most = dis.readLong();
			long least = dis.readLong();
			String link = dis.readUTF();
			for (int i = 0; i < passServer.getConnections().size(); i++) {
				PassConnection con = passServer.getConnections().get(i);
				if (con.isRunning()) {
					con.lock.lock();
					con.dos.writeByte(RESOURCEPACK);
					con.dos.writeLong(most);
					con.dos.writeLong(least);
					con.dos.writeUTF(link);
					con.lock.unlock();
				}
			}
			break;
		case MOVE:
			most = dis.readLong();
			least = dis.readLong();
			String server = dis.readUTF();
			PassConnection con = passServer.getProxy();
			try {
				con.lock.lock();
				con.dos.writeByte(MOVE);
				con.dos.writeLong(most);
				con.dos.writeLong(least);
				con.dos.writeUTF(server);
				con.lock.unlock();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			System.err.println("Bad Operation: " + operation);
		}
	}

	public void run() {
		while (running) {
			try {
				byte operation = dis.readByte();
				execute(operation);
			} catch (IOException e) {
				e.printStackTrace();
				close();
			}
		}
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