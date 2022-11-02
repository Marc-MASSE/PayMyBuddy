package fr.marc.paymybuddy.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.service.ITransactionService;

@Service
public class TransactionServiceImpl implements ITransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	
	public Iterable<Transaction> getTransactions(){
		return transactionRepository.findAll();
	}
	
	public Optional<Transaction> getTransactionById(Integer id) {
		return transactionRepository.findById(id);
	}
	
	public Transaction addTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}
	
	public int getNextTransactionNumber() {
		return transactionRepository.findFirstByOrderByTransactionNumberDesc().getTransactionNumber()+1;
	}

}