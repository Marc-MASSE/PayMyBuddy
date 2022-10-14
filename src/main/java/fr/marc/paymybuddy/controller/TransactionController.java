package fr.marc.paymybuddy.controller;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.TransactionService;
import fr.marc.paymybuddy.service.UserService;


/*
 * Controller used for end point /transaction
 * For updating transaction's data list
 */

@RestController
public class TransactionController {
	
	static Logger log = LogManager.getLogger(TransactionController.class.getName());

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserService userService;

	@GetMapping("/transactions")
	public Iterable<Transaction> getTransactions() {
		log.info("GET request - endpoint /transactions - return the entire list of transactions");
		return transactionService.getTransactions();
	}
	
    @GetMapping("/transaction")
    public Optional<Transaction> getTransactionById(@RequestParam int id) {
		log.info("GET request - endpoint /transaction - id = "+id);
        return transactionService.getTransactionById(id);
    }
    
    @PostMapping(value = "/transaction")
    public Transaction addTransaction (@RequestBody Transaction transaction) {
		log.info("POST request - endpoint /transaction - body = "+transaction);
		User user = userService.getUserById(1).get();
		transaction.setUser(user);
    	return transactionService.addTransaction(transaction);
    }

}
