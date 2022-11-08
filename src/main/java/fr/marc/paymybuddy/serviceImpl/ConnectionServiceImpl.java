package fr.marc.paymybuddy.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.DTO.BuddyDTO;
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
	
	@Autowired
	public ConnectionServiceImpl(UserRepository userRepository,IUserService userService,ConnectionRepository connectionRepository) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.connectionRepository = connectionRepository;
	}
	
	
	public List<BuddyDTO> getBuddyList(Integer userId) {
		User user = userRepository.findById(userId).get();
		log.info("getBuddyList method for - id = "+userId);
		List<Connection> buddies = connectionRepository.findAllByUser(user);
		List<BuddyDTO> buddyList = new ArrayList<>();
		buddies.forEach(b -> {
			BuddyDTO buddy = new BuddyDTO();
			buddy.setId(b.getBuddyId());
			buddy.setBuddyName(userService.getUserById(b.getBuddyId()).get().getFirstName()+" "+userService.getUserById(b.getBuddyId()).get().getLastName());
			buddy.setEmail(userService.getUserById(b.getBuddyId()).get().getEmail());
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
		connection.setUser(userService.getUserById(UserId).get());
		connection.setBuddyId(userService.getUserByEmail(email).get().getId());
		
		return connectionRepository.save(connection);
	}
	
	@Override
	public void deleteConnectionById(Integer id) {
		connectionRepository.deleteById(id);
	}

}
