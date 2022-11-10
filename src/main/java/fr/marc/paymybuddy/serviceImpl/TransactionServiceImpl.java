package fr.marc.paymybuddy.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.ITransactionService;
import fr.marc.paymybuddy.service.IUserService;

@Service
public class TransactionServiceImpl implements ITransactionService {
	
	static Logger log = LogManager.getLogger(TransactionServiceImpl.class.getName());
	
	private TransactionRepository transactionRepository;
	
	private UserRepository userRepository;
	
	private IUserService userService;
	
	private BigDecimal sum;
	
	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository,UserRepository userRepository,IUserService userService) {
		this.transactionRepository = transactionRepository;
		this.userRepository = userRepository;
		this.userService = userService;
	}
	
	
	public Iterable<Transaction> getTransactions(){
		return transactionRepository.findAll();
	}
	
	public Optional<Transaction> getTransactionById(Integer id) {
		return transactionRepository.findById(id);
	}
	
	/*
	 * User send money to Buddy => 2 transactions
	 * User : transaction with negative amount
	 * Buddy : transaction with positive amount
	 * 
	 */
    public void sendMoneyToBuddy (SendMoneyDTO sendMoneyDTO) {
		log.info("Send money from "+sendMoneyDTO.getUserId()+" to "+sendMoneyDTO.getBuddyId()+" pay = "+sendMoneyDTO.getAmount());
		
		// User : transaction with negative amount
		Transaction userTransaction = new Transaction();
		userTransaction.setTransactionNumber(getNextTransactionNumber());
		userTransaction.setBuddyId(sendMoneyDTO.getBuddyId());
		userTransaction.setDescription(sendMoneyDTO.getDescription());
		userTransaction.setAmount("-"+sendMoneyDTO.getAmount().toString());
		userTransaction.setDate(LocalDate.now());
		userTransaction.setUser(userService.getUserById(sendMoneyDTO.getUserId()).get());
		
		// Buddy : transaction with positive amount
		Transaction buddyTransaction = new Transaction();
		buddyTransaction.setTransactionNumber(getNextTransactionNumber());
		buddyTransaction.setBuddyId(sendMoneyDTO.getUserId());
		buddyTransaction.setDescription(sendMoneyDTO.getDescription());
		buddyTransaction.setAmount(sendMoneyDTO.getAmount().toString());
		buddyTransaction.setDate(LocalDate.now());
		buddyTransaction.setUser(userService.getUserById(sendMoneyDTO.getBuddyId()).get());
		
		addTransaction(userTransaction);
		addTransaction(buddyTransaction);
    }
    
	/*
	 * User receive money from his bank => 1 transaction
	 * User and Buddy are the same 
	 */
    public void receiveMoneyFromBank (SendMoneyDTO sendMoneyDTO) {
		log.info("Send money from "+sendMoneyDTO.getUserId()+" to "+sendMoneyDTO.getBuddyId()+" pay = "+sendMoneyDTO.getAmount());
		
		Transaction userTransaction = new Transaction();
		userTransaction.setTransactionNumber(getNextTransactionNumber());
		userTransaction.setBuddyId(sendMoneyDTO.getUserId());
		userTransaction.setDescription(sendMoneyDTO.getDescription());
		userTransaction.setAmount(sendMoneyDTO.getAmount().toString());
		userTransaction.setDate(LocalDate.now());
		userTransaction.setUser(userService.getUserById(sendMoneyDTO.getUserId()).get());
		
		addTransaction(userTransaction);
    }
    
	/*
	 * The balance is calculated as the sum of all transactions amounts for a user.
	 */
	public String getBalance(Integer id) {
		List<Transaction> transactions = new ArrayList<>();
		sum = (new BigDecimal(0.00)).setScale(2,RoundingMode.HALF_EVEN);
		try {
			transactions = transactionRepository.findAllByUserOrderByIdDesc(userRepository.findById(id).get());
			transactions.forEach(t -> sum = sum.add(new BigDecimal(t.getAmount())));
		} catch (Exception e) {
			log.warn("There is no user with id = "+id);
		}
		return sum.toString();
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
			activityDTO.setArrow(new BigDecimal(a.getAmount()).compareTo(BigDecimal.ZERO)>0);
			User buddy = userRepository.findById(a.getBuddyId()).get();
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
			BigDecimal amount = new BigDecimal(a.getAmount());
			if (amount.compareTo(BigDecimal.ZERO)<0&&
				!a.getBuddyName().contains(userService.getUserById(id).get().getFirstName())&&
				!a.getBuddyName().contains(userService.getUserById(id).get().getLastName())) {
				a.setAmount(amount.negate().toString());
				transactions.add(a);
			}
		});
		return transactions;
	}
	
	
	
	public Transaction addTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}
	
	public int getNextTransactionNumber() {
		return transactionRepository.findFirstByOrderByTransactionNumberDesc().getTransactionNumber()+1;
	}

}
