package fr.marc.paymybuddy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.controller.UserController;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;

@Service
public class UserService {
	
	static Logger log = LogManager.getLogger(UserService.class.getName());
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private ConnectionRepository connectionRepository;
	
	
	private int sum;
	
	public Iterable<User> getUsers(){
		return userRepository.findAll();
	}
	
	public Optional<User> getUserById(Integer id) {
		return userRepository.findById(id);
	}
	
	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/*
	 * The balance is calculated as the sum of all transactions amounts for a user.
	 */
	public int getBalance(Integer id) {
		List<Transaction> transactions = new ArrayList<>();
		transactions = transactionRepository.findAllByUser(userRepository.findById(id).get());
		sum = 0;
		transactions.forEach(t -> sum += t.getAmount());
		return sum;
	}
	
	public List<Transaction> getActivity(Integer id) {
		User user = userRepository.findById(id).get();
		return transactionRepository.findAllByUser(user);
		}

	public List<Connection> getBuddies(Integer userId) {
		User user = userRepository.findById(userId).get();
		log.error("getBuddies method for - user = "+user.getFirsName()+" "+user.getLastName());
		return connectionRepository.findAllByUser(user);
		}
	
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}

}
