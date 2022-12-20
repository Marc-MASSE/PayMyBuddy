package fr.marc.paymybuddy.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.BankOrderDTO;
import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.IConnectionService;
import fr.marc.paymybuddy.service.ITransactionService;
import fr.marc.paymybuddy.service.IUserService;


/*
 * Controller used for end point /transaction
 * For updating transaction's data list
 */

@Controller
public class TransactionController {
	
	static Logger log = LogManager.getLogger(TransactionController.class.getName());

	@Autowired
	private ITransactionService transactionService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IConnectionService connectionService;

	@ResponseBody
	@GetMapping("/transactions")
	public Iterable<Transaction> getTransactions() {
		log.info("GET request - endpoint /transactions - return the entire list of transactions");
		return transactionService.getTransactions();
	}
	
	@ResponseBody
    @GetMapping("/transaction")
    public Optional<Transaction> getTransactionById(@RequestParam int id) {
		log.info("GET request - endpoint /transaction - id = "+id);
        return transactionService.getTransactionById(id);
    }
    
	/*
	 * Page "Transfer"
	 * Display buddies list, gifts to buddy list
	 * and a form to send money to a buddy
	 */
    @GetMapping("/transfer")
    public String displayTransferPageById(Model model, 
    		@RequestParam (name="message", defaultValue="") String message,
    		@RequestParam(name="page", defaultValue="1") int page) {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
    	log.info("GET request - endpoint /transfer - email = "+connectedEmail);
		
		model.addAttribute("user",user);
		
		List<BuddyDTO> buddyList = connectionService.getBuddyList(user.getId());
		model.addAttribute("buddyList",buddyList);
		log.info("Buddy list = "+buddyList);
		
		List<ActivityDTO> transactions = transactionService.getTransactionsById(user.getId());
		int linesNumber = 3;
		int start = (page-1)*linesNumber;
		int end = Math.min(page*linesNumber,transactions.size());
		model.addAttribute("transactions",transactions.subList(start,end));
		log.info("Transactions list = "+transactions);
		
		int pagesNumber = (int) (Math.ceil(((float)transactions.size()/(float)linesNumber)));
		int[] pages = new int[pagesNumber];
		for(int i=0;i<pagesNumber;i++) pages[i]=i+1;
		model.addAttribute("pages",pages);
		
		model.addAttribute("currentPage",page);
		model.addAttribute("maxPage",pagesNumber);
		
		SendMoneyDTO sendMoneyDTO = new SendMoneyDTO();
		model.addAttribute("sendMoneyDTO",sendMoneyDTO);
		
		model.addAttribute("message",message);
		
        return "transfer";
    }
	
	/*
	 * Page "Transfer", send operation
	 */
    @PostMapping(value = "/sendOperation")
    public String sendOperation(@ModelAttribute("confirmation") SendMoneyDTO sendMoneyDTO,
    		@RequestParam Integer id,
    		@RequestParam Integer amount,
    		@RequestParam Integer buddyId) {
		log.info("POST request - endpoint /sendOperation - body = "+sendMoneyDTO);
		sendMoneyDTO.setUserId(id);
		sendMoneyDTO.setAmount(amount);
		sendMoneyDTO.setBuddyId(buddyId);
		transactionService.sendMoneyToBuddy(sendMoneyDTO);
		return "redirect:/transfer?message=";
    }
    
	/*
	 * Page "Confirmation"
	 * To confirm Send Money to buddy operation
	 */
    @PostMapping(value = "/confirmation")
    public String displayConfirmationPage(Model model,@ModelAttribute SendMoneyDTO sendMoneyDTO) {
		log.info("POST request - endpoint /confirmation - body = {}",sendMoneyDTO);
		
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		
		// buddyId=0 means no buddy selected
		if (sendMoneyDTO.getBuddyId()==0 || sendMoneyDTO.getAmount()==null || sendMoneyDTO.getAmount()<=0) {
			return "redirect:/transfer?message=Buddy and positiv amount are required.";
		}else {
			model.addAttribute("user",userService.getUserById(user.getId()).get());
			sendMoneyDTO.setUserId(user.getId());
			model.addAttribute("sendMoneyDTO",sendMoneyDTO);
			model.addAttribute("buddyName",userService.getCompleteName(sendMoneyDTO.getBuddyId()));
			Integer amount = Math.negateExact(sendMoneyDTO.getAmount());
			String projectedBalance = transactionService.getProjectedBalance(transactionService.getBalance(user.getId()),amount.toString());
			model.addAttribute("projectedBalance",projectedBalance);
			return "confirmation";
		}
    }
    
    
	/*
	 * Page "bankorder"
	 * To confirm bank order operation
	 */
    @PostMapping(value = "/bankOrder")
    public String displayBankOrderPage(Model model,@ModelAttribute BankOrderDTO bankOrderDTO) {
		log.info("POST request - endpoint /confirmation - body = {}",bankOrderDTO);
		
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		
		// buddyId=0 means no buddy selected
		if (bankOrderDTO.getOperationType()==0 || bankOrderDTO.getAmount()<=0) {
			String message = "Operation and positiv amount are required.";
			return "redirect:/home?message="+message;
		}else {
			SendMoneyDTO sendMoneyDTO = new SendMoneyDTO();
			model.addAttribute("sendMoneyDTO",sendMoneyDTO);
			
			bankOrderDTO.setUserId(user.getId());
			model.addAttribute("bankOrderDTO",bankOrderDTO);
			
			Integer amount =bankOrderDTO.getAmount()*bankOrderDTO.getOperationType();
			String projectedBalance = transactionService.getProjectedBalance(transactionService.getBalance(user.getId()),amount.toString());
			model.addAttribute("projectedBalance",projectedBalance);
			
			return "bankorder";
		}
    }
    
	/*
	 * Page "Bankorder", Receive or Send money from Bank
	 * operationType = -1 means to send money to user's bank
	 * operationType = 1 means to receive money from user's bank
	 */
    @PostMapping(value = "/bankOperation")
    public String bankOperation(@ModelAttribute("bankorder") SendMoneyDTO sendMoneyDTO,
    		@RequestParam Integer amount,
    		@RequestParam Integer operationType) {
		log.info("POST request - endpoint /bankOperation - body = "+sendMoneyDTO);
		
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		
		sendMoneyDTO.setUserId(user.getId());
		sendMoneyDTO.setAmount(amount);
		sendMoneyDTO.setBuddyId(user.getId());
		
		switch(operationType) {
		  case -1:
			  transactionService.sendMoneyToBank(sendMoneyDTO);
			  break;
		  case 1:
			  transactionService.receiveMoneyFromBank(sendMoneyDTO);
			  break;
		}
		return "redirect:/home?message=";
    }
    
    
}
