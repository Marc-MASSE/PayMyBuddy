package fr.marc.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ConnectionControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ConnectionRepository connectionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
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
	
	// End point "/connection" POST
	@Test
	@WithMockUser (authorities="ADMIN")
    public void addAConnectionTest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Connection testConnection = Connection.builder()
				.buddyId(4)
				.user(userRepository.findById(1).get())
				.build();
		
		assertThat(connectionRepository.findByUserIdAndBuddyId(1,4))
				.isNull();
		
        mockMvc.perform(post("/connection")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(testConnection)))
            .andExpect(status().isOk());
        
        assertThat(connectionRepository.findByUserIdAndBuddyId(1,4))
				.isNotNull();
        
        connectionRepository.delete(connectionRepository.findByUserIdAndBuddyId(1,4));
    }
	
	// End point "/buddies"
    @Test
	@WithMockUser (username = "acall@mail.fr")
    public void testDisplayArthurBuddiesPage() throws Exception {
    	assertThat(connectionRepository.findById(8)
				.isEmpty());
        mockMvc.perform(get("/buddies?message=Message"))
            .andExpect(status().isOk())
            .andExpect(view().name("buddies"))
            .andExpect(content().string(containsString("My buddies")))
            .andExpect(content().string(containsString("Midas King")))
            .andExpect(content().string(containsString("Job Poor")))
            .andExpect(content().string(containsString("Message")));
    }
	
	// End point "/addABuddy"
	@Nested
	class TestAddABuddy {
		@Test
		@WithMockUser (username = "mking@mail.fr")
	    public void addAValideBuddy() throws Exception {
			
			assertThat(connectionRepository.findByUserIdAndBuddyId(2,3))
					.isNull();
	        mockMvc.perform(post("/addABuddy")
	        		.param("id","3")
	        		.param("buddyName","Job Poor")
	        		.param("email","jpoor@mail.fr"))
	        .andExpect(status().is(302))
	        .andExpect(view().name("redirect:/buddies?message="));
	        
	        assertThat(connectionRepository.findByUserIdAndBuddyId(2,3))
	        		.isNotNull();
	        
	        connectionRepository.delete(connectionRepository.findByUserIdAndBuddyId(2,3));
	    }
		
		@Test
		@WithMockUser (username = "acall@mail.fr")
	    public void addANoRegisteredBuddy() throws Exception {
	        mockMvc.perform(post("/addABuddy")
	        		.param("id","15")
	        		.param("buddyName","Nemo")
	        		.param("email","nobody@mail.fr"))
	        .andExpect(status().is(302))
	        .andExpect(view().name("redirect:/buddies?message=This buddy isn't registered."));
	    }
		
		@Test
		@WithMockUser (username = "acall@mail.fr")
	    public void addTheTreasurerAsABuddy() throws Exception {
	        mockMvc.perform(post("/addABuddy")
	        		.param("id","5")
	        		.param("buddyName","PayMyBuddy ")
	        		.param("email","admin@mail.fr"))
	        .andExpect(status().is(302))
	        .andExpect(view().name("redirect:/buddies?message=This email isn't available."));
	    }
		
		@Test
		@WithMockUser (username = "acall@mail.fr")
	    public void addANoNewBuddy() throws Exception {
	        mockMvc.perform(post("/addABuddy")
	        		.param("id","2")
	        		.param("buddyName","Midas King")
	        		.param("email","mking@mail.fr"))
	        .andExpect(status().is(302))
	        .andExpect(view().name("redirect:/buddies?message=Midas King is already your buddy."));
	    }
	}
	
	// End point "/connection" DELETE
	@Test
	@WithMockUser (authorities="ADMIN")
    public void deleteAConnectionTest() throws Exception {
		Connection testConnection = Connection.builder()
				.buddyId(5)
				.user(userRepository.findById(1).get())
				.build();
		
		connectionRepository.save(testConnection);
		
        assertThat(connectionRepository.findByUserIdAndBuddyId(1,5))
				.isNotNull();
		
        mockMvc.perform(delete("/connection?Id="+connectionRepository.findByUserIdAndBuddyId(1,5).getId()))
        		.andExpect(status().isOk());
		
		assertThat(connectionRepository.findByUserIdAndBuddyId(1,5))
				.isNull();
    }
	

}
