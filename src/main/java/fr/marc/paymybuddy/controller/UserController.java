package fr.marc.paymybuddy.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.marc.paymybuddy.DTO.ActivityDTO;
import fr.marc.paymybuddy.DTO.BankOrderDTO;
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
    public String displayProfileById(Model model) {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		log.info("GET request - endpoint /profile - email = {}",connectedEmail);
		model.addAttribute("user",user);
        return "profile";
    }
	
	/*
	 * Page "Login"
	*/
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
	
	/*
	 * "Logout" redirect to "Login"
	*/
	@GetMapping("/logout")
	public String logout() {
		return "redirect:/login";
	}
	
	/*
	 * Calculate the user's balance to display on "Home" page
	*/
	@ResponseBody
    @GetMapping("/balance")
    public String getBalanceById() {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		log.info("GET request - endpoint /balance - email = {}",connectedEmail);
        return transactionService.getBalance(user.getId());
    }
    
	/*
	 * Collect the user's activity to display on "Home" page
	*/
	@ResponseBody
    @GetMapping("/activity")
    public List<ActivityDTO> getActivityById() {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		log.info("GET request - endpoint /activity - email = {}",connectedEmail);
        return transactionService.getActivityById(user.getId());
    }
	
	/*
	 * Page "Home"
	*/
	@GetMapping("/home")
	public String displayHomePageById(Model model, 
			@RequestParam (name="message", defaultValue="")String message, 
			@RequestParam(name="page", defaultValue="1") int page) {
    	
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		
		log.info("GET request - endpoint /home - connectedEmail = {}",connectedEmail);
		log.info("Role = {}",SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		
		model.addAttribute("user",user);
		
		model.addAttribute("message",message);
		
		String balance = transactionService.getBalance(user.getId());
		model.addAttribute("balance",balance);
		
		List<ActivityDTO> activities = transactionService.getActivityById(user.getId());
		int linesNumber = 3;
		int start = (page-1)*linesNumber;
		int end = Math.min(page*linesNumber,activities.size());
		model.addAttribute("activities",activities.subList(start,end));
		
		int pagesNumber = (int) (Math.ceil(((float)activities.size()/(float)linesNumber)));
		int[] pages = new int[pagesNumber];
		for(int i=0;i<pagesNumber;i++) pages[i]=i+1;
		model.addAttribute("pages",pages);
		
		model.addAttribute("currentPage",page);
		model.addAttribute("maxPage",pagesNumber);
		
		BankOrderDTO bankOrderDTO = new BankOrderDTO();
		model.addAttribute("bankOrderDTO",bankOrderDTO);
		
        return "home";
    }
	
	/*
	 * Page "Contact"
	*/
    @GetMapping("/contact")
    public String displayContactPageById() {
        return "contact";
    }
    
	/*
	 * Page "Modify"
	*/
    @GetMapping("/modify")
    public String displayModifyPageById(Model model) {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		log.info("GET request - endpoint /modify - email = {}",connectedEmail);
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
    public String saveUser(@ModelAttribute("modify") User user) {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User userConnected = userService.getUserByEmail(connectedEmail).get();
    	user.setId(userConnected.getId());
    	user.setEmail(connectedEmail);
    	user.setPassword(userConnected.getPassword());
    	user.setRole(userConnected.getRole());
		log.info("POST request - endpoint /saveUser - body = {}",user);
		userService.addUser(user);
    	return "redirect:/profile";
    }
    
	@ResponseBody
    @DeleteMapping("/user")
    public void deletePersonByParam(@RequestParam int id) {
		log.info("DELETE request - endpoint /user - id = {}",id);
        userService.deleteUser(id);
    }
    
}
