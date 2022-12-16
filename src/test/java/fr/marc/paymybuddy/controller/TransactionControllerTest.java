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

import java.util.Iterator;

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

import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.constants.Treasurer;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.ITransactionService;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TransactionController transactionController;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
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
	
	
	@Test
	void transactionController_is_correctely_called()throws Exception {
	   assertThat(transactionController).isNotNull();
	   }
	
	// End point "/transactions"
	@Test
	@WithMockUser (authorities="ADMIN")
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
	@WithMockUser (authorities="ADMIN")
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
	@WithMockUser (username = "acall@mail.fr")
    public void displayArthurTransferPageTest() throws Exception {
        mockMvc.perform(get("/transfer?message="))
            .andExpect(status().isOk())
            .andExpect(view().name("transfer"))
            .andExpect(content().string(containsString("Send Money")))
            .andExpect(content().string(containsString("My transactions")))
            .andExpect(content().string(containsString("Job Poor")));
    }
	
	// End point "/sendOperation"
    @Test
	@WithMockUser (username = "acall@mail.fr")
    public void sendAnOperationTest() throws Exception {
        mockMvc.perform(post("/sendOperation?id=1&&amount=300&&buddyId=3")
        		.param("description", "Gift"))
            .andExpect(status().is(302))
            .andExpect(view().name("redirect:/transfer?message="));
        
        Integer treasurerId = userRepository.findByEmail(Treasurer.EMAIL).get().getId();
        
        System.out.println("TransactionNumber ="+transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,3,"-300.00").getTransactionNumber());
        
        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,3,"-300.00"))
        		.isNotNull();
        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(3,1,"300.00"))
				.isNotNull();
        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,treasurerId,"-1.50"))
				.isNotNull();
        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(treasurerId,1,"1.50"))
				.isNotNull();
        
        int transactionNumber = transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,3,"-300.00").getTransactionNumber();
        for (int i=0; i<4;i++) {
        	transactionRepository.delete(transactionRepository.findFirstByTransactionNumber(transactionNumber));
        	}
    }
    
    // End point "/confirmation"
	@Nested
	@WithMockUser (username = "acall@mail.fr")
	class displayConfirmationPageTest {
		@Test
	    public void send_100_to_Midas() throws Exception {
	        mockMvc.perform(post("/confirmation")
	        		.param("buddyId","2")
	        		.param("amount", "100"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("confirmation"))
	            .andExpect(content().string(containsString("100")))
	            .andExpect(content().string(containsString("Send Money to")))
	            .andExpect(content().string(containsString("Midas King")));
	    }
	}
	
	// End point "/bankOrder"
	@Nested
	@WithMockUser (username = "acall@mail.fr")
	class displayBankOrderPageTest {
		@Test
	    public void send_100_to_my_bank() throws Exception {
	        mockMvc.perform(post("/bankOrder")
	        		.param("amount", "100")
	        		.param("operationType","-1"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("bankorder"))
	            .andExpect(content().string(containsString("100")))
	            .andExpect(content().string(containsString("Send Money to my bank")));
	    }
		
		@Test
	    public void receive_100_from_my_bank() throws Exception {
	        mockMvc.perform(post("/bankOrder")
	        		.param("amount", "100")
	        		.param("operationType","1"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("bankorder"))
	            .andExpect(content().string(containsString("100")))
	            .andExpect(content().string(containsString("Receive Money from my bank")));
	    }
		
		@Test
	    public void invalid_operation_send_0() throws Exception {
	        mockMvc.perform(post("/bankOrder")
	        		.param("amount", "0")
	        		.param("operationType","-1"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/home?message=Operation and positiv amount are required."));
	    }
		
		@Test
	    public void invalid_operation_negativ_amount() throws Exception {
	        mockMvc.perform(post("/bankOrder")
	        		.param("amount", "-100")
	        		.param("operationType","-1"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/home?message=Operation and positiv amount are required."));
	    }
		
		@Test
	    public void invalid_operation_no_operation_selected() throws Exception {
	        mockMvc.perform(post("/bankOrder")
	        		.param("amount", "100")
	        		.param("operationType","0"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/home?message=Operation and positiv amount are required."));
	    }
	}
	
	// End point "/bankOperation"
	@Nested
	@WithMockUser (username = "acall@mail.fr")
	class BankOperationTest {
		@Test
	    public void send_100_to_my_bank() throws Exception {
	        mockMvc.perform(post("/bankOperation?amount=100&&operationType=-1")
	        		.param("description", "To my bank"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/home?message="));
	        
	        Integer treasurerId = userRepository.findByEmail(Treasurer.EMAIL).get().getId();
	        
	        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,1,"-100.00"))
    				.isNotNull();
	        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,treasurerId,"-0.50"))
					.isNotNull();
	        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(treasurerId,1,"0.50"))
					.isNotNull();
    
		    int transactionNumber = transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,1,"-100.00").getTransactionNumber();
		    for (int i=0; i<3;i++) {
		    	transactionRepository.delete(transactionRepository.findFirstByTransactionNumber(transactionNumber));
		    	}
		}
		
		@Test
	    public void receive_100_from_my_bank() throws Exception {
	        mockMvc.perform(post("/bankOperation?amount=100&&operationType=1")
	        		.param("description", "To my bank"))
	            .andExpect(status().is(302))
	            .andExpect(view().name("redirect:/home?message="));
	        
	        Integer treasurerId = userRepository.findByEmail(Treasurer.EMAIL).get().getId();
	        
	        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,1,"100.00"))
    				.isNotNull();
	        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,treasurerId,"-0.50"))
					.isNotNull();
	        assertThat(transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(treasurerId,1,"0.50"))
					.isNotNull();
    
		    int transactionNumber = transactionRepository.findFirstByUserIdAndBuddyIdAndAmount(1,1,"100.00").getTransactionNumber();
		    for (int i=0; i<3;i++) {
		    	transactionRepository.delete(transactionRepository.findFirstByTransactionNumber(transactionNumber));
		    	}
		}
	}

}
