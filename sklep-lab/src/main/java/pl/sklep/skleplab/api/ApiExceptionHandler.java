package pl.sklep.skleplab.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
/**
 * {@code @RestControllerAdvice} — wspólna obsługa wyjątków z kontrolerów.
 * Dzięki temu błąd biznesowy nie kończy się „twardym” 500, tylko czytelną odpowiedzią.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> illegalArgument(IllegalArgumentException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, String>> illegalState(IllegalStateException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Map<String, String>> userNotFound(UsernameNotFoundException ex) {
    	return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED) // 401
            .body(Map.of("error", "Użytkownik nie istnieje", "message", ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
		public ResponseEntity<Map<String, String>> badCredentials(BadCredentialsException ex) {
    	return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED) // 401
            .body(Map.of("error", "Błędne hasło", "message", "Podane dane logowania są nieprawidłowe"));
		}
	}
