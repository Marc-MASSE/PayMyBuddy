package fr.marc.paymybuddy.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.service.TransactionService;


/*
 * Controller used for end point /transaction
 * For updating transaction's data list
 */

@RestController
public class TransactionController {
	
	static Logger log = LogManager.getLogger(TransactionController.class.getName());

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/transactions")
	public Iterable<Transaction> getTransactions() {
		log.info("GET request - endpoint /users - return the entire list of transactions");
		return transactionService.getTransactions();
	}

}
