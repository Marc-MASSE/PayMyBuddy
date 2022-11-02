package fr.marc.paymybuddy.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.DTO.LoginDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.controller.UserController;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.ITransactionService;
import fr.marc.paymybuddy.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	static Logger log = LogManager.getLogger(UserServiceImpl.class.getName());
	
	private UserRepository userRepository;
	
	private TransactionRepository transactionRepository;
	
	private ConnectionRepository connectionRepository;
	
	private ITransactionService transactionService;
	
	private int sum;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository,TransactionRepository transactionRepository,ConnectionRepository connectionRepository,ITransactionService transactionService) {
		this.userRepository = userRepository;
		this.transactionRepository = transactionRepository;
		this.connectionRepository = connectionRepository;
		this.transactionService = transactionService;
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
			transactions = transactionRepository.findAllByUserOrderByIdDesc(userRepository.findById(id).get());
			transactions.forEach(t -> sum += t.getAmount());
		} catch (Exception e) {
			log.warn("There is no user with id = "+id);
		}
		return sum;
	}
	
	public List<Transaction> getActivity(Integer id) {
		User user = userRepository.findById(id).get();
		return transactionRepository.findAllByUserOrderByIdDesc(user);
		}
	
	public List<ActivityDTO> getActivityById(Integer id) {
		User user = userRepository.findById(id).get();
		List<Transaction> activities = transactionRepository.findAllByUserOrderByIdDesc(user);
		List<ActivityDTO> activitiesDTO = new ArrayList<>();
		activities.forEach(a -> {
			ActivityDTO activityDTO = new ActivityDTO();
			// arrow=true(right) if amount>=0, arrow= false(left) unless
			activityDTO.setArrow(a.getAmount()>=0);
			User buddy = userRepository.findById(a.getBuddy_id()).get();
			activityDTO.setBuddyName(buddy.getFirstName()+" "+buddy.getLastName());
			activityDTO.setDate(a.getDate().toString());
			activityDTO.setDescription(a.getDescription());
			activityDTO.setAmount(a.getAmount());
			activitiesDTO.add(activityDTO);
		});
		return activitiesDTO;
	}
	
	/*
	 * List of payments to buddies
	 * A payment means that the amount is negative and the buddy isn't the user
	 */
	public List<ActivityDTO> getTransactionsById(Integer id) {
		List<ActivityDTO> transactions = new ArrayList<>();
		List<ActivityDTO> activities = getActivityById(id);
		activities.forEach(a -> {
			if (a.getAmount()<0&&
				!a.getBuddyName().contains(getUserById(id).get().getFirstName())&&
				!a.getBuddyName().contains(getUserById(id).get().getLastName())) {
				a.setAmount(-a.getAmount());
				transactions.add(a);
			}
		});
		return transactions;
	}
	
	/*
	public List<Connection> getBuddies(Integer user_id) {
		User user = userRepository.findById(user_id).get();
		log.info("getBuddies method for - user = "+user.getFirstName()+" "+user.getLastName());
		return connectionRepository.findAllByUser(user);
		}
	*/
	
	public List<BuddyDTO> getBuddyList(Integer user_id) {
		User user = userRepository.findById(user_id).get();
		log.info("getBuddyList method for - id = "+user_id);
		List<Connection> buddies = connectionRepository.findAllByUser(user);
		List<BuddyDTO> buddyList = new ArrayList<>();
		buddies.forEach(b -> {
			BuddyDTO buddy = new BuddyDTO();
			buddy.setId(b.getBuddy_id());
			buddy.setBuddyName(getUserById(b.getBuddy_id()).get().getFirstName()+" "+getUserById(b.getBuddy_id()).get().getLastName());
			buddyList.add(buddy);
		});
		return buddyList;
		}
	
	/*
	 * If email unknown return 0.
	 * If password doesn't match return -1.
	 * If email and password matched return the user id.
	 */
	public int verifyLogin(LoginDTO loginDTO) {
		int user_id = -1;
		try {
			User user = getUserByEmail(loginDTO.getEmail()).get();
			if (user.getPassword().equals(loginDTO.getPassword())) {
				user_id = user.getId();
			}
		} catch (Exception e) {
			user_id = 0;
		}
		return user_id;
	}
	
	/*
	 * User send money to Buddy => 2 transactions
	 * User : transaction with negative amount
	 * Buddy : transaction with positive amount
	 * 
	 */
    public void sendMoneyToBuddy (SendMoneyDTO sendMoneyDTO) {
		log.info("POST request - endpoint /sendmoney - from "+sendMoneyDTO.getUser_id()+" to "+sendMoneyDTO.getBuddy_id()+" pay = "+sendMoneyDTO.getAmount());
		
		// User : transaction with negative amount
		Transaction userTransaction = new Transaction();
		userTransaction.setTransactionNumber(transactionService.getNextTransactionNumber());
		userTransaction.setBuddy_id(sendMoneyDTO.getBuddy_id());
		userTransaction.setDescription(sendMoneyDTO.getDescription());
		userTransaction.setAmount(-sendMoneyDTO.getAmount());
		userTransaction.setDate(LocalDate.now());
		userTransaction.setUser(getUserById(sendMoneyDTO.getUser_id()).get());
		
		// Buddy : transaction with positive amount
		Transaction buddyTransaction = new Transaction();
		buddyTransaction.setTransactionNumber(transactionService.getNextTransactionNumber());
		buddyTransaction.setBuddy_id(sendMoneyDTO.getUser_id());
		buddyTransaction.setDescription(sendMoneyDTO.getDescription());
		buddyTransaction.setAmount(sendMoneyDTO.getAmount());
		buddyTransaction.setDate(LocalDate.now());
		buddyTransaction.setUser(getUserById(sendMoneyDTO.getBuddy_id()).get());
		
		transactionService.addTransaction(userTransaction);
		transactionService.addTransaction(buddyTransaction);
    }
	
	
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}

}
