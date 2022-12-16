package fr.marc.paymybuddy.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.IConnectionService;

@ExtendWith(MockitoExtension.class)
public class ConnectionServiceImplTest {
	
	private IConnectionService connectionService;
	
	@Mock
	private ConnectionRepository connectionRepository;
	
	@Mock
	private UserRepository userRepository;
	
	private User user1;
	private User user2;
	private User user3;
	private Connection connection1To2;
	private Connection connection1To3;
	private Connection connectionToAdd;
	private BuddyDTO buddy2;
	private BuddyDTO buddy3;
	
	@Captor
	ArgumentCaptor<Connection> connectionCaptor;
	@Captor
	ArgumentCaptor<Integer> idCaptor;
	
	@BeforeEach
	public void init() {
		connectionService = new ConnectionServiceImpl(userRepository,connectionRepository);
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
		user3 = User.builder()
				.id(3)
				.email("user3@mail.fr")
				.firstName("Prénom3")
				.lastName("Nom3")
				.password("333")
				.iban("FR003")
				.bank("Banque3")
				.build();
		connection1To2 = Connection.builder()
				.id(1)
				.user(user1)
				.buddyId(2)
				.build();
		connection1To3 = Connection.builder()
				.id(1)
				.user(user1)
				.buddyId(3)
				.build();
	}
	
	@Nested
	class GetBuddyList {
		@Test
		public void success() {
			buddy2 = BuddyDTO.builder()
					.id(2)
					.buddyName("Prénom2 Nom2")
					.email("user2@mail.fr")
					.build();
			buddy3 = BuddyDTO.builder()
					.id(3)
					.buddyName("Prénom3 Nom3")
					.email("user3@mail.fr")
					.build();
			
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(userRepository.findById(2))
				.thenReturn(Optional.of(user2));
			when(userRepository.findById(3))
				.thenReturn(Optional.of(user3));
			when(connectionRepository.findAllByUser(user1))
				.thenReturn(Arrays.asList(connection1To2,connection1To3));
			assertThat(connectionService.getBuddyList(1))
				.contains(buddy2)
				.contains(buddy3);
			verify(connectionRepository).findAllByUser(user1);
			verify(userRepository).findById(1);
			verify(userRepository,times(3)).findById(2);
			verify(userRepository,times(3)).findById(3);
		}
		@Test
		public void no_connection() {
			when(userRepository.findById(1))
				.thenReturn(Optional.of(user1));
			when(connectionRepository.findAllByUser(user1))
				.thenReturn(List.of());
			assertThat(connectionService.getBuddyList(1))
				.isEmpty();
		}
	}
	
	@Nested
	class GetConnectionByUserIdAndBuddyId {
		@Test
		public void success() {
			when(connectionRepository.findByUserIdAndBuddyId(1,2))
				.thenReturn(connection1To2);
			assertThat(connectionService.getConnectionByUserIdAndBuddyId(1,2))
				.isEqualTo(connection1To2);
			verify(connectionRepository).findByUserIdAndBuddyId(1,2);
		}
		@Test
		public void no_answer() {
			when(connectionRepository.findByUserIdAndBuddyId(1,4))
				.thenReturn(null);
			assertThat(connectionService.getConnectionByUserIdAndBuddyId(1,4))
				.isNull();
			verify(connectionRepository).findByUserIdAndBuddyId(1,4);
		}
	}
	
	@Test
	public void addConnection_success() {
		connectionToAdd = Connection.builder()
				.buddyId(4)
				.user(user1)
				.build();
		when(connectionRepository.save(any(Connection.class)))
			.thenReturn(connectionToAdd);
		connectionService.addConnection(connectionToAdd);
		verify(connectionRepository).save(connectionCaptor.capture());
		assertThat(connectionCaptor.getValue()).isEqualTo(connectionToAdd);
		verify(connectionRepository).save(connectionToAdd);
	}
	
	@Test
	public void addANewBuddy_success() {
		connectionToAdd = Connection.builder()
				.buddyId(2)
				.user(user1)
				.build();
		when(userRepository.findById(1))
			.thenReturn(Optional.of(user1));
		when(userRepository.findByEmail("user2@mail.fr"))
			.thenReturn(Optional.of(user2));
		when(connectionRepository.save(connectionToAdd))
			.thenReturn(connectionToAdd);
		connectionService.addANewBuddy("user2@mail.fr",1);
		verify(connectionRepository).save(connectionCaptor.capture());
		assertThat(connectionCaptor.getValue()).isEqualTo(connectionToAdd);
		verify(connectionRepository).save(connectionToAdd);
	}
	
	@Test
	public void deleteconnection_success() {
		connectionService.deleteConnectionById(1);
		verify(connectionRepository).deleteById(idCaptor.capture());
		assertThat(idCaptor.getValue()).isEqualTo(1);
		verify(connectionRepository).deleteById(1);
	}
	
}
