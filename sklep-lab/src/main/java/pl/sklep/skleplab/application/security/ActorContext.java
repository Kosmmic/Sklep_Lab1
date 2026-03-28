package pl.sklep.skleplab.application.security;

import java.util.Objects;

public record ActorContext(String username, ActorRole role) {
	public ActorContext {
		username = Objects.requireNonNull(username, "username");
		role = Objects.requireNonNull(role, "role");
	}
}

