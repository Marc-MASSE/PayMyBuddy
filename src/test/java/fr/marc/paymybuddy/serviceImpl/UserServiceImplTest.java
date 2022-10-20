package fr.marc.paymybuddy.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.IUserService;
import fr.marc.paymybuddy.serviceImpl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
	
	private IUserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private TransactionRepository transactionRepository;
	
	@Mock
	private ConnectionRepository connectionRepository;
	
	//private User user1 = new User(1,"acall@mail.fr","Arthur","Call","Excalibur",false,"FR00123456789","Camelot");
	private User user1 = User.builder()
			.id(1)
			.email("user1@mail.fr")
			.firstName("Prénom1")
			.lastName("Nom1")
			.password("111")
			.rememberMe(false)
			.iban("FR001")
			.bank("Banque1")
			.build();
	private User user2 = User.builder()
			.id(2)
			.email("user2@mail.fr")
			.firstName("Prénom2")
			.lastName("Nom2")
			.password("222")
			.rememberMe(false)
			.iban("FR002")
			.bank("Banque2")
			.build();
	
	
	@BeforeEach
	public void init() {
		userService = new UserServiceImpl(userRepository, transactionRepository, connectionRepository);
	}
	
	@Test
	public void getUsers_success() {
		when(userRepository.findAll()).thenReturn(Arrays.asList(user1,user2));
		assertThat(userService.getUsers()).contains(user1).contains(user2);
		verify(userRepository).findAll();
	}
	
	@Nested
	class GetUserById {
		@Test
		public void success() {
			when(userRepository.findById(1)).thenReturn(Optional.of(user1));
			assertThat(userService.getUserById(1).equals(Optional.of(user1)));
			verify(userRepository).findById(1);
		}
		@Test
		public void no_user() {
			when(userRepository.findById(15)).thenReturn(null);
			assertThat(userService.getUserById(15)).isNull();
			verify(userRepository).findById(15);
		}
	}
	
	@Nested
	class GetUserByEmail {
		@Test
		public void success() {
			when(userRepository.findByEmail("user1@mail.fr")).thenReturn(Optional.of(user1));
			assertThat(userService.getUserByEmail("user1@mail.fr").equals(Optional.of(user1)));
			verify(userRepository).findByEmail("user1@mail.fr");
		}
		@Test
		public void no_user() {
			when(userRepository.findByEmail("noemail@mail.fr")).thenReturn(null);
			assertThat(userService.getUserByEmail("noemail@mail.fr")).isNull();
			verify(userRepository).findByEmail("noemail@mail.fr");
		}
	}

}
