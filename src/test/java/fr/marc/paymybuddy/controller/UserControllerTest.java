package fr.marc.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
	
	static Logger log = LogManager.getLogger(UserControllerTest.class.getName());
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	// To mock the Spring Security context
	@Mock
	private SecurityContextHolder securityContextHolder;
	
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
	@Nested
	class GetUsers {
		@Test
		@WithMockUser (authorities="ADMIN")
	    public void withAdminRole() throws Exception {
	        mockMvc.perform(get("/users"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$[0].firstName", is("Arthur")))
	            .andExpect(jsonPath("$[1].firstName", is("Midas")))
	            .andExpect(jsonPath("$[2].firstName", is("Job")))
	            .andExpect(jsonPath("$[3].firstName", is("Balthazar")));
	    }
		
		@Test
		@WithMockUser (authorities="USER")
	    public void withUserRole() throws Exception {
	        mockMvc.perform(get("/users"))
	            .andExpect(status().is(403));
	    }
	}
	
	// End point "/user"
	@Nested
	@WithMockUser (authorities="ADMIN")
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
	
    // TODO : End point "/admin"
	

	
	// End point "/profile"
    @Test
	@WithMockUser (username = "acall@mail.fr")
    public void testDisplayArthurProfilePage() throws Exception {
        mockMvc.perform(get("/profile"))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(content().string(containsString("Arthur")))
            .andExpect(content().string(containsString("Call")))
            .andExpect(content().string(containsString("Modify")));
    }
	
    // End point "/login"
	@Nested
	@WithMockUser
	class TestLoginPage {
	    @Test
	    public void displayLoginPage() throws Exception {
	        mockMvc.perform(get("/login"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("login"))
	            .andExpect(content().string(containsString("Password")));
	    }
	    
	    @Test
	    public void successfulLogin() throws Exception {
	        RequestBuilder requestBuilder = formLogin()
	        		.user("acall@mail.fr")
	        		.password("Excalibur");
	        mockMvc.perform(requestBuilder)
	        	.andExpect(redirectedUrl("/home"))
	        	.andExpect(status().isFound());
	    }
	    
	    @Test
	    public void loginFailure() throws Exception {
	        RequestBuilder requestBuilder = formLogin()
	        		.user("unknown.fr")
	        		.password("anything");
	        mockMvc.perform(requestBuilder)
	        	.andExpect(redirectedUrl("/login?error"))
	        	.andExpect(status().isFound());
	    }
	}
	
    // End point "/logout"
	@Test
	@WithMockUser
    public void testLogoutRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/logout"))
            .andExpect(status().is(302))
            .andExpect(redirectedUrl("/login?logout"))
            .andExpect(status().isFound())
            ;
    }
    
    // End point "/balance"
	@Nested
	class GetBalance {
		@Test
		@WithMockUser (username = "acall@mail.fr")
	    public void arthur_balance_should_return_450() throws Exception {
	        mockMvc.perform(get("/balance"))
	        	.andExpect(status().isOk())
	            .andExpect(jsonPath("$", is(450.0)));
	    }
	}
	
    // End point "/activity"
	@Nested
	@WithMockUser (username = "acall@mail.fr")
	class GetActivity {
		@Test
	    public void Arthur_activities_success() throws Exception {
	        mockMvc.perform(get("/activity"))
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
	@WithMockUser (username = "acall@mail.fr")
    public void testDisplayArthurHomePage() throws Exception {
        mockMvc.perform(get("/home?message=Coucou"))
            .andExpect(status().isOk())
            .andExpect(view().name("home"))
            .andExpect(content().string(containsString("Welcome")))
            .andExpect(content().string(containsString("Arthur")))
            .andExpect(content().string(containsString("450.00")))
            .andExpect(content().string(containsString("Coucou")));
    }
	
    // End point "/contact"
    @Test
	@WithMockUser (username = "acall@mail.fr")
    public void testDisplayArthurContactPage() throws Exception {
        mockMvc.perform(get("/contact"))
            .andExpect(status().isOk())
            .andExpect(view().name("contact"))
            .andExpect(content().string(containsString("Contact us")))
            .andExpect(content().string(containsString("www.paymybuddy.com")));
    }
	
    // End point "/modify" POST
    @Test
	@WithMockUser (username = "acall@mail.fr")
    public void testDisplayArthurModifyPage() throws Exception {
        mockMvc.perform(get("/modify"))
            .andExpect(status().isOk())
            .andExpect(view().name("modify"))
            .andExpect(content().string(containsString("Arthur")))
            .andExpect(content().string(containsString("Call")))
            .andExpect(content().string(containsString("Confirm")));
    }
    
    // End point "/user" POST
    @Test
	@WithMockUser (authorities="ADMIN")
    public void testAddAUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper(); 
		testUser = User.builder()
			.email("user1@mail.fr")
			.firstName("Prénom1")
			.lastName("Nom1")
			.password("111")
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
	@WithMockUser (username = "user2@mail.fr")
    public void testModifyAUser() throws Exception {
		testUser = User.builder()
			.email("user2@mail.fr")
			.firstName("Prénom2")
			.lastName("Nom2")
			.password("222")
			.iban("FR002")
			.bank("Banque2")
			.role("USER")
			.build();
		userRepository.save(testUser);
		assertThat(userRepository.findByEmail("user2@mail.fr").get().getBank())
			.isEqualTo("Banque2");
		log.info("User to add : {}",userRepository.findByEmail("user2@mail.fr"));
		testUser.setId(userRepository.findByEmail("user2@mail.fr").get().getId());
		
        mockMvc.perform(post("/saveUser")
        		.param("id", "6")
        		.param("email","user2@mail.fr")
        		.param("firstName", "Prénom2")
        		.param("lastName", "Nom2")
        		.param("password", "222")
        		.param("iban", "FR002")
        		.param("bank", "New Bank")
        		.param("role", "USER"))
            .andExpect(status().is(302));
		log.info("User modified : {}",userRepository.findByEmail("user2@mail.fr"));
        assertThat(userRepository.findByEmail("user2@mail.fr").get().getBank())
        	.isEqualTo("New Bank");
        userRepository.delete(userRepository.findByEmail("user2@mail.fr").get());
    }
	
    // End point "/user" DELETE
    @Test
	@WithMockUser (authorities="ADMIN")
    public void testDeleteAUser() throws Exception {
		testUser = User.builder()
			.email("user3@mail.fr")
			.firstName("Prénom3")
			.lastName("Nom3")
			.password("333")
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
