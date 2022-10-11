package fr.marc.paymybuddy.controller;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
    @GetMapping("/balance")
    public int getBalanceById(@RequestParam int id) {
		log.info("GET request - endpoint /balance - id = "+id);
        return userService.getBalance(id);
    }
    
}
