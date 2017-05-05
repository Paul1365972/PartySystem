package de.paul.redirect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PassServer {
	private PassConnection proxy;
	private List<PassConnection> connections = new ArrayList<>();
	private ServerSocket serverSocket;
	private boolean RUNNING = true;
	private int passive = 0;

	private void run() {
		new Thread(new Runnable() {
			public void run() {
				while (RUNNING)
					try {
						Socket socket = PassServer.this.serverSocket.accept();
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						DataInputStream dis = new DataInputStream(socket.getInputStream());
						byte type = dis.readByte();
						System.out.println("Connected to " + socket.getRemoteSocketAddress());
						PassConnection con = new PassConnection(dos, dis, PassServer.this);
						if (type == 2)
							proxy = con;
						else if (type == 1)
							connections.add(con);
						else {
							passive++;
						}
						con.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}).start();
	}

	PassConnection getProxy() {
		return proxy;
	}

	List<PassConnection> getConnections() {
		return connections;
	}

	public PassServer(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		System.out.println("Running Passserver on Port " + port);
		run();
	}

	public void printInfo() {
		System.out.println("Proxy: " + (proxy == null ? "Undefiend" : "Defined"));
		System.out.println("Server: " + connections.size());
		System.out.println("Other: " + passive);
	}
}