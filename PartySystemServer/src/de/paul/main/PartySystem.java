package de.paul.main;

import de.paul.database.DataServer;
import de.paul.redirect.PassServer;
import java.io.IOException;

public class PartySystem {
	public static void main(String[] args) {
		try {
			DataServer database = new DataServer(8868);
			PassServer pass = new PassServer(8869);
			System.out.println("Commands: list/stop");
			if (System.console() != null)
				while (true) {
					String in = System.console().readLine();
					if ((in.equalsIgnoreCase("stop")) || (in.equalsIgnoreCase("exit")))
						System.exit(0);
					if (in.equalsIgnoreCase("list")) {
						database.printInfo();
						pass.printInfo();
					} else {
						System.out.println("Commands: list/stop");
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}