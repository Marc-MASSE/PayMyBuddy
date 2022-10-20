package fr.marc.paymybuddy.service;

import java.util.Optional;

import fr.marc.paymybuddy.model.Transaction;

public interface ITransactionService {
	
	Iterable<Transaction> getTransactions();
	
	Optional<Transaction> getTransactionById(Integer id);
	
	Transaction addTransaction(Transaction transaction);
	
	int getNextTransactionNumber();
}
