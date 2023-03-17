package fr.marc.paymybuddy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.model.Transaction;

public interface ITransactionService {
	
	Iterable<Transaction> getTransactions();
	
	Optional<Transaction> getTransactionById(Integer id);
	
	public void sendMoneyToBuddy (SendMoneyDTO sendMoneyDTO);
	
	public void receiveMoneyFromBank (SendMoneyDTO sendMoneyDTO);
	
	public void sendMoneyToBank (SendMoneyDTO sendMoneyDTO);
	
	public String getBalance(Integer id);
	
	public List<Transaction> getActivity(Integer id);
	
	public List<ActivityDTO> getActivityById(Integer id);
	//public List<ActivityDTO> getActivityById(Integer id, Integer page);
	
	public List<ActivityDTO> getTransactionsById(Integer id);
	
	public Transaction createSendingTransaction(SendMoneyDTO sendMoneyDTO,BigDecimal multiplier);
	
	public Transaction createReceivingTransaction(SendMoneyDTO sendMoneyDTO,BigDecimal multiplier);
	
	Transaction addTransaction(Transaction transaction);
	
	int getNextTransactionNumber();

	String getProjectedBalance(String balance, String amount);

}
