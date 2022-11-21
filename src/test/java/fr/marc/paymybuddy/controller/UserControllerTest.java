package fr.marc.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.marc.paymybuddy.DTO.LoginDTO;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserController userController;
	
	// For tests to pass through Spring Security
	@BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
        		.webAppContextSetup(this.context)
        		.apply(springSecurity())
        		.build();
        // For using @WithMockUser annotation
        MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void userController_is_correctely_called()throws Exception {
	   assertThat(userController).isNotNull();
	   }

	@Test
	@WithMockUser
    public void getUsers() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName", is("Arthur")))
            .andExpect(jsonPath("$[1].firstName", is("Midas")))
            .andExpect(jsonPath("$[2].firstName", is("Job")))
            .andExpect(jsonPath("$[3].firstName", is("Balthazar")));
    }
	
	@Nested
	@WithMockUser
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
	
    @Test
	@WithMockUser
    public void testRootRederectToLoginPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is(302))
            .andExpect(view().name("redirect:/login?message="))
            ;
    }
	
    @Test
	@WithMockUser
    public void testDisplayArthurProfilePage() throws Exception {
        mockMvc.perform(get("/profile?id=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(content().string(containsString("Arthur")));
    }
	
	@Nested
	@WithMockUser
	class TestDisplayLoginPage {
	    @Test
	    public void no_message() throws Exception {
	        mockMvc.perform(get("/login?message="))
	            .andExpect(status().isOk())
	            .andExpect(view().name("login"))
	            .andExpect(content().string(containsString("Password")));
	    }
	    
	    @Test
	    public void with_message() throws Exception {
	        mockMvc.perform(get("/login?message=Message"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("login"))
	            .andExpect(content().string(containsString("Message")));
	    }
	}
    
	@Nested
	@WithMockUser
	class TestLoginRequest {
	    @Test
	    public void ArthurLogin() throws Exception {
	    	
			ObjectMapper mapper = new ObjectMapper(); 
	    	
	    	LoginDTO loginDTO = new LoginDTO();
	    	loginDTO.setEmail("acall@mail.fr");
	    	loginDTO.setPassword("Excalibur");
	    	
	        mockMvc.perform(post("/loginRequest")
	        		.contentType(MediaType.APPLICATION_JSON)
	        		.content(mapper.writeValueAsString(loginDTO)))
	        	//.sessionAttr("loginDTO",loginDTO)
	        	//.param("loginDTO.email","acall@mail.fr")
	        	//.param("loginDTO.password","Excalibur")
	        	//.with(csrf()))
	            .andExpect(status().is(302))
	            //.andExpect(model().attributeExists("loginDTO"))
	            .andExpect(view().name("home?id=1"));
	    }
	    
	    @Test
	    public void with_message() throws Exception {
	        mockMvc.perform(get("/login?message=Message"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("login"))
	            .andExpect(content().string(containsString("Message")));
	    }
	}
    
    
	
	@Nested
	@WithMockUser
	class GetBalance {
		@Test
	    public void success() throws Exception {
	        mockMvc.perform(get("/balance?id=1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$", is(450.0)));
	    }
		
		@Test
	    public void no_user() throws Exception {
	        mockMvc.perform(get("/balance?id=15"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$", is(0)));
	    }
	}
	
}
