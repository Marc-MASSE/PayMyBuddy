package fr.marc.paymybuddy.serviceImpl;

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
import fr.marc.paymybuddy.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	static Logger log = LogManager.getLogger(UserServiceImpl.class.getName());
	
	private UserRepository userRepository;
	
	private TransactionRepository transactionRepository;
	
	private ConnectionRepository connectionRepository;
	
	private int sum;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository,TransactionRepository transactionRepository,ConnectionRepository connectionRepository) {
		this.userRepository = userRepository;
		this.transactionRepository = transactionRepository;
		this.connectionRepository = connectionRepository;
	}
	
	
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
		sum = 0;
		try {
			transactions = transactionRepository.findAllByUser(userRepository.findById(id).get());
			transactions.forEach(t -> sum += t.getAmount());
		} catch (Exception e) {
			log.warn("There is no user with id = "+id);
		}
		return sum;
	}
	
	public List<Transaction> getActivity(Integer id) {
		User user = userRepository.findById(id).get();
		return transactionRepository.findAllByUser(user);
		}

	public List<Connection> getBuddies(Integer user_id) {
		User user = userRepository.findById(user_id).get();
		log.info("getBuddies method for - user = "+user.getFirstName()+" "+user.getLastName());
		return connectionRepository.findAllByUser(user);
		}
	
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}

}
