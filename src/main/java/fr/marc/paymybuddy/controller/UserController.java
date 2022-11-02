package fr.marc.paymybuddy.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.LoginDTO;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.Transaction;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.IUserService;
import fr.marc.paymybuddy.serviceImpl.UserServiceImpl;

/*
 * Controller used for end point /user
 * For updating user's data list
 */

//@RestController
@Controller
public class UserController {

	static Logger log = LogManager.getLogger(UserController.class.getName());

	@Autowired
	private IUserService userService;

	@ResponseBody
	@GetMapping("/users")
	public Iterable<User> getUsers() {
		log.info("GET request - endpoint /users - return the entire list of users");
		return userService.getUsers();
	}
	
	@ResponseBody
    @GetMapping("/user")
    public Optional<User> getUserById(@RequestParam int id) {
		log.info("GET request - endpoint /user - id = "+id);
        return userService.getUserById(id);
    }
	
	/*
	 * Page "Profile"
	*/
    @GetMapping("/profile")
    public String displayProfileById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /profile - id = "+id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
        return "profile";
    }
	
    /*
	@ResponseBody
    @GetMapping("/login")
    public Optional<User> getUserByEmail(@RequestParam String email) {
		log.info("GET request - endpoint /login - email = "+email);
        return userService.getUserByEmail(email);
    }
	*/
	
    /*
	@PostMapping("/login")
	public ModelAndView getLogin(Model model,@ModelAttribute LoginDTO loginDTO) {
		User user = userService.getUserByEmail(loginDTO.getEmail()).get();
		log.debug("Login - firstName = "+user.getFirstName()+" lastName = "+user.getLastName());
		if (user.getPassword().equals(loginDTO.getPassword())) {
			model.addAttribute("user",user);
			return new ModelAndView("redirect:/user");
		}else {
			return new ModelAndView("redirect:/login");
		}
	}
	*/
	
	/*
	 * Page "Login"
	*/
	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("loginDTO", new LoginDTO());
		return "login";
	}
	
	/*
	 * Page "Login", verify if email and password match
	 * If email unknown return 0.
	 * If password doesn't match return -1.
	 * If email and password matched return the user id.
	 */
    @PostMapping(value = "/loginRequest")
    public String verifyLogin(@ModelAttribute("login") LoginDTO loginDTO) {
		log.info("POST request - endpoint /loginRequest - body = "+loginDTO);
		Integer user_id = userService.verifyLogin(loginDTO);
		log.debug("user_id = "+user_id);
		switch(user_id) {
		  case 0:
			  return "redirect:/login";
		  case -1:
			  return "redirect:/login";
		  default:
			  return "redirect:/home?id="+user_id.toString();
		}
    }
	
	
	@ResponseBody
    @GetMapping("/balance")
    public int getBalanceById(@RequestParam int id) {
		log.info("GET request - endpoint /balance - id = "+id);
        return userService.getBalance(id);
    }
    
	@ResponseBody
    @GetMapping("/activity")
    public List<ActivityDTO> getActivityById(@RequestParam int id) {
		log.info("GET request - endpoint /activity - id = "+id);
        return userService.getActivityById(id);
    }
	
	/*
	 * Page "Home"
	*/
    @GetMapping("/home")
    public String displayHomePageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /home - id = "+id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
		int balance = userService.getBalance(id);
		model.addAttribute("balance",balance);
		List<ActivityDTO> activities = userService.getActivityById(id);
		model.addAttribute("activities",activities);
        return "home";
    }
	
    
	@ResponseBody
    @GetMapping("/buddies")
    public List<Connection> getBuddiesById(@RequestParam int user_id) {
		log.info("GET request - endpoint /buddies - user_id = "+user_id);
        return userService.getBuddies(user_id);
    }
    
	@ResponseBody
    @PostMapping(value = "/user")
    public User addUser(@RequestBody User user) {
		log.info("POST request - endpoint /user - body = "+user);
    	return userService.addUser(user);
    }
    
	@ResponseBody
    @PutMapping(value = "/user")
    public User updateUser(@RequestBody User user) {
		log.info("PUT request - endpoint /user - body = "+user);
    	return userService.addUser(user);
    }
    
	@ResponseBody
    @DeleteMapping("/user")
    public void deletePersonByParam(@RequestParam int id) {
		log.info("DELETE request - endpoint /user - id = "+id);
        userService.deleteUser(id);
    }
    
    @GetMapping("/")
    public String login() {
        return "login";
    }
    
}
