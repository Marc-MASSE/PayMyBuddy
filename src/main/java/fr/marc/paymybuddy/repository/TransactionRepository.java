package fr.marc.paymybuddy.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
	
	public List<Transaction> findAllByUserOrderByIdDesc(User user);
	
	public Transaction findFirstByOrderByTransactionNumberDesc();
	
	public List<Transaction> findAllByTransactionNumber(Integer transaction_number);

	public void deleteAllByTransactionNumber(int id);
	
}
