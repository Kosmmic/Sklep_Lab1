package pl.sklep.skleplab.security;

import java.util.Collection;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.security.ActorContext;
import pl.sklep.skleplab.application.security.ActorContextProvider;
import pl.sklep.skleplab.application.security.ActorRole;

@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SpringSecurityActorContextProvider implements ActorContextProvider {

	@Override
	public ActorContext current() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			throw new IllegalStateException("Brak uwierzytelnienia.");
		}
		return new ActorContext(auth.getName(), mapRole(auth.getAuthorities()));
	}

	private static ActorRole mapRole(Collection<? extends GrantedAuthority> authorities) {
		if (has(authorities, "ROLE_ADMIN")) {
			return ActorRole.ADMIN;
		}
		if (has(authorities, "ROLE_MANAGER")) {
			return ActorRole.MANAGER;
		}
		if (has(authorities, "ROLE_EMPLOYEE")) {
			return ActorRole.EMPLOYEE;
		}
		if (has(authorities, "ROLE_CLIENT")) {
			return ActorRole.CLIENT;
		}
		throw new IllegalStateException("Brak roli w kontekście bezpieczeństwa.");
	}

	private static boolean has(Collection<? extends GrantedAuthority> authorities, String authority) {
		for (GrantedAuthority a : authorities) {
			if (authority.equals(a.getAuthority())) {
				return true;
			}
		}
		return false;
	}
}

