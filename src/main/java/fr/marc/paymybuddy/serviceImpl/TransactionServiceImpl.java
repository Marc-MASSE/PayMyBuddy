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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.BankOrderDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.constants.Commission;
import fr.marc.paymybuddy.constants.Treasurer;
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
	
	//private IUserService userService;
	
	private BigDecimal sum;
	
	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository,UserRepository userRepository) {
		this.transactionRepository = transactionRepository;
		this.userRepository = userRepository;
	}
	
	
	@Override
	public Iterable<Transaction> getTransactions(){
		return transactionRepository.findAll();
	}
	
	@Override
	public Optional<Transaction> getTransactionById(Integer id) {
		return transactionRepository.findById(id);
	}
	
	/*
	 * User send money to Buddy => 2 transactions
	 * User : transaction with negative amount
	 * Buddy : transaction with positive amount
	 * 
	 */
	@Override
	public void sendMoneyToBuddy (SendMoneyDTO sendMoneyDTO) {
		log.info("Send money from {} to {} pay = {}",sendMoneyDTO.getUserId(),sendMoneyDTO.getBuddyId(),sendMoneyDTO.getAmount());
		
		// User : transaction with negative amount
		Transaction userTransaction = createSendingTransaction(sendMoneyDTO, BigDecimal.ONE);
		
		// Buddy : transaction with positive amount
		Transaction buddyTransaction = createReceivingTransaction(sendMoneyDTO, BigDecimal.ONE);
		
		// PayMyBuddy : commission 0.5% deducted from user's account
		sendMoneyDTO.setBuddyId(userRepository.findByEmail(Treasurer.EMAIL).get().getId());
		Transaction sendCommissionTransaction = createSendingTransaction(sendMoneyDTO, Commission.AMOUNT);
		Transaction receiveCommissionTransaction = createReceivingTransaction(sendMoneyDTO, Commission.AMOUNT);
		
		addTransaction(userTransaction);
		addTransaction(buddyTransaction);
		addTransaction(sendCommissionTransaction);
		addTransaction(receiveCommissionTransaction);
    }
    
	/*
	 * User receive money from his bank => 1 transaction
	 * User and Buddy are the same 
	 */
	@Override
    public void receiveMoneyFromBank (SendMoneyDTO sendMoneyDTO) {
		log.info("Send money from {} to {} pay = {}",sendMoneyDTO.getUserId(),sendMoneyDTO.getBuddyId(),sendMoneyDTO.getAmount());
		
		Transaction userTransaction = createReceivingTransaction(sendMoneyDTO, BigDecimal.ONE);
		
		// PayMyBuddy : commission 0.5% deducted from user's account
		sendMoneyDTO.setBuddyId(userRepository.findByEmail(Treasurer.EMAIL).get().getId());
		Transaction sendCommissionTransaction = createSendingTransaction(sendMoneyDTO, Commission.AMOUNT);
		Transaction receiveCommissionTransaction = createReceivingTransaction(sendMoneyDTO, Commission.AMOUNT);
		
		addTransaction(userTransaction);
		addTransaction(sendCommissionTransaction);
		addTransaction(receiveCommissionTransaction);
    }
    
	/*
	 * User receive money from his bank => 1 transaction
	 * User and Buddy are the same 
	 */
	@Override
    public void sendMoneyToBank (SendMoneyDTO sendMoneyDTO) {
		log.info("Send money from {} to {} pay = {}",sendMoneyDTO.getUserId(),sendMoneyDTO.getBuddyId(),sendMoneyDTO.getAmount());
		
		Transaction userTransaction = createSendingTransaction(sendMoneyDTO, BigDecimal.ONE);
		
		// PayMyBuddy : commission 0.5% deducted from user's account
		sendMoneyDTO.setBuddyId(userRepository.findByEmail(Treasurer.EMAIL).get().getId());
		Transaction sendCommissionTransaction = createSendingTransaction(sendMoneyDTO, Commission.AMOUNT);
		Transaction receiveCommissionTransaction = createReceivingTransaction(sendMoneyDTO, Commission.AMOUNT);
		
		addTransaction(userTransaction);
		addTransaction(sendCommissionTransaction);
		addTransaction(receiveCommissionTransaction);
    }
    
	/*
	 * The balance is calculated as the sum of all transactions amounts for a user.
	 */
	@Override
	public String getBalance(Integer id) {
		List<Transaction> transactions = new ArrayList<>();
		//sum = (new BigDecimal(0.00)).setScale(2,RoundingMode.HALF_EVEN);
		sum = BigDecimal.ZERO;
		try {
			transactions = transactionRepository.findAllByUserOrderByIdDesc(userRepository.findById(id).get());
			transactions.forEach(t -> sum = sum.add(new BigDecimal(t.getAmount())));
		} catch (Exception e) {
			log.warn("There is no user with id = "+id);
		}
		return sum.toString();
	}
	
	@Override
	public List<Transaction> getActivity(Integer id) {
		User user = userRepository.findById(id).get();
		return transactionRepository.findAllByUserOrderByIdDesc(user);
		}
	
	@Override
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
		//Page<ActivityDTO> activitiesPage = activitiesDTO(new PageRequest(0,5));
		//int lineNumber = 5;
		//int start = (page-1)*lineNumber;
		//int end = Math.min(page*lineNumber,activitiesDTO.size());
		//System.out.println("Page "+page+" : " + activitiesDTO.subList(start,end));
		
		return activitiesDTO;
	}
	
	/*
	 * List of payments to buddies
	 * A payment means that the amount is negative and the buddy isn't the user or PayMyBuddy
	 */
	@Override
	public List<ActivityDTO> getTransactionsById(Integer id) {
		List<ActivityDTO> transactions = new ArrayList<>();
		List<ActivityDTO> activities = getActivityById(id);
		activities.forEach(a -> {
			BigDecimal BDAmount = new BigDecimal(a.getAmount());
			if (BDAmount.compareTo(BigDecimal.ZERO)<0&&
				!a.getBuddyName().contains(userRepository.findById(id).get().getFirstName())&&
				!a.getBuddyName().contains(userRepository.findById(id).get().getLastName())&&
				!a.getBuddyName().contains("PayMyBuddy")) {
				a.setAmount(BDAmount.negate().toString());
				transactions.add(a);
			}
		});
		return transactions;
	}
	
	/*
	 * Creates a sending transaction from a sendMoneyDTO.
	 * The sender is the user, the receiver is the buddy, the amount is negative.
	 * If it's a simple transaction, the amount is multiplied by 1.
	 * If it's a commission, the amount is multiplied by 0,005.
	 */
	@Override
	public Transaction createSendingTransaction(SendMoneyDTO sendMoneyDTO,BigDecimal multiplier) {
		
		BigDecimal BDAmount = new BigDecimal(sendMoneyDTO.getAmount()).setScale(2,RoundingMode.HALF_EVEN);
		Transaction transaction = new Transaction();
		
		transaction.setTransactionNumber(getNextTransactionNumber());
		transaction.setBuddyId(sendMoneyDTO.getBuddyId());
		transaction.setDescription(sendMoneyDTO.getDescription());
		BigDecimal resultAmount = BDAmount.multiply(multiplier).setScale(2,RoundingMode.HALF_EVEN);
		transaction.setAmount(resultAmount.negate().toString());
		transaction.setDate(LocalDate.now());
		User user = userRepository.findById(sendMoneyDTO.getUserId()).get();
		transaction.setUser(user);
		
		return transaction;
	}
	
	/*
	 * Creates a receiving transaction from a sendMoneyDTO.
	 * The sender is the buddy, the receiver is the user, the amount is positive.
	 * If it's a simple transaction, the amount is multiplied by 1.
	 * If it's a commission, the amount is multiplied by 0,005.
	 */
	@Override
	public Transaction createReceivingTransaction(SendMoneyDTO sendMoneyDTO,BigDecimal multiplier) {
		
		BigDecimal BDAmount = new BigDecimal(sendMoneyDTO.getAmount()).setScale(2,RoundingMode.HALF_EVEN);
		Transaction transaction = new Transaction();
		
		transaction.setTransactionNumber(getNextTransactionNumber());
		transaction.setBuddyId(sendMoneyDTO.getUserId());
		transaction.setDescription(sendMoneyDTO.getDescription());
		BigDecimal resultAmount = BDAmount.multiply(multiplier).setScale(2,RoundingMode.HALF_EVEN);
		transaction.setAmount(resultAmount.toString());
		transaction.setDate(LocalDate.now());
		transaction.setUser(userRepository.findById(sendMoneyDTO.getBuddyId()).get());
		
		return transaction;
	}
	
	@Override
	public Transaction addTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}
	
	@Override
	public int getNextTransactionNumber() {
		return transactionRepository.findFirstByOrderByTransactionNumberDesc().getTransactionNumber()+1;
	}
	
	@Override
	public String getProjectedBalance(String balance, String amount) {
		BigDecimal projectedBalance = (new BigDecimal(balance)).setScale(2,RoundingMode.HALF_EVEN);
		BigDecimal BDAmount = (new BigDecimal(amount)).setScale(2,RoundingMode.HALF_EVEN);
		BigDecimal commission = BDAmount.multiply(Commission.AMOUNT).setScale(2,RoundingMode.HALF_EVEN);
		projectedBalance = projectedBalance.add(BDAmount).subtract(commission.abs());
		return projectedBalance.toString();
	}
	
}
