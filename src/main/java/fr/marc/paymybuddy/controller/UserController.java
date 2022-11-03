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

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.LoginDTO;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.ITransactionService;
import fr.marc.paymybuddy.service.IUserService;

/*
 * Controller used for end point /user
 * For updating user's data list
 */

@Controller
public class UserController {

	static Logger log = LogManager.getLogger(UserController.class.getName());

	@Autowired
	private IUserService userService;
	
	@Autowired
	private ITransactionService transactionService;

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
	 * Page "Login"
	*/
	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("loginDTO", new LoginDTO());
		return "login";
	}
	
	/*
	 * Page "Login", verify if email and password match
	 * If user_id = 0 => message email unknown
	 * If user_id = -1 => message password doesn't match
	 * If email and password matched redirect to Home page.
	 */
    @PostMapping(value = "/loginRequest")
    public String verifyLogin(@ModelAttribute("login") LoginDTO loginDTO, Model model) {
		log.info("POST request - endpoint /loginRequest - body = "+loginDTO);
		Integer userId = userService.verifyLogin(loginDTO);
		log.debug("user_id = "+userId);
		String message;
		switch(userId) {
		  case 0:
			  message = "Your email isn't registed";
			  model.addAttribute(message);
			  log.debug("Message = "+message);
			  return "redirect:/login";
		  case -1:
			  message = "The assword doesn't match with your email";
			  model.addAttribute(message);
			  log.debug("Message = "+message);
			  return "redirect:/login";
		  default:
			  return "redirect:/home?id="+userId.toString();
		}
    }
	
	
	@ResponseBody
    @GetMapping("/balance")
    public int getBalanceById(@RequestParam int id) {
		log.info("GET request - endpoint /balance - id = "+id);
        return transactionService.getBalance(id);
    }
    
	@ResponseBody
    @GetMapping("/activity")
    public List<ActivityDTO> getActivityById(@RequestParam int id) {
		log.info("GET request - endpoint /activity - id = "+id);
        return transactionService.getActivityById(id);
    }
	
	/*
	 * Page "Home"
	*/
    @GetMapping("/home")
    public String displayHomePageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /home - id = "+id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
		int balance = transactionService.getBalance(id);
		model.addAttribute("balance",balance);
		List<ActivityDTO> activities = transactionService.getActivityById(id);
		model.addAttribute("activities",activities);
        return "home";
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
    public String login(Model model) {
		log.info("Redirect to Login page");
		String message ="";
		model.addAttribute(message);
        return "redirect:/login";
    }
    
}
