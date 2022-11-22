package fr.marc.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TransactionController transactionController;
	
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
	void transactionController_is_correctely_called()throws Exception {
	   assertThat(transactionController).isNotNull();
	   }
	
	// End point "/transactions"
	@Test
	@WithMockUser
    public void getTransactions() throws Exception {
        mockMvc.perform(get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].description", is("Initial deposit")))
            .andExpect(jsonPath("$[0].user.id", is(1)))
            .andExpect(jsonPath("$[0].buddyId", is(1)))
            .andExpect(jsonPath("$[3].description", is("Medical support")))
            .andExpect(jsonPath("$[3].user.id", is(1)))
            .andExpect(jsonPath("$[3].buddyId", is(3)));
    }
	
	// End point "/transaction"
	@Nested
	@WithMockUser
	class GetTransactionById {
		@Test
	    public void success() throws Exception {
	        mockMvc.perform(get("/transaction?id=1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.description", is("Initial deposit")))
	            .andExpect(jsonPath("$.user.id", is(1)))
	            .andExpect(jsonPath("$.buddyId", is(1)));
	    }
		
		@Test
	    public void no_answer() throws Exception {
	        mockMvc.perform(get("/transaction?id=15"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").doesNotExist());
	    }
	}
	
	// End point "/transfer"
    @Test
	@WithMockUser
    public void testDisplayArthurTransferPage() throws Exception {
        mockMvc.perform(get("/transfer?id=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("transfer"))
            .andExpect(content().string(containsString("Send Money")))
            .andExpect(content().string(containsString("My transactions")))
            .andExpect(content().string(containsString("Job Poor")));
    }
	
	//TODO : End point "/sendOperation"
	
	
	//TODO : End point "/account"
	
	
	//TODO : End point "/bankTransfer/receive"
	
	
	//TODO : End point "/bankTransfer/send"
	
	

}
