package pl.sklep.skleplab.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ZwrotTest {

	@Test
	void akceptacja_tylko_po_odbiorze() {
		Zwrot z = new Zwrot(1L, 10L, "uszkodzone", Instant.now());

		// Zgłoszony -> nie wolno akceptować od razu
		assertThrows(IllegalStateException.class, z::akceptujZwrot);

		z.potwierdzOdbiorPaczki();
		z.akceptujZwrot();
		assertEquals(StatusZwrotu.AKCEPTOWANY, z.getStatusDecyzji());

		z.zlećZwrotSrodkow();
		assertEquals(StatusZwrotu.ZREALIZOWANY, z.getStatusDecyzji());
	}
}

