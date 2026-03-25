package pl.sklep.skleplab.application;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.domain.Koszyk;
import pl.sklep.skleplab.domain.Towar;

/**
 * {@code @Service} — warstwa „use case”: tutaj łączysz domenę z dostępem do danych.
 * Kontroler powinien być cienki; reguły biznesowe trzymaj tu lub w klasach domeny.
 */
@Service
public class KoszykService {

	private final TowarCatalog towarCatalog;
	private final Koszyk koszykDemo = new Koszyk();

	public KoszykService(TowarCatalog towarCatalog) {
		this.towarCatalog = towarCatalog;
	}

	public Koszyk pobierzKoszyk() {
		return koszykDemo;
	}

	public void dodajDoKoszyka(Long towarId, int ilosc) {
		Towar towar = towarCatalog.findById(towarId)
				.orElseThrow(() -> new IllegalArgumentException("Nieznany towar: " + towarId));
		koszykDemo.dodaj(towar, ilosc);
	}

	public void wyczyscKoszyk() {
		koszykDemo.wyczysc();
	}
}
