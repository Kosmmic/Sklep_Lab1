package pl.sklep.skleplab.domain;

/**
 * Zamiast {@code String statusZamowienia} z diagramu — w kodzie enum jest bezpieczniejszy.
 */
public enum StatusZamowienia {

	NOWE,
	OPLACONE,
	W_REALIZACJI,
	WYSLANE,
	ANULOWANE
}
