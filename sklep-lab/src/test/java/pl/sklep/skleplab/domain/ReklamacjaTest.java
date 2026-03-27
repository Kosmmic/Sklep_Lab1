package pl.sklep.skleplab.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ReklamacjaTest {

	@Test
	void decyzje_kierownika_tylko_po_weryfikacji() {
		Reklamacja r = new Reklamacja(
				1L,
				10L,
				Instant.now(),
				StatusZgloszeniaProduktowego.ZGLOSZONE,
				"ZWROT_PODSTAWA",
				"ZEPSUTE");

		assertThrows(IllegalStateException.class, r::akceptuj);

		r.weryfikuj("opis stanu");
		r.akceptuj();

		assertEquals(StatusZgloszeniaProduktowego.AKCEPTOWANE, r.getStatus());
	}
}

