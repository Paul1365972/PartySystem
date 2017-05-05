package de.paul.database;

import java.util.UUID;

class InviteRequest {
	private UUID source;
	private UUID target;

	InviteRequest(UUID source, UUID target) {
		this.source = source;
		this.target = target;
	}

	UUID getSource() {
		return this.source;
	}

	UUID getTarget() {
		return this.target;
	}
}