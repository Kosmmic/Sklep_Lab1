package pl.sklep.skleplab.application.security;

import java.util.EnumSet;
import java.util.Objects;

public final class Authz {
	private Authz() {}

	public static void requireAnyRole(ActorContext actor, ActorRole... allowed) {
		Objects.requireNonNull(actor, "actor");
		if (allowed == null || allowed.length == 0) {
			throw new IllegalArgumentException("allowed roles required");
		}
		EnumSet<ActorRole> allow = EnumSet.noneOf(ActorRole.class);
		for (ActorRole r : allowed) {
			allow.add(Objects.requireNonNull(r, "role"));
		}
		if (!allow.contains(actor.role())) {
			throw new IllegalStateException("Brak uprawnień: rola " + actor.role() + " nie może wykonać tej operacji.");
		}
	}
}

