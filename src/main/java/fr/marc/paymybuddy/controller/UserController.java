package fr.marc.paymybuddy.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.UserService;

/*
 * Controller used for end point /user
 * For updating user's data list
 */

@RestController
public class UserController {

	static Logger log = LogManager.getLogger(UserController.class.getName());

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public Iterable<User> getUsers() {
		log.info("GET request - endpoint /users - return the entire list of users");
		return userService.getUsers();
	}
	
    @GetMapping("/user")
    public Optional<User> getUserById(@RequestParam int id) {
		log.info("GET request - endpoint /user - id = "+id);
        return userService.getUserById(id);
    }
    
    @GetMapping("/login")
    public Optional<User> getUserByEmail(@RequestParam String email) {
		log.info("GET request - endpoint /login - email = "+email);
        return userService.getUserByEmail(email);
    }
	
    
    @GetMapping("/balance")
    public int getBalanceById(@RequestParam int id) {
		log.info("GET request - endpoint /balance - id = "+id);
        return userService.getBalance(id);
    }
    
    @GetMapping("/activity")
    public List<Transaction> getActivityById(@RequestParam int id) {
		log.info("GET request - endpoint /activity - id = "+id);
        return userService.getActivity(id);
    }
    
    @GetMapping("/buddies")
    public List<Connection> getBuddiesById(@RequestParam int userId) {
		log.info("GET request - endpoint /buddies - user_id = "+userId);
        return userService.getBuddies(userId);
    }
    
    @PostMapping(value = "/user")
    public User addUser(@RequestBody User user) {
		log.info("POST request - endpoint /user - body = "+user);
    	return userService.addUser(user);
    }
    
    @PutMapping(value = "/user")
    public User updateUser(@RequestBody User user) {
		log.info("PUT request - endpoint /user - body = "+user);
    	return userService.addUser(user);
    }
    
    @DeleteMapping("/user")
    public void deletePersonByParam(@RequestParam int id) {
		log.info("DELETE request - endpoint /user - id = "+id);
        userService.deleteUser(id);
    }
    
}
