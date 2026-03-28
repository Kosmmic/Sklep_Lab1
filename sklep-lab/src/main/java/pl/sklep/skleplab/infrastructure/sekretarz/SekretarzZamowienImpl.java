package pl.sklep.skleplab.infrastructure.sekretarz;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.SekretarzZamowien;
import pl.sklep.skleplab.application.ZamowienieRepository;
import pl.sklep.skleplab.domain.Zamowienie;

@Component
public class SekretarzZamowienImpl implements SekretarzZamowien {

	private final ZamowienieRepository zamowienieRepository;

	public SekretarzZamowienImpl(ZamowienieRepository zamowienieRepository) {
		this.zamowienieRepository = zamowienieRepository;
	}

	@Override
	public long nastepnyIdZamowienia() {
		return zamowienieRepository.nastepnyId();
	}

	@Override
	public void zakolejkujPoPlatnosci(String uzytkownik, Zamowienie zamowienie) {
		Objects.requireNonNull(uzytkownik);
		zamowienieRepository.save(zamowienie);
	}

	@Override
	public List<Zamowienie> pobierzWszystkieZamowienia() {
		return zamowienieRepository.findAll();
	}

	@Override
	public Optional<Zamowienie> znajdzZamowienie(long id) {
		return zamowienieRepository.findById(id);
	}

	@Override
	public void zapiszZamowienieWBackendzie(Zamowienie zamowienie) {
		zamowienieRepository.save(zamowienie);
	}
}
