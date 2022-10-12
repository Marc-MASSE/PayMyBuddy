package fr.marc.paymybuddy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	private int sum;
	
	public Iterable<User> getUsers(){
		return userRepository.findAll();
	}
	
	public Optional<User> getUserById(Integer id) {
		return userRepository.findById(id);
	}

	/*
	 * The balance is calculated as the sum of all transaction amounts for a user.
	 */
	public int getBalance(Integer id) {
		List<Transaction> transactions = new ArrayList<>();
		transactions = userRepository.findById(id).get().getTransactions();
		sum = 0;
		transactions.forEach(t -> sum += t.getAmount());
		return sum;
	}
	
	public List<Transaction> getActivity(Integer id) {
		return userRepository.findById(id).get().getTransactions();
	}
	
	
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}

}
