package de.paul.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.paul.main.DataConstants;

class Database implements DataConstants {
	private static Database database;
	private List<Party> partys = new ArrayList<>();
	private List<InviteRequest> requests = new ArrayList<>();
	private List<UUID> declineInvite = new ArrayList<>();

	byte invite(UUID source, UUID target) {
		if (source.equals(target))
			return ErrorCodes.SELF_INVITE;
		int i = 0;
		for (i = 0; i < declineInvite.size(); i++) {
			if (target.equals(declineInvite.get(i))) {
				return ErrorCodes.INVITES_DISABLED;
			}
		}
		for (i = partys.size() - 1; i >= 0; i--) {
			Party party = partys.get(i);
			if ((party.isOwner(target)) || (party.isPlayer(target)))
				return ErrorCodes.TARGET_ALREADY_IN_PARTY;
			if (party.isPlayer(source))
				return ErrorCodes.NOT_OWNER;
		}
		for (i = requests.size() - 1; i >= 0; i--) {
			InviteRequest request = requests.get(i);
			if ((source.equals(request.getSource())) && (target.equals(request.getTarget())))
				return ErrorCodes.YOU_ALREADY_REQUESTED;
			if ((target.equals(request.getSource())) && (source.equals(request.getTarget())))
				return ErrorCodes.OTHER_ALREADY_REQUESTED;
			if (target.equals(request.getTarget()))
				requests.remove(i);
		}
		requests.add(new InviteRequest(source, target));
		return ErrorCodes.SUCCESS;
	}

	Party accept(UUID source) {
		for (int i = requests.size() - 1; i >= 0; i--) {
			InviteRequest request = requests.get(i);
			if (source.equals(request.getTarget())) {
				Party party = getPartyByOwner(request.getSource());
				if (party == null)
					party = new Party(request.getSource());
				party.addMember(source);
				partys.add(party);
				requests.remove(i);
				removeRequestSource(source);
				removeRequestTarget(request.getSource());
				return party;
			}
		}
		return null;
	}

	private void removeRequestSource(UUID source) {
		for (int i = requests.size() - 1; i >= 0; i--)
			if (source.equals((requests.get(i)).getSource()))
				requests.remove(i);
	}

	private void removeRequestTarget(UUID source) {
		for (int i = requests.size() - 1; i >= 0; i--)
			if (source.equals((requests.get(i)).getTarget()))
				requests.remove(i);
	}

	UUID decline(UUID source) {
		for (int i = requests.size() - 1; i >= 0; i--) {
			InviteRequest request = requests.get(i);
			if (source.equals(request.getTarget())) {
				requests.remove(i);
				return request.getSource();
			}
		}
		return null;
	}

	Party leave(UUID source) {
		Party party = getPartyByMember(source);
		if ((party != null) && (!source.equals(party.getOwner()))) {
			party.removeMember(source);
			return party;
		}
		return null;
	}

	Party owner(UUID source, UUID target) {
		Party party = getPartyByOwner(source);
		if (party != null) {
			party.setOwner(target);
			return party;
		}
		return null;
	}

	Party disband(UUID source) {
		for (int i = partys.size() - 1; i >= 0; i--) {
			Party party = partys.get(i);
			if (party.isOwner(source))
				return partys.remove(i);
		}
		return null;
	}

	boolean toggle(UUID source) {
		for (int i = 0; i < declineInvite.size(); i++) {
			if (source.equals(declineInvite.get(i))) {
				declineInvite.remove(i);
				return true;
			}
		}
		declineInvite.add(source);
		return false;
	}

	Party getPartyByOwner(UUID owner) {
		for (int i = partys.size() - 1; i >= 0; i--) {
			Party party = partys.get(i);
			if (party.isOwner(owner))
				return party;
		}
		return null;
	}

	Party getPartyByMember(UUID member) {
		for (int i = partys.size() - 1; i >= 0; i--) {
			Party party = partys.get(i);
			if ((party.isOwner(member)) || (party.isPlayer(member)))
				return party;
		}
		return null;
	}

	void toggleAll(UUID[] uuids) {
		for (UUID uuid : uuids)
			declineInvite.add(uuid);
	}

	void printInfo() {
		System.out.println("Partys: " + partys.size());
		System.out.println("Requests: " + requests.size());
	}

	static void init(UUID[] uuids) {
		database = new Database();
		database.toggleAll(uuids);
	}

	static Database get() {
		return database;
	}

	public UUID[] getDeclineUUIDs() {
		return (UUID[]) declineInvite.toArray(new UUID[declineInvite.size()]);
	}
}