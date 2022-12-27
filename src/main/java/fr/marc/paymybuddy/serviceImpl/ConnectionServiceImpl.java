package fr.marc.paymybuddy.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.constants.Treasurer;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.IConnectionService;
import fr.marc.paymybuddy.service.IUserService;

@Service
public class ConnectionServiceImpl implements IConnectionService {
	
	static Logger log = LogManager.getLogger(ConnectionServiceImpl.class.getName());
	
	private UserRepository userRepository;
	
	private IUserService userService;
	
	private ConnectionRepository connectionRepository;
	
	private String message;
	
	@Autowired
	public ConnectionServiceImpl(UserRepository userRepository,ConnectionRepository connectionRepository) {
		this.userRepository = userRepository;
		this.connectionRepository = connectionRepository;
	}
	
	
	@Override
	public List<BuddyDTO> getBuddyList(Integer userId) {
		User user = userRepository.findById(userId).get();
		log.info("getBuddyList method for - id = {}",userId);
		List<Connection> buddies = connectionRepository.findAllByUser(user);
		List<BuddyDTO> buddyList = new ArrayList<>();
		buddies.forEach(b -> {
			BuddyDTO buddy = new BuddyDTO();
			buddy.setId(b.getBuddyId());
			buddy.setBuddyName(userRepository.findById(b.getBuddyId()).get().getFirstName()+" "+userRepository.findById(b.getBuddyId()).get().getLastName());
			buddy.setEmail(userRepository.findById(b.getBuddyId()).get().getEmail());
			buddyList.add(buddy);
		});
		return buddyList;
		}

	
	@Override
	public Connection getConnectionByUserIdAndBuddyId(Integer userId, Integer buddyId) {
		return connectionRepository.findByUserIdAndBuddyId(userId,buddyId);
	}

	@Override
	public Connection addConnection(Connection connection) {
		return connectionRepository.save(connection);
	}
	
	@Override
	public Connection addANewBuddy(String email, Integer UserId) {
		Connection connection = new Connection();
		connection.setUser(userRepository.findById(UserId).get());
		connection.setBuddyId(userRepository.findByEmail(email).get().getId());
		
		return connectionRepository.save(connection);
	}
	
	@Override
	public void deleteConnectionById(Integer id) {
		connectionRepository.deleteById(id);
	}

	@Override
	public String newBuddyAvailabilityMessage(String buddyEmail, String userEmail) {
		
		message = "";
		if (userRepository.findByEmail(buddyEmail).isEmpty()) {
			message = "This buddy isn't registered.";
		}
		if (buddyEmail.equals(Treasurer.EMAIL)) {
			message = "This email isn't available.";
		}
		List<BuddyDTO> buddyList = getBuddyList(userRepository.findByEmail(userEmail).get().getId());
		buddyList.forEach(b -> {
			if (b.getEmail().equals(buddyEmail)) {
				message = b.getBuddyName()+" is already your buddy.";
			}
		});
		return message;
	}

}
