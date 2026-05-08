package com.shortener.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class UrlIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void shorten_url_integration_test() throws Exception {
		
		String input="https://google.com";
	    
		// shorten test
		
		MvcResult shortenRes=mockMvc.perform(
	            post("/shorten").param("url",input)
	    )
	    .andExpect(status().isOk())
	    .andReturn();
	    
	   String shortCode=shortenRes.getResponse().getContentAsString().trim();
	   
	   // redirect test
	   
	   MvcResult redirRes=mockMvc.perform(
	   			get("/"+shortCode)
	   	).andExpect(status().isFound()).andReturn();
	   
	   String longUrl=redirRes.getResponse().getRedirectedUrl();
	   
	   assertEquals(input,longUrl);
	   
	   // analytics test
	   
	   mockMvc.perform(
	   			get("/stats/"+shortCode)
	   	).andExpect(jsonPath("$.clicks").value(1));
	   
	}

}
