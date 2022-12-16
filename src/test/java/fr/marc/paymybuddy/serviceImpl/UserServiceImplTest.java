package fr.marc.paymybuddy.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import fr.marc.paymybuddy.DTO.LoginDTO;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.IUserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
	
	private IUserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Captor
	ArgumentCaptor<User> userCaptor;
	@Captor
	ArgumentCaptor<Integer> idCaptor;
	
	@BeforeEach
	public void init() {
		userService = new UserServiceImpl(userRepository);
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
	}
	
	@Test
	public void getUsers_success() {
		when(userRepository.findAll())
			.thenReturn(Arrays.asList(user1,user2));
		assertThat(userService.getUsers())
			.contains(user1)
			.contains(user2);
		verify(userRepository).findAll();
	}
	
	@Nested
	class GetUserById {
		@Test
		public void success() {
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			assertThat(userService.getUserById(1)
				.equals(Optional.of(user1)));
			verify(userRepository).findById(1);
		}
		@Test
		public void no_user() {
			when(userRepository.findById(15))
				.thenReturn(null);
			assertThat(userService.getUserById(15))
				.isNull();
			verify(userRepository).findById(15);
		}
	}
	
	@Nested
	class GetUserByEmail {
		@Test
		public void success() {
			when(userRepository.findByEmail("user1@mail.fr"))
				.thenReturn(Optional.of(user1));
			assertThat(userService.getUserByEmail("user1@mail.fr")
				.equals(Optional.of(user1)));
			verify(userRepository).findByEmail("user1@mail.fr");
		}
		@Test
		public void no_user() {
			when(userRepository.findByEmail("noemail@mail.fr"))
				.thenReturn(null);
			assertThat(userService.getUserByEmail("noemail@mail.fr"))
				.isNull();
			verify(userRepository).findByEmail("noemail@mail.fr");
		}
	}
	
	@Nested
	class VerifyLogin {
		@Test
		public void success() {
			LoginDTO loginDTO = new LoginDTO();
			loginDTO.setEmail("user1@mail.fr");
			loginDTO.setPassword("111");
			when(userRepository.findByEmail("user1@mail.fr"))
				.thenReturn(Optional.of(user1));
			assertThat(userService.verifyLogin(loginDTO))
				.isEqualTo(1);
			verify(userRepository).findByEmail("user1@mail.fr");
		}
		@Test
		public void password_doesnt_match() {
			LoginDTO loginDTO = new LoginDTO();
			loginDTO.setEmail("user1@mail.fr");
			loginDTO.setPassword("222");
			when(userRepository.findByEmail("user1@mail.fr"))
				.thenReturn(Optional.of(user1));
			assertThat(userService.verifyLogin(loginDTO))
				.isEqualTo(-1);
			verify(userRepository).findByEmail("user1@mail.fr");
		}
		@Test
		public void email_unknown() {
			LoginDTO loginDTO = new LoginDTO();
			loginDTO.setEmail("unknown@mail.fr");
			loginDTO.setPassword("111");
			when(userRepository.findByEmail("user1@mail.fr"))
				.thenReturn(Optional.of(user1));
			assertThat(userService.verifyLogin(loginDTO))
				.isEqualTo(-2);
			verify(userRepository).findByEmail("unknown@mail.fr");
		}
	}
	
	@Test
	public void addUser_success() {
		user3 = User.builder()
				.email("user3@mail.fr")
				.firstName("Prénom3")
				.lastName("Nom3")
				.password("333")
				.iban("FR003")
				.bank("Banque3")
				.build();
		when(userRepository.save(any(User.class)))
			.thenReturn(user3);
		userService.addUser(user3);
		verify(userRepository).save(userCaptor.capture());
		assertThat(userCaptor.getValue()).isEqualTo(user3);
		verify(userRepository).save(user3);
	}
	
	@Test
	public void deleteUser_success() {
		userService.deleteUser(1);
		verify(userRepository).deleteById(idCaptor.capture());
		assertThat(idCaptor.getValue()).isEqualTo(1);
		verify(userRepository).deleteById(1);
	}
	
	@Test
	public void get_user1_complete_name() {
		when(userRepository.findById(1))
			.thenReturn(Optional.of(user1));
		assertThat(userService.getCompleteName(1))
			.isEqualTo("Prénom1 Nom1");
		verify(userRepository).findById(1);
	}

}
