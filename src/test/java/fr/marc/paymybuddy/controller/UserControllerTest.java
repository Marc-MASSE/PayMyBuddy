package fr.marc.paymybuddy.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Test
    public void getUsers() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName", is("Arthur")))
            .andExpect(jsonPath("$[1].firstName", is("Midas")))
            .andExpect(jsonPath("$[2].firstName", is("Job")))
            .andExpect(jsonPath("$[3].firstName", is("Balthazar")));
    }
	
	@Nested
	class GetUserById {
		@Test
	    public void success() throws Exception {
	        mockMvc.perform(get("/user?id=1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("firstName", is("Arthur")));
	    }
		
		@Test
	    public void no_user() throws Exception {
	        mockMvc.perform(get("/user?id=15"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$[0]").doesNotExist());
	    }
	}
	
	@Nested
	class GetUserByEmail {
		@Test
	    public void success() throws Exception {
	        mockMvc.perform(get("/login?email=acall@mail.fr"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("firstName", is("Arthur")));
	    }
		
		@Test
	    public void no_email() throws Exception {
	        mockMvc.perform(get("/login?email=noemail@mail.fr"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$[0]").doesNotExist());
	    }
	}
	
	@Nested
	class GetBalance {
		@Test
	    public void success() throws Exception {
	        mockMvc.perform(get("/balance?id=1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$", is(450)));
	    }
		
		@Test
	    public void no_user() throws Exception {
	        mockMvc.perform(get("/balance?id=15"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$", is(0)));
	    }
	}
	
}
