package pl.sklep.skleplab.api;

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
class ReklamacjaSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void reklamacja_flow_wybrane_role() throws Exception {
		String klientToken = loginAndGetToken("klient", "demo");
		String pracownikToken = loginAndGetToken("pracownik", "demo");
		String kierownikToken = loginAndGetToken("kierownik", "demo");

		// create order first (in-memory)
		mockMvc.perform(post("/api/v1/koszyk/pozycje")
				.header("Authorization", bearer(klientToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"towarId\":1,\"ilosc\":1}"))
				.andExpect(status().isCreated());

		MvcResult orderResult = mockMvc.perform(post("/api/v1/zamowienia")
				.header("Authorization", bearer(klientToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"metodaPlatnosci\":\"PRZELEW\"}"))
				.andExpect(status().isCreated())
				.andReturn();

		long orderId = objectMapper.readTree(orderResult.getResponse().getContentAsString()).get("id").asLong();

		MvcResult reklamacjaResult = mockMvc.perform(post("/api/v1/zamowienia/" + orderId + "/reklamacja")
						.header("Authorization", bearer(klientToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"powod\":\"WADLIWE\",\"opisUsterki\":\"NIE_DZIALA\",\"pozycje\":[{\"towarId\":1,\"ilosc\":1}]}"))
				.andExpect(status().isCreated())
				.andReturn();

		long reklamacjaId = objectMapper.readTree(reklamacjaResult.getResponse().getContentAsString()).get("id").asLong();

		// employee verification
		mockMvc.perform(post("/api/v1/reklamacje/" + reklamacjaId + "/weryfikacja")
						.header("Authorization", bearer(pracownikToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"opisStanu\":\"PROBLEMY_Z_MECHANIKI\"}"))
				.andExpect(status().isNoContent());

		// manager decision
		mockMvc.perform(post("/api/v1/reklamacje/" + reklamacjaId + "/akceptacja")
						.header("Authorization", bearer(kierownikToken)))
				.andExpect(status().isNoContent());
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

