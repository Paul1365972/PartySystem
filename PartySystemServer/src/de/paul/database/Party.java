package de.paul.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Party {
	private UUID owner;
	private List<UUID> members = new ArrayList<>();

	Party(UUID owner) {
		this.owner = owner;
	}

	UUID getOwner() {
		return owner;
	}

	void setOwner(UUID newOwner) {
		members.add(owner);
		members.remove(newOwner);
		owner = newOwner;
	}

	List<UUID> getMembers() {
		return members;
	}

	void addMember(UUID player) {
		members.add(player);
	}

	void removeMember(UUID player) {
		members.remove(player);
	}

	boolean isOwner(UUID player) {
		return player.equals(owner);
	}

	boolean isPlayer(UUID player) {
		for (int i = 0; i < members.size(); i++) {
			if (player.equals(members.get(i)))
				return true;
		}
		return false;
	}
}