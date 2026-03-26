package pl.sklep.skleplab.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void koszyk_bezLogowania_401() throws Exception {
		mockMvc.perform(get("/api/v1/koszyk")).andExpect(status().isUnauthorized());
	}

	@Test
	void koszyk_jakoKlient_200() throws Exception {
		String token = loginAndGetToken("klient", "demo");
		mockMvc.perform(get("/api/v1/koszyk").header("Authorization", bearer(token)))
				.andExpect(status().isOk());
	}

	@Test
	void listaZamowien_jakoKlient_403() throws Exception {
		String token = loginAndGetToken("klient", "demo");
		mockMvc.perform(get("/api/v1/zamowienia").header("Authorization", bearer(token)))
				.andExpect(status().isForbidden());
	}

	@Test
	void listaZamowien_jakoPracownik_200() throws Exception {
		String token = loginAndGetToken("pracownik", "demo");
		mockMvc.perform(get("/api/v1/zamowienia").header("Authorization", bearer(token)))
				.andExpect(status().isOk());
	}

	@Test
	void zlozZamowienie_jakoKlient_poDodaniuDoKoszyka_201() throws Exception {
		String token = loginAndGetToken("klient", "demo");

		mockMvc.perform(post("/api/v1/koszyk/pozycje")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"towarId\":1,\"ilosc\":1}"))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/zamowienia")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"metodaPlatnosci\":\"PRZELEW\"}"))
				.andExpect(status().isCreated());
	}

	private String loginAndGetToken(String username, String password) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		return json.get("token").asText();
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}
}
