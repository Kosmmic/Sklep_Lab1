package pl.sklep.skleplab.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void koszyk_bezLogowania_401() throws Exception {
		mockMvc.perform(get("/api/v1/koszyk")).andExpect(status().isUnauthorized());
	}

	@Test
	void koszyk_jakoKlient_200() throws Exception {
		mockMvc.perform(get("/api/v1/koszyk").with(httpBasic("klient", "demo")))
				.andExpect(status().isOk());
	}

	@Test
	void listaZamowien_jakoKlient_403() throws Exception {
		mockMvc.perform(get("/api/v1/zamowienia").with(httpBasic("klient", "demo")))
				.andExpect(status().isForbidden());
	}

	@Test
	void listaZamowien_jakoPracownik_200() throws Exception {
		mockMvc.perform(get("/api/v1/zamowienia").with(httpBasic("pracownik", "demo")))
				.andExpect(status().isOk());
	}

	@Test
	void zlozZamowienie_jakoKlient_poDodaniuDoKoszyka_201() throws Exception {
		mockMvc.perform(post("/api/v1/koszyk/pozycje")
				.with(httpBasic("klient", "demo"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"towarId\":1,\"ilosc\":1}"))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/zamowienia")
				.with(httpBasic("klient", "demo"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"metodaPlatnosci\":\"PRZELEW\"}"))
				.andExpect(status().isCreated());
	}
}
