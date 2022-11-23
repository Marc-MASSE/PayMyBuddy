package fr.marc.paymybuddy.service;

import java.util.Optional;

import fr.marc.paymybuddy.DTO.LoginDTO;
import fr.marc.paymybuddy.model.User;

public interface IUserService {
	
	Iterable<User> getUsers();
	
	Optional<User> getUserById(Integer id);
	
	Optional<User> getUserByEmail(String email);
	
	int verifyLogin(LoginDTO loginDTO);
	
	User addUser(User user);
	
	void deleteUser(Integer id);

	String getCompleteName(Integer id);
}
