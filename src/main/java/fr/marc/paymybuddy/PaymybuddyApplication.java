package fr.marc.paymybuddy;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.service.IUserService;
import fr.marc.paymybuddy.serviceImpl.UserServiceImpl;

@SpringBootApplication
public class PaymybuddyApplication implements CommandLineRunner {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private TransactionRepository transactionRepository;

	public static void main(String[] args) {
		SpringApplication.run(PaymybuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {	
		
		
		Iterable<User> users = userService.getUsers();
		users.forEach(u -> System.out.println(u.getFirstName()+" "+u.getLastName()));
		
		System.out.println("-------------------------------------------");
		
		Optional<User> user = userService.getUserById(1);
		User userId1 = user.get();
		System.out.println(userId1.getFirstName()+" "+userId1.getLastName());
		
		System.out.println("-------------------------------------------");
		
		List<Transaction> resultList = transactionRepository.findAllByTransactionNumber(6);
		resultList.forEach(r -> System.out.println(r.getTransactionNumber()+" "+r.getDescription()+" "+r.getAmount()));
		
		System.out.println("-------------------------------------------");
		
		Transaction lastTransaction = transactionRepository.findFirstByOrderByTransactionNumberDesc();
		System.out.println("Transaction_number le plus élevé = "+lastTransaction.getTransactionNumber());
		
		System.out.println("-------------------------------------------");
		
		System.out.println("-------------------------------------------");
		
	}
	
	
}
