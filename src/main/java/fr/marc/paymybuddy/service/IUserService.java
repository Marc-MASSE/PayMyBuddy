package fr.marc.paymybuddy.service;

import java.util.List;
import java.util.Optional;

import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;

public interface IUserService {
	
	Iterable<User> getUsers();
	
	Optional<User> getUserById(Integer id);
	
	Optional<User> getUserByEmail(String email);
	
	int getBalance(Integer id);
	
	List<Transaction> getActivity(Integer id);
	
	List<Connection> getBuddies(Integer user_id);
	
	User addUser(User user);
	
	void deleteUser(Integer id);
}
