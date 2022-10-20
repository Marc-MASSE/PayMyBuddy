package fr.marc.paymybuddy.serviceImpl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.UserRepository;

@Service
public class ConnectionServiceImpl {
	
	/*
	static Logger log = LogManager.getLogger(ConnectionService.class.getName());
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ConnectionRepository connectionRepository;
	
	public List<Connection> getBuddies(Integer userId) {
		User user = userRepository.findById(userId).get();
		log.error("getBuddies method for - user = "+user.getFirsName()+" "+user.getLastName());
		return connectionRepository.findAllByUser(user);
		}
	*/

}
