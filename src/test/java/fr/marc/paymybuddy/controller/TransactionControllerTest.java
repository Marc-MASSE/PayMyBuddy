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
import fr.marc.paymybuddy.repository.TransactionRepository;
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
    @Test
	@WithMockUser
    public void sendAnOperation() throws Exception {
        mockMvc.perform(post("/sendOperation?id=1&&amount=100&&buddyId=3")
        		.param("description", "Gift"))
            .andExpect(status().is(302))
            .andExpect(view().name("redirect:/transfer?id=1"));
        assertThat(transactionRepository.findById(10).get().getAmount()).isEqualTo("-100.00");
        assertThat(transactionRepository.findById(11).get().getAmount()).isEqualTo("100.00");
        assertThat(transactionRepository.findById(12).get().getAmount()).isEqualTo("-0.50");
        assertThat(transactionRepository.findById(13).get().getAmount()).isEqualTo("0.50");
        transactionRepository.deleteById(10);
        transactionRepository.deleteById(11);
        transactionRepository.deleteById(12);
        transactionRepository.deleteById(13);
    }
    
    //TODO : End point "/confirmation"
	@Nested
	@WithMockUser
	class displayConfirmationPageByIdTest {
		@Test
	    public void success() throws Exception {
			
			SendMoneyDTO sendMoneyDTO = SendMoneyDTO.builder()
					.userId(1)
					.buddyId(2)
					.amount(100)
					.build();
			
	        mockMvc.perform(post("/confirmation?id=1")
	        		//.contentType(MediaType.ALL)
	        		//.content(sendMoneyDTO)
	        		//.param("buddyId","2")
	        		//.param("amount", "100")
	        		)
	            .andExpect(status().is(302))
	            .andExpect(view().name("confirmation"))
	            //.andExpect(content().string(containsString("100.00")))
	            //.andExpect(content().string(containsString("Send Money to")))
	            //.andExpect(content().string(containsString("Midas King")))
	            ;
	    }
		
		@Test
	    public void no_answer() throws Exception {
			
	    }
	}
    
	
	//TODO : End point "/bankOrder"
	
	
	//TODO : End point "/bankOperation"
	
	

}
