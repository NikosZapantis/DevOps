package gr.hua.dit.ds.crowdfunding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@SpringBootTest
@AutoConfigureMockMvc
class DscrowdfundingApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testSignupAndLogin() throws Exception {
		String signupJson = "{ \"username\": \"newuser\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"jonh@hua.gr\", \"password\": \"newpass\" }";
		
		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(signupJson))
			.andExpect(status().isOk());
		
		String loginJson = "{ \"username\": \"newuser\", \"password\": \"newpass\" }";
		
		mockMvc.perform(post("/api/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("newuser"));
	}

}
