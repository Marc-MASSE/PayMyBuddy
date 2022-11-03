package fr.marc.paymybuddy.service;

import java.util.List;
import java.util.Optional;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.model.Transaction;

public interface ITransactionService {
	
	Iterable<Transaction> getTransactions();
	
	Optional<Transaction> getTransactionById(Integer id);
	
	public void sendMoneyToBuddy (SendMoneyDTO sendMoneyDTO);
	
	public int getBalance(Integer id);
	
	public List<Transaction> getActivity(Integer id);
	
	public List<ActivityDTO> getActivityById(Integer id);
	
	public List<ActivityDTO> getTransactionsById(Integer id);
	
	Transaction addTransaction(Transaction transaction);
	
	int getNextTransactionNumber();
}
