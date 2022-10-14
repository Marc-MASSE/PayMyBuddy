package fr.marc.paymybuddy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	public Iterable<Transaction> getTransactions(){
		return transactionRepository.findAll();
	}
	
	public Optional<Transaction> getTransactionById(Integer id) {
		return transactionRepository.findById(id);
	}
	
	public Transaction addTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

}
