package fr.marc.paymybuddy;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.UserService;

@SpringBootApplication
public class PaymybuddyApplication implements CommandLineRunner {
	
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(PaymybuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {	
		Iterable<User> users = userService.getUsers();
		users.forEach(u -> System.out.println(u.getFirsName()+" "+u.getLastName()));
		
		System.out.println("-------------------------------------------");
		
		Optional<User> user = userService.getUserById(1);
		User userId1 = user.get();
		System.out.println(userId1.getFirsName()+" "+userId1.getLastName());
		
	}
	
	
}
