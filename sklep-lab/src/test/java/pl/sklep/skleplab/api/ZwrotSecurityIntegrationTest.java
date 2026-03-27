package pl.sklep.skleplab.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ZwrotSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void zwrot_zgloszenie_jakoKlient_201() throws Exception {
		String token = loginAndGetToken("klient", "demo");

		// create order first (in-memory)
		mockMvc.perform(post("/api/v1/koszyk/pozycje")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"towarId\":1,\"ilosc\":1}"))
				.andExpect(status().isCreated());

		MvcResult orderResult = mockMvc.perform(post("/api/v1/zamowienia")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"metodaPlatnosci\":\"PRZELEW\"}"))
				.andExpect(status().isCreated())
				.andReturn();

		JsonNode orderJson = objectMapper.readTree(orderResult.getResponse().getContentAsString());
		long orderId = orderJson.get("id").asLong();

		mockMvc.perform(post("/api/v1/zamowienia/" + orderId + "/zwrot")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"powod\":\"ZWROT_KONSUMENCKI\",\"pozycje\":[{\"towarId\":1,\"ilosc\":1}]}"))
				.andExpect(status().isCreated());
	}

	@Test
	void zwrot_zgloszenie_jakoPracownik_403() throws Exception {
		String token = loginAndGetToken("pracownik", "demo");

		// not creating order here: endpoint auth should block first with 403
		mockMvc.perform(post("/api/v1/zamowienia/999/zwrot")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"powod\":\"ZWROT_KONSUMENCKI\",\"pozycje\":[{\"towarId\":1,\"ilosc\":1}]}"))
				.andExpect(status().isForbidden());
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

