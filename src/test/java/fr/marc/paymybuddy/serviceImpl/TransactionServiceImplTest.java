package fr.marc.paymybuddy.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.constants.Commission;
import fr.marc.paymybuddy.constants.Treasurer;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.ITransactionService;
import fr.marc.paymybuddy.service.IUserService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
	
	private ITransactionService transactionService;
	
	@Mock
	private TransactionRepository transactionRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private IUserService userService;
	
	@Captor
	ArgumentCaptor<Transaction> transactionCaptor;

	private User user1;
	private User user2;
	private Transaction transaction1;
	private Transaction transaction2;
	private Transaction transaction3;
	private Transaction transaction;
	private Transaction transaction1ToAdd;
	private ActivityDTO activityDTO1;
	private ActivityDTO activityDTO2;
	private ActivityDTO activityDTO3;
	private SendMoneyDTO sendMoneyDTO;
	
	@BeforeEach
	public void init() {
		transactionService = new TransactionServiceImpl(transactionRepository,userRepository);
		user1 = User.builder()
				.id(1)
				.email("user1@mail.fr")
				.firstName("Prénom1")
				.lastName("Nom1")
				.password("111")
				.iban("FR001")
				.bank("Banque1")
				.build();
		user2 = User.builder()
				.id(2)
				.email("user2@mail.fr")
				.firstName("Prénom2")
				.lastName("Nom2")
				.password("222")
				.iban("FR002")
				.bank("Banque2")
				.build();
		transaction1 = Transaction.builder()
				.id(1)
				.transactionNumber(1)
				.buddyId(1)
				.description("Initial deposit")
				.amount("100")
				.date(LocalDate.now())
				.done(false)
				.build();
		transaction2 = Transaction.builder()
				.id(2)
				.transactionNumber(2)
				.buddyId(2)
				.description("Gift")
				.amount("55")
				.date(LocalDate.now())
				.done(false)
				.build();
		transaction3 = Transaction.builder()
				.id(3)
				.transactionNumber(3)
				.buddyId(2)
				.description("Repayment")
				.amount("-35")
				.date(LocalDate.now())
				.done(false)
				.build();
	}
	
	@Test
	public void getTransactions_success() {
		when(transactionRepository.findAll())
			.thenReturn(Arrays.asList(transaction1,transaction2));
		assertThat(transactionService.getTransactions())
			.contains(transaction1)
			.contains(transaction2);
		verify(transactionRepository).findAll();
	}
	
	@Nested
	class GetTransactionById {
		@Test
		public void success() {
			when(transactionRepository.findById(1))
				.thenReturn(Optional.of(transaction1));
			assertThat(transactionService.getTransactionById(1))
				.isEqualTo(Optional.of(transaction1));
			verify(transactionRepository).findById(1);
		}
		@Test
		public void no_transaction() {
			when(transactionRepository.findById(15))
				.thenReturn(null);
			assertThat(transactionService.getTransactionById(15))
				.isNull();
			verify(transactionRepository).findById(15);
		}
	}
	
	@Nested
	class SendMoneyToBuddyTest {
		@Test
		public void simpleSending() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(2)
				.description("Gift")
				.amount(100)
				.build();
			Transaction userTransactionTest = new Transaction();
			userTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(2)
				.description("Gift")
				.amount("-100.00")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			Transaction buddyTransactionTest = new Transaction();
			buddyTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("Gift")
				.amount("100.00")
				.date(LocalDate.now())
				.done(false)
				.user(user2)
				.build();
			Transaction sendCommissionTransactionTest = new Transaction();
			sendCommissionTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(3)
				.description("Gift")
				.amount("-0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			Transaction receiveCommissionTransactionTest = new Transaction();
			receiveCommissionTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("Gift")
				.amount("0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user2)
				.build();
			User user3 = User.builder()
					.id(3)
					.email(Treasurer.EMAIL)
					.firstName("")
					.lastName("")
					.password("admin123")
					.iban("FR001")
					.bank("Banque1")
					.build();
			
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(user1))
				.thenReturn(Optional.of(user2))
				.thenReturn(Optional.of(user2));
			
			when(userRepository.findByEmail(Treasurer.EMAIL))
				.thenReturn(Optional.of(user3));
			
			when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(userTransactionTest)
				.thenReturn(buddyTransactionTest)
				.thenReturn(sendCommissionTransactionTest)
				.thenReturn(receiveCommissionTransactionTest);
			
			transactionService.sendMoneyToBuddy(sendMoneyDTO);
			
			verify(transactionRepository,times(4)).save(transactionCaptor.capture());
			
			assertThat(transactionCaptor.getAllValues())
				.extracting("buddyId","amount")
				.contains(tuple(2,"-100.00"),tuple(1,"100.00"),tuple(3,"-0.50"),tuple(1,"0.50"));
			
			verify(transactionRepository,times(4)).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository,times(4)).findById(anyInt());
			verify(userRepository).findByEmail(Treasurer.EMAIL);
			verify(transactionRepository,times(4)).save(any(Transaction.class));
		
		}
	}	
	
	@Nested
	class ReceiveMoneyFromBankTest {
		@Test
		public void simpleReceiving() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(1)
				.description("From my bank")
				.amount(100)
				.build();
			Transaction userTransactionTest = new Transaction();
			userTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("From my bank")
				.amount("100.00")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			Transaction sendCommissionTransactionTest = new Transaction();
			sendCommissionTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(3)
				.description("From my bank")
				.amount("-0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			Transaction receiveCommissionTransactionTest = new Transaction();
			receiveCommissionTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("From my bank")
				.amount("0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			User user3 = User.builder()
					.id(3)
					.email(Treasurer.EMAIL)
					.firstName("")
					.lastName("")
					.password("admin123")
					.iban("FR001")
					.bank("Banque1")
					.build();
			
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(user1))
				.thenReturn(Optional.of(user1))
				.thenReturn(Optional.of(user1));
			
			when(userRepository.findByEmail(Treasurer.EMAIL))
				.thenReturn(Optional.of(user3));
			
			when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(userTransactionTest)
				.thenReturn(sendCommissionTransactionTest)
				.thenReturn(receiveCommissionTransactionTest);
			
			transactionService.receiveMoneyFromBank(sendMoneyDTO);
			
			verify(transactionRepository,times(3)).save(transactionCaptor.capture());
			
			assertThat(transactionCaptor.getAllValues())
				.extracting("buddyId","amount")
				.contains(tuple(1,"100.00"),tuple(3,"-0.50"),tuple(1,"0.50"));
			
			verify(transactionRepository,times(3)).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository,times(3)).findById(anyInt());
			verify(userRepository).findByEmail(Treasurer.EMAIL);
			verify(transactionRepository,times(3)).save(any(Transaction.class));
		}
	}	
	
	@Nested
	class SendMoneyFromBankTest {
		@Test
		public void simpleSending() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(1)
				.description("To my bank")
				.amount(100)
				.build();
			Transaction userTransactionTest = new Transaction();
			userTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("To my bank")
				.amount("-100.00")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			Transaction sendCommissionTransactionTest = new Transaction();
			sendCommissionTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(3)
				.description("From my bank")
				.amount("-0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			Transaction receiveCommissionTransactionTest = new Transaction();
			receiveCommissionTransactionTest = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("From my bank")
				.amount("0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			User user3 = User.builder()
					.id(3)
					.email(Treasurer.EMAIL)
					.firstName("")
					.lastName("")
					.password("admin123")
					.iban("FR001")
					.bank("Banque1")
					.build();
			
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(anyInt()))
				.thenReturn(Optional.of(user1))
				.thenReturn(Optional.of(user1))
				.thenReturn(Optional.of(user1));
			
			when(userRepository.findByEmail(Treasurer.EMAIL))
				.thenReturn(Optional.of(user3));
			
			when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(userTransactionTest)
				.thenReturn(sendCommissionTransactionTest)
				.thenReturn(receiveCommissionTransactionTest);
			
			transactionService.sendMoneyToBank(sendMoneyDTO);
			
			verify(transactionRepository,times(3)).save(transactionCaptor.capture());
			
			assertThat(transactionCaptor.getAllValues())
				.extracting("buddyId","amount")
				.contains(tuple(1,"-100.00"),tuple(3,"-0.50"),tuple(1,"0.50"));
			
			verify(transactionRepository,times(3)).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository,times(3)).findById(anyInt());
			verify(userRepository).findByEmail(Treasurer.EMAIL);
			verify(transactionRepository,times(3)).save(any(Transaction.class));
		}
	}	
	
	
	@Nested
	class GetBalance{
		@Test
		public void should_retun_120() {
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(transactionRepository.findAllByUserOrderByIdDesc(user1))
				.thenReturn(Arrays.asList(transaction3,transaction2,transaction1));
			assertThat(transactionService.getBalance(1))
				.isEqualTo("120");
			verify(userRepository).findById(1);
			verify(transactionRepository).findAllByUserOrderByIdDesc(user1);
		}
		@Test
		public void user_doesnt_exist() {
			when(userRepository.findById(1))
				.thenReturn(null);
			assertThat(transactionService.getBalance(1))
				.isEqualTo("0");
		verify(userRepository).findById(1);
		}
	}
	
	@Nested
	class GetActivity{
		@Test
		public void success() {
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(transactionRepository.findAllByUserOrderByIdDesc(user1))
				.thenReturn(Arrays.asList(transaction2,transaction1));
			assertThat(transactionService.getActivity(1))
				.contains(transaction1)
				.contains(transaction2);
			verify(userRepository).findById(1);
			verify(transactionRepository).findAllByUserOrderByIdDesc(user1);
		}
	}
	
	@Nested
	class GetActivityById{
		@Test
		public void success() {
			activityDTO1 = ActivityDTO.builder()
				.arrow(true)
				.buddyName("Prénom1 Nom1")
				.date(LocalDate.now().toString())
				.description("Initial deposit")
				.amount("100")
				.build();
			activityDTO2 = ActivityDTO.builder()
				.arrow(true)
				.buddyName("Prénom2 Nom2")
				.date(LocalDate.now().toString())
				.description("Gift")
				.amount("55")
				.build();
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(userRepository.findById(2))
				.thenReturn(Optional.of(user2));
			when(transactionRepository.findAllByUserOrderByIdDesc(user1))
				.thenReturn(Arrays.asList(transaction2,transaction1));
			assertThat(transactionService.getActivityById(1))
				.contains(activityDTO1)
				.contains(activityDTO2);
			verify(userRepository,times(2)).findById(1);
			verify(userRepository).findById(2);
			verify(transactionRepository).findAllByUserOrderByIdDesc(user1);
		}
	}
	
	//TODO : getTransactionsById
	@Nested
	class GetTransactionsById{
		@Test
		public void success() {
			activityDTO1 = ActivityDTO.builder()
				.arrow(true)
				.buddyName("Prénom1 Nom1")
				.date(LocalDate.now().toString())
				.description("Initial deposit")
				.amount("100")
				.build();
			activityDTO2 = ActivityDTO.builder()
				.arrow(true)
				.buddyName("Prénom2 Nom2")
				.date(LocalDate.now().toString())
				.description("Gift")
				.amount("55")
				.build();
			activityDTO3 = ActivityDTO.builder()
				.arrow(false)
				.buddyName("Prénom2 Nom2")
				.date(LocalDate.now().toString())
				.description("Repayment")
				.amount("35")
				.build();
			
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(userRepository.findById(2))
				.thenReturn(Optional.of(user2));
			when(transactionRepository.findAllByUserOrderByIdDesc(user1))
				.thenReturn(Arrays.asList(transaction3,transaction2,transaction1));
			System.out.println(transactionService.getTransactionsById(1));
			assertThat(transactionService.getTransactionsById(1))
				.doesNotContain(activityDTO1)
				.doesNotContain(activityDTO2)
				.contains(activityDTO3);
			verify(userRepository,times(8)).findById(1);
			verify(userRepository,times(4)).findById(2);
			verify(transactionRepository,times(2)).findAllByUserOrderByIdDesc(user1);
		}
	}
	
	@Nested
	class CreateSendingTransactionTest{
		
		@Test
		public void simpleSending() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(2)
				.description("Gift")
				.amount(100)
				.build();
			transaction = Transaction.builder()
				.transactionNumber(4)
				.buddyId(2)
				.description("Gift")
				.amount("-100.00")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			assertThat(transactionService.createSendingTransaction(sendMoneyDTO,BigDecimal.ONE))
				.isEqualTo(transaction);
			verify(transactionRepository).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository).findById(1);
		}
		
		@Test
		public void commissionSending() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(2)
				.description("Gift")
				.amount(100)
				.build();
			transaction = Transaction.builder()
				.transactionNumber(4)
				.buddyId(2)
				.description("Gift")
				.amount("-0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user1)
				.build();
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			assertThat(transactionService.createSendingTransaction(sendMoneyDTO,Commission.AMOUNT))
				.isEqualTo(transaction);
			verify(transactionRepository).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository).findById(1);
		}
	}
	
	@Nested
	class CreateReceivingingTransactionTest{
		
		@Test
		public void simpleReceiving() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(2)
				.description("Gift")
				.amount(100)
				.build();
			transaction = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("Gift")
				.amount("100.00")
				.date(LocalDate.now())
				.done(false)
				.user(user2)
				.build();
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(2))
				.thenReturn(Optional.of(user2));
			assertThat(transactionService.createReceivingTransaction(sendMoneyDTO,BigDecimal.ONE))
				.isEqualTo(transaction);
			verify(transactionRepository).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository).findById(2);
		}
		
		@Test
		public void commissionReceiving() {
			sendMoneyDTO = SendMoneyDTO.builder()
				.userId(1)
				.buddyId(2)
				.description("Gift")
				.amount(100)
				.build();
			transaction = Transaction.builder()
				.transactionNumber(4)
				.buddyId(1)
				.description("Gift")
				.amount("0.50")
				.date(LocalDate.now())
				.done(false)
				.user(user2)
				.build();
			when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
				.thenReturn(transaction3);
			when(userRepository.findById(2))
				.thenReturn(Optional.of(user2));
			assertThat(transactionService.createReceivingTransaction(sendMoneyDTO,Commission.AMOUNT))
				.isEqualTo(transaction);
			verify(transactionRepository).findFirstByOrderByTransactionNumberDesc();
			verify(userRepository).findById(2);
		}
	}
	
	
	@Test
	public void addTransaction_success() {
		transaction1ToAdd = Transaction.builder()
			.transactionNumber(1)
			.buddyId(1)
			.description("Initial deposit")
			.amount("100")
			.date(LocalDate.now())
			.done(false)
			.build();
		when(transactionRepository.save(any(Transaction.class)))
			.thenReturn(transaction1ToAdd);
		transactionService.addTransaction(transaction1ToAdd);
		verify(transactionRepository).save(transactionCaptor.capture());
		assertThat(transactionCaptor.getValue()).isEqualTo(transaction1ToAdd);
		
	}
	
	@Test
	public void getNextTransactionNumber_should_return_4() {
		when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
			.thenReturn(transaction3);
		assertThat(transactionService.getNextTransactionNumber()).isEqualTo(4);
		verify(transactionRepository).findFirstByOrderByTransactionNumberDesc();
	}
	
	@Nested
	class ProjectedBalanceTest{
		
		@Test
		public void positive_amount() {
			assertThat(transactionService.getProjectedBalance("200.00", "100.00"))
				.isEqualTo("299.50");
		}
		
		@Test
		public void negative_amount() {
			assertThat(transactionService.getProjectedBalance("200.00", "-100.00"))
				.isEqualTo("99.50");
		}
	}
	
	//convertToSendMoney
	

}
