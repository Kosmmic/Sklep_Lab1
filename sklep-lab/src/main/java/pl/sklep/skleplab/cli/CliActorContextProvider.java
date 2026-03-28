package pl.sklep.skleplab.cli;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.security.ActorContext;
import pl.sklep.skleplab.application.security.ActorContextProvider;
import pl.sklep.skleplab.application.security.ActorRole;

@Component
@Profile("cli")
public class CliActorContextProvider implements ActorContextProvider {
	private final AtomicReference<ActorContext> current = new AtomicReference<>(new ActorContext("klient", ActorRole.CLIENT));

	@Override
	public ActorContext current() {
		return current.get();
	}

	public void setCurrent(String username, ActorRole role) {
		current.set(new ActorContext(username, role));
	}
}

