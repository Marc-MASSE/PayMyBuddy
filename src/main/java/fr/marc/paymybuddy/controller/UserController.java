package fr.marc.paymybuddy.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.BankOrderDTO;
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
		log.info("GET request - endpoint /user - id = {}",id);
        return userService.getUserById(id);
    }
	
	/*
	 * When open to "/" page, redirect to "Login" page
	*/
    @GetMapping("/")
    public String login(Model model) {
		log.info("Redirect to Login page");
		String message ="";
		model.addAttribute(message);
        return "redirect:/login?message="+message;
    }
    
	/*
	 * Page "Admin"
	*/
    @GetMapping("/admin")
    public String admin() {
		log.info("Redirect to Admin page");
        return "admin";
    }
	
	/*
	 * Page "Profile"
	*/
    @GetMapping("/profile")
    public String displayProfileById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /profile - id = {}",id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
        return "profile";
    }
	
	// TODO : Fix login page
	/*
	 * Page "Login"
	*
	@GetMapping("/login")
	public String loginForm(Model model, @RequestParam String message) {
		model.addAttribute("loginDTO", new LoginDTO());
		model.addAttribute("message", message);
		
		//log.info("GET request - endpoint /login - bindingResult = {}",result);
		SecurityContext ctx = SecurityContextHolder.getContext();
		log.info("GET request - endpoint /login - SecurityContext = {}",ctx);
		
		return "login";
	}
	*/
	@GetMapping("/login")
	public String loginForm() {
		
		SecurityContext ctx = SecurityContextHolder.getContext();
		log.info("GET request - endpoint /login - SecurityContext = {}",ctx);
		
		return "login";
	}
	
	/*
	 * Page "Login", verify if email and password match
	 * If user_id = -2 => message email unknown
	 * If user_id = -1 => message password doesn't match
	 * If email and password matched redirect to Home page.
	 */
    @PostMapping(value = "/loginRequest")
    public String verifyLogin(@ModelAttribute("login") LoginDTO loginDTO) {
		log.info("POST request - endpoint /loginRequest - body = {}",loginDTO);
		Integer userId = userService.verifyLogin(loginDTO);
		log.info("user_id = {}",userId);
		String message;
		switch(userId) {
		  case -2:
			  message = "Your email isn't registed";
			  log.info("Message = {}",message);
			  return "redirect:/login?message="+message;
		  case -1:
			  message = "The password doesn't match with your email";
			  log.info("Message = {}",message);
			  return "redirect:/login?message="+message;
		  default:
			  return "redirect:/home?id="+userId.toString();
		}
    }
	
	
	@ResponseBody
    @GetMapping("/balance")
    public String getBalanceById(@RequestParam int id) {
		log.info("GET request - endpoint /balance - id = {}",id);
        return transactionService.getBalance(id);
    }
    
	@ResponseBody
    @GetMapping("/activity")
    public List<ActivityDTO> getActivityById(@RequestParam int id) {
		log.info("GET request - endpoint /activity - id = {}",id);
        return transactionService.getActivityById(id);
    }
	
	/*
	 * Page "Home"
	*/
    @GetMapping("/home")
    public String displayHomePageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /home - id = {}",id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
		String balance = transactionService.getBalance(id);
		model.addAttribute("balance",balance);
		List<ActivityDTO> activities = transactionService.getActivityById(id);
		model.addAttribute("activities",activities);
		BankOrderDTO bankOrderDTO = new BankOrderDTO();
		model.addAttribute("bankOrderDTO",bankOrderDTO);
		
		SecurityContext ctx = SecurityContextHolder.getContext();
		log.info("GET request - endpoint /home - SecurityContext = {}",ctx);
		
        return "home";
    }
	
	/*
	 * Page "Contact"
	*/
    @GetMapping("/contact")
    public String displayContactPageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /contact - id = {}",id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
        return "contact";
    }
    
	/*
	 * Page "Modify"
	*/
    @GetMapping("/modify")
    public String displayModifyPageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /modify - id = {}",id);
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
        return "modify";
    }
    
	@ResponseBody
    @PostMapping(value = "/user")
    public User addUser(@RequestBody User user) {
		log.info("POST request - endpoint /user - body = {}",user);
    	return userService.addUser(user);
    }
	
	/*
	 * Page "Modify" to update a user
	*/
    @PostMapping(value = "/saveUser")
    public String saveUser(@ModelAttribute("modify") User user,@RequestParam Integer id) {
    	user.setId(id);
		log.info("POST request - endpoint /saveUser - body = {}",user);
		userService.addUser(user);
    	return "redirect:/profile?id="+id.toString();
    }
    
    /*
	@ResponseBody
    @PutMapping(value = "/user")
    public User updateUser(@RequestBody User user) {
		log.info("PUT request - endpoint /user - body = {}",user);
    	return userService.addUser(user);
    }
	*/
    
	@ResponseBody
    @DeleteMapping("/user")
    public void deletePersonByParam(@RequestParam int id) {
		log.info("DELETE request - endpoint /user - id = {}",id);
        userService.deleteUser(id);
    }
    
}
