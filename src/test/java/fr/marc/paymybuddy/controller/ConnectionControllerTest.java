package fr.marc.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
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
import fr.marc.paymybuddy.model.User;
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
	private ConnectionController connectionController;
	
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
	@WithMockUser
    public void addAConnectionTest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Connection testConnection = Connection.builder()
				.buddyId(4)
				.user(userRepository.findById(1).get())
				.build();
		assertThat(connectionRepository.findById(8)
				.isEmpty());
        mockMvc.perform(post("/connection")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(testConnection)))
            .andExpect(status().isOk());
        
        assertThat(connectionRepository.findById(8).get().getUser().getId())
        	.isEqualTo(1);
        assertThat(connectionRepository.findById(8).get().getBuddyId())
    		.isEqualTo(4);
        connectionRepository.deleteById(8);
    }
	
	// End point "/buddies"
    @Test
	@WithMockUser
    public void testDisplayArthurBuddiesPage() throws Exception {
    	assertThat(connectionRepository.findById(8)
				.isEmpty());
        mockMvc.perform(get("/buddies?id=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("buddies"))
            .andExpect(content().string(containsString("My buddies")))
            .andExpect(content().string(containsString("Midas King")))
            .andExpect(content().string(containsString("Job Poor")));
    }
	
	// TODO : End point "/adBbuddy"
    @Test
	@WithMockUser
    public void testAddABuddy() throws Exception {
        mockMvc.perform(post("/addBuddy?id=1")
        		.param("id","4")
        		.param("buddyName","Balthazar Picsou")
        		.param("email","bpicsou@mail.fr"))
        .andExpect(status().is(302))
        .andExpect(view().name("redirect:/buddies?id=1"));
        
        assertThat(connectionRepository.findById(8).get().getUser().getId())
    		.isEqualTo(1);
        assertThat(connectionRepository.findById(8).get().getBuddyId())
			.isEqualTo(4);
        //connectionRepository.deleteById(8);
    }
	
	
	// TODO : End point "/connection" DELETE
	

}
