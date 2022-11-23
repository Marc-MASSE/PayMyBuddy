package fr.marc.paymybuddy.controller;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder.Case;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    
	@ResponseBody
    @PostMapping(value = "/transaction")
    public Transaction addTransaction (@RequestParam int userId, @RequestBody Transaction transaction) {
		log.info("POST request - endpoint /transaction - body = "+transaction);
		User user = userService.getUserById(userId).get();
		transaction.setUser(user);
    	return transactionService.addTransaction(transaction);
    }

	/*
	 * Page "Transfer"
	 * Display buddies list, gifts to buddy list
	 * and a form to send money to a buddy
	 */
    @GetMapping("/transfer")
    public String displayTransferPageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /transfer - id = "+id);
		
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
		
		List<BuddyDTO> buddyList = connectionService.getBuddyList(id);
		model.addAttribute("buddyList",buddyList);
		log.info("Buddy list = "+buddyList);
		
		List<ActivityDTO> transactions = transactionService.getTransactionsById(id);
		model.addAttribute("transactions",transactions);
		log.info("Transactions list = "+transactions);
		
		SendMoneyDTO sendMoneyDTO = new SendMoneyDTO();
		model.addAttribute("sendMoneyDTO",sendMoneyDTO);
		
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
		return "redirect:/transfer?id="+id.toString();
    }
    
	/*
	 * Page "Confirmation"
	 * To confirm Send Money to buddy operation
	 */
    @PostMapping(value = "/confirmation")
    public String displayConfirmationPageById(Model model,SendMoneyDTO sendMoneyDTO,@RequestParam Integer id) {
		log.info("POST request - endpoint /confirmation - body = {}",sendMoneyDTO);
		// buddyId=0 means no buddy selected
		if (sendMoneyDTO.getBuddyId()==0) {
			return "redirect:/transfer?id="+id.toString();
		}else {
			model.addAttribute("user",userService.getUserById(id).get());
			sendMoneyDTO.setUserId(id);
			model.addAttribute("sendMoneyDTO",sendMoneyDTO);
			model.addAttribute("buddyName",userService.getCompleteName(sendMoneyDTO.getBuddyId()));
			Integer amount = Math.negateExact(sendMoneyDTO.getAmount());
			String projectedBalance = transactionService.getProjectedBalance(transactionService.getBalance(id),amount.toString());
			model.addAttribute("projectedBalance",projectedBalance);
			return "confirmation";
		}
    }
    
	/*
	 * Page "Account"
	 * Display the user's balance
	 * and a form to transfer money from the user's bank
	 *
    @GetMapping("/account")
    public String displayAccountPageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /account - id = "+id);
		
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
		
		String balance = transactionService.getBalance(id);
		model.addAttribute("balance",balance);
		
		SendMoneyDTO sendMoneyDTO = new SendMoneyDTO();
		model.addAttribute("sendMoneyDTO",sendMoneyDTO);
		
        return "account";
    }
    */
    
	/*
	 * Page "Account", Receive money from Bank
	 *
    @PostMapping(value = "/bankTransfer/receive")
    public String receiveFromBank(@ModelAttribute("account") SendMoneyDTO sendMoneyDTO,@RequestParam Integer id) {
		sendMoneyDTO.setUserId(id);
		sendMoneyDTO.setBuddyId(id);
		log.info("POST request - endpoint /bankTransfer/receive - body = "+sendMoneyDTO);
		if (sendMoneyDTO.getAmount()>0) {
			transactionService.receiveMoneyFromBank(sendMoneyDTO);
		}
		return "redirect:/account?id="+id.toString();
    }
    */
    
	/*
	 * Page "Account", Receive money from Bank
	 *
    @PostMapping(value = "/bankTransfer/send")
    public String sendToBank(@ModelAttribute("account") SendMoneyDTO sendMoneyDTO,@RequestParam Integer id) {
		sendMoneyDTO.setUserId(id);
		sendMoneyDTO.setBuddyId(id);
		log.info("POST request - endpoint /bankTransfer/send - body = "+sendMoneyDTO);
		if (sendMoneyDTO.getAmount()>0) {
			transactionService.sendMoneyToBank(sendMoneyDTO);
		}
		return "redirect:/account?id="+id.toString();
    }
    */
    
	/*
	 * Page "bankorder"
	 * To confirm bank order operation
	 */
    @PostMapping(value = "/bankOrder")
    public String displayBankOrderPageById(Model model,BankOrderDTO bankOrderDTO,@RequestParam Integer id) {
		log.info("POST request - endpoint /confirmation - body = {}",bankOrderDTO);
		// buddyId=0 means no buddy selected
		if (bankOrderDTO.getOperationType()==0) {
			return "redirect:/home?id="+id.toString();
		}else {
			model.addAttribute("user",userService.getUserById(id).get());
			
			SendMoneyDTO sendMoneyDTO = new SendMoneyDTO();
			model.addAttribute("sendMoneyDTO",sendMoneyDTO);
			
			bankOrderDTO.setUserId(id);
			model.addAttribute("bankOrderDTO",bankOrderDTO);
			
			Integer amount =bankOrderDTO.getAmount()*bankOrderDTO.getOperationType();
			String projectedBalance = transactionService.getProjectedBalance(transactionService.getBalance(id),amount.toString());
			model.addAttribute("projectedBalance",projectedBalance);
			
			return "bankorder";
		}
		
    }
    
	/*
	 * Page "Account", Receive money from Bank
	 */
    @PostMapping(value = "/bankOperation")
    public String bankOperation(@ModelAttribute("bankorder") SendMoneyDTO sendMoneyDTO,
    		@RequestParam Integer id,
    		@RequestParam Integer amount,
    		@RequestParam Integer operationType) {
		log.info("POST request - endpoint /bankOperation - body = "+sendMoneyDTO);
		sendMoneyDTO.setUserId(id);
		sendMoneyDTO.setAmount(amount);
		sendMoneyDTO.setBuddyId(id);
		
		switch(operationType) {
		  case -1:
			  transactionService.sendMoneyToBank(sendMoneyDTO);
			  break;
		  case 1:
			  transactionService.receiveMoneyFromBank(sendMoneyDTO);
			  break;
		}
		return "redirect:/home?id="+id.toString();
    }
    
    
}
