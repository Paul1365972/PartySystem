package de.paul.database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataServer {
	private List<DataConnection> connections = new ArrayList<>();
	private ServerSocket serverSocket;
	private boolean RUNNING = true;

	private static File decline = new File(new File(DataServer.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().concat("\\decline.txt"));

	private void init() {
		System.out.println("DeclineFile: " + decline);
		Database.init(readDecline());
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					exit();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	private void run() {
		new Thread(new Runnable() {
			public void run() {
				while (RUNNING)
					try {
						Socket socket = serverSocket.accept();
						System.out.println("Connected to " + socket.getRemoteSocketAddress());
						DataConnection con = new DataConnection(socket);
						connections.add(con);
						con.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}).start();
	}

	public DataServer(int port) throws IOException {
		init();
		serverSocket = new ServerSocket(port);
		System.out.println("Created DataServer on Port " + port);
		run();
	}

	private void exit() throws IOException {
		RUNNING = false;
		serverSocket.close();
		for (int i = 0; i < connections.size(); i++) {
			((DataConnection) connections.get(i)).close();
		}
		writeDecline();
	}

	private void writeDecline() {
		System.out.println("Saving...");
		UUID[] uuids = Database.get().getDeclineUUIDs();
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(decline));
			dos.writeInt(uuids.length);
			for (int i = 0; i < uuids.length; i++) {
				dos.writeLong(uuids[i].getMostSignificantBits());
				dos.writeLong(uuids[i].getLeastSignificantBits());
			}
			dos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Saved");
	}

	private UUID[] readDecline() {
		if (!decline.exists()) {
			decline.getParentFile().mkdirs();
			try {
				decline.createNewFile();
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(decline));
				dos.writeInt(0);
				dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		UUID[] uuids = new UUID[0];
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(decline));
			int length = dis.readInt();
			uuids = new UUID[length];
			for (int i = 0; i < length; i++)
				uuids[i] = new UUID(dis.readLong(), dis.readLong());
			dis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return uuids;
	}

	public void printInfo() {
		System.out.println("Connections: " + connections.size());
		int active = 0;
		for (int i = 0; i < connections.size(); i++) {
			if (((DataConnection) connections.get(i)).isRunning())
				active++;
		}
		System.out.println("Active: " + active);
		Database.get().printInfo();
	}
}