package fr.marc.paymybuddy.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.marc.paymybuddy.DTO.ActivityDTO;
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

	private User user1;
	private User user2;
	private Transaction transaction1;
	private Transaction transaction2;
	private Transaction transaction3;
	private Transaction transaction1ToAdd;
	private ActivityDTO activityDTO1;
	private ActivityDTO activityDTO2;
	private ActivityDTO activityDTO3;
	
	@BeforeEach
	public void init() {
		transactionService = new TransactionServiceImpl(transactionRepository,userRepository,userService);
		user1 = User.builder()
				.id(1)
				.email("user1@mail.fr")
				.firstName("Prénom1")
				.lastName("Nom1")
				.password("111")
				.rememberMe(false)
				.iban("FR001")
				.bank("Banque1")
				.build();
		user2 = User.builder()
				.id(2)
				.email("user2@mail.fr")
				.firstName("Prénom2")
				.lastName("Nom2")
				.password("222")
				.rememberMe(false)
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
			assertThat(transactionService.getTransactionById(1)
				.equals(Optional.of(transaction1)));
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
	
	//TODO : sendMoneyToBuddy
	
	//TODO : receiveMoneyFromBank
	
	@Nested
	class GetBalance{
		@Test
		public void should_retun_120() {
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(transactionRepository.findAllByUserOrderByIdDesc(user1))
				.thenReturn(Arrays.asList(transaction3,transaction2,transaction1));
			assertThat(transactionService.getBalance(1))
				.isEqualTo(120);
			verify(userRepository).findById(1);
			verify(transactionRepository).findAllByUserOrderByIdDesc(user1);
		}
		@Test
		public void user_doesnt_exist() {
			when(userRepository.findById(1))
				.thenReturn(null);
			assertThat(transactionService.getBalance(1))
				.isEqualTo(0);
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
				.buddyName("Prénom3 Nom3")
				.date(LocalDate.now().toString())
				.description("Repayment")
				.amount("-35")
				.build();
			/*
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(userRepository.findById(2))
				.thenReturn(Optional.of(user2));
			when(transactionRepository.findAllByUserOrderByIdDesc(user1))
				.thenReturn(Arrays.asList(transaction3,transaction2,transaction1));
			
			assertThat(transactionService.getTransactionsById(1))
				.doesNotContain(activityDTO1)
				.doesNotContain(activityDTO2)
				.contains(activityDTO3);
			verify(userRepository,times(2)).findById(1);
			verify(userRepository).findById(2);
			verify(transactionRepository).findAllByUserOrderByIdDesc(user1);
			*/
		}
	}
	
	//TODO : addTransaction
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
		assertThat(transactionService.getTransactions())
			.isEmpty();
		System.out.println(transactionService.getTransactions().toString());
		transactionService.addTransaction(transaction1ToAdd);
		System.out.println(transactionService.getTransactions().toString());
		assertThat(transactionService.getTransactions())
			.contains(transaction1);
	}
	
	@Test
	public void getNextTransactionNumber_should_return_4() {
		when(transactionRepository.findFirstByOrderByTransactionNumberDesc())
			.thenReturn(transaction3);
		assertThat(transactionService.getNextTransactionNumber()).isEqualTo(4);
	}

}
