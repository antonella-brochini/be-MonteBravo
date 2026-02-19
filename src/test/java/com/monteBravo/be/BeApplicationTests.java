package com.monteBravo.be;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BeApplicationTests {
	@Autowired
	private MockMvc mockito;

	@Test
	void contextLoads() {
	}

	@Test
	void MPControllerTes(){
		try{
			mockito.perform( post(""))
					.andExpect(status().isOk());
		}catch(Exception e ){

		}

	}

}
