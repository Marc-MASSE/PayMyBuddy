package fr.marc.paymybuddy.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.DTO.LoginDTO;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.repository.ConnectionRepository;
import fr.marc.paymybuddy.repository.TransactionRepository;
import fr.marc.paymybuddy.repository.UserRepository;
import fr.marc.paymybuddy.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	static Logger log = LogManager.getLogger(UserServiceImpl.class.getName());
	
	private UserRepository userRepository;
	
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	@Override
	public Iterable<User> getUsers(){
		return userRepository.findAll();
	}
	
	@Override
	public Optional<User> getUserById(Integer id) {
		return userRepository.findById(id);
	}
	
	@Override
	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	
	/*
	 * If email unknown return -2.
	 * If password doesn't match return -1.
	 * If email and password matched return the user id.
	 */
	@Override
	public int verifyLogin(LoginDTO loginDTO) {
		int user_id = -1;
		try {
			User user = getUserByEmail(loginDTO.getEmail()).get();
			if (user.getPassword().equals(loginDTO.getPassword())) {
				user_id = user.getId();
			}
		} catch (Exception e) {
			user_id = -2;
		}
		return user_id;
	}
	
	@Override
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}

}
