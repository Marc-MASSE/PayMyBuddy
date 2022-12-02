package fr.marc.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	static Logger log = LogManager.getLogger(UserControllerTest.class.getName());
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private UserRepository userRepository;
	
	private User testUser;
	
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

	// End point "/users"
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
	
	// End point "/user"
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
	
	// End point "/"
    @Test
	@WithMockUser
    public void testRootRederectToLoginPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is(302))
            .andExpect(view().name("redirect:/login?message="))
            ;
    }
    
    // TODO : End point "/admin"
    
	
	// End point "/profile"
    @Test
	@WithMockUser
    public void testDisplayArthurProfilePage() throws Exception {
        mockMvc.perform(get("/profile?id=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(content().string(containsString("Arthur")));
    }
	
    // End point "/login"
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
    
    // End point "/loginRequest"
	@Nested
	@WithMockUser
	class TestLoginRequest {
	    @Test
	    public void arthurLogin() throws Exception {
	        mockMvc.perform(post("/loginRequest")
		        	.param("email","acall@mail.fr")
		        	.param("password","Excalibur"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/home?id=1"));
	    }
	    
	    @Test
	    public void password_not_match() throws Exception {
	    	String message = "The password doesn't match with your email";
	        mockMvc.perform(post("/loginRequest")
		        	.param("email","acall@mail.fr")
		        	.param("password","False Password"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/login?message="+message));
	    }
	    
	    @Test
	    public void email_not_registered() throws Exception {
	    	String message = "Your email isn't registed";
	        mockMvc.perform(post("/loginRequest")
		        	.param("email","unknown@mail.fr")
		        	.param("password","Password"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/login?message="+message));
	    }
	}
    
    
    // End point "/balance"
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
	
    // End point "/activity"
	@Nested
	@WithMockUser
	class GetActivity {
		@Test
	    public void Arthur_activities_success() throws Exception {
	        mockMvc.perform(get("/activity?id=1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$[0].buddyName", is("Midas King")))
	            .andExpect(jsonPath("$[0].amount", is("200.00")))
	            .andExpect(jsonPath("$[1].buddyName", is("Job Poor")))
	            .andExpect(jsonPath("$[1].amount", is("-100.00")))
	            .andExpect(jsonPath("$[2].buddyName", is("Arthur Call")))
	            .andExpect(jsonPath("$[2].amount", is("350.00")));
	    }
	}
	
    // End point "/home"
    @Test
	@WithMockUser
    public void testDisplayArthurHomePage() throws Exception {
        mockMvc.perform(get("/home?id=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("home"))
            .andExpect(content().string(containsString("Welcome")))
            .andExpect(content().string(containsString("Arthur")))
            .andExpect(content().string(containsString("450.00")));
    }
	
    // End point "/contact"
    @Test
	@WithMockUser
    public void testDisplayArthurContactPage() throws Exception {
        mockMvc.perform(get("/contact?id=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("contact"))
            .andExpect(content().string(containsString("Contact us")))
            .andExpect(content().string(containsString("www.paymybuddy.com")));
    }
	
    // End point "/user" POST
    @Test
	@WithMockUser
    public void testAddAUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper(); 
		testUser = User.builder()
			.email("user1@mail.fr")
			.firstName("Prénom1")
			.lastName("Nom1")
			.password("111")
			.rememberMe(false)
			.iban("FR001")
			.bank("Banque1")
			.build();
        mockMvc.perform(post("/user")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(testUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("user1@mail.fr")))
            .andExpect(jsonPath("$.firstName", is("Prénom1")))
            .andExpect(jsonPath("$.lastName", is("Nom1")));
        userRepository.delete(userRepository.findByEmail("user1@mail.fr").get());
    }
	
	// End point "/saveUser"
    @Test
	@WithMockUser
    public void testModifyAUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper(); 
		testUser = User.builder()
			.email("user2@mail.fr")
			.firstName("Prénom2")
			.lastName("Nom2")
			.password("222")
			.rememberMe(false)
			.iban("FR002")
			.bank("Banque2")
			.build();
		userRepository.save(testUser);
		assertThat(userRepository.findByEmail("user2@mail.fr")).isPresent();
		log.info("User to add : {}",userRepository.findByEmail("user2@mail.fr"));
		testUser.setId(userRepository.findByEmail("user2@mail.fr").get().getId());
		
        mockMvc.perform(post("/saveUser?id="+testUser.getId())
        		.param("id", "6")
        		.param("email","user2@mail.fr")
        		.param("firstName", "Prénom2")
        		.param("lastName", "Nom2")
        		.param("password", "change")
        		.param("iban", "FR002")
        		.param("bank", "Banque2")        		)
            .andExpect(status().is(302));
		log.info("User modified : {}",userRepository.findByEmail("user2@mail.fr"));
        assertThat(userRepository.findByEmail("user2@mail.fr").get().getPassword())
        	.isEqualTo("change");
        userRepository.delete(userRepository.findByEmail("user2@mail.fr").get());
    }
	
    // TODO : End point "/user" DELETE
    @Test
	@WithMockUser
    public void testDeleteAUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper(); 
		testUser = User.builder()
			.email("user3@mail.fr")
			.firstName("Prénom3")
			.lastName("Nom3")
			.password("333")
			.rememberMe(false)
			.iban("FR003")
			.bank("Banque3")
			.build();
		userRepository.save(testUser);
		testUser.setId(userRepository.findByEmail("user3@mail.fr").get().getId());
		assertThat(userRepository.findByEmail("user3@mail.fr")).isPresent();
		
        mockMvc.perform(delete("/user?id="+testUser.getId()))
            .andExpect(status().isOk());
		assertThat(userRepository.findByEmail("user3@mail.fr")).isNotPresent();
    }
	
	
}
