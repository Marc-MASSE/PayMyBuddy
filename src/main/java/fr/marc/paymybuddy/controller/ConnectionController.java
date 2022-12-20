package fr.marc.paymybuddy.controller;

import java.util.List;

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
import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.DTO.SendMoneyDTO;
import fr.marc.paymybuddy.model.Connection;
import fr.marc.paymybuddy.model.User;
import fr.marc.paymybuddy.service.IConnectionService;
import fr.marc.paymybuddy.service.IUserService;

@Controller
public class ConnectionController {
	
	static Logger log = LogManager.getLogger(ConnectionController.class.getName());
	
	@Autowired
	private IConnectionService connectionService;
	
	@Autowired
	private IUserService userService;
	
	
	@ResponseBody
    @PostMapping(value = "/connection")
    public Connection addAConnection(@RequestBody Connection connection) {
		log.info("POST request - endpoint /connection - body = "+connection);
    	return connectionService.addConnection(connection);
    }
	
	
	/*
	 * Page "Buddies"
	 * To add a new buddy or delete a buddy
	 */
    @GetMapping("/buddies")
    public String displayBuddiesPageById(Model model, 
    		@RequestParam (name="message", defaultValue="") String message) {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		log.info("GET request - endpoint /buddies - email = "+connectedEmail);
		
		model.addAttribute("user",user);
		
		model.addAttribute("message",message);
		
		List<BuddyDTO> buddyList = connectionService.getBuddyList(user.getId());
		model.addAttribute("buddyList",buddyList);
		log.info("Buddy list = "+buddyList);
		
		BuddyDTO buddy = new BuddyDTO();
		model.addAttribute("buddy",buddy);
		
        return "buddies";
    }
	
	/*
	 * Page "Buddies", add a buddy by Email
	 */
    @PostMapping(value = "/addABuddy")
    public String addABuddy(@ModelAttribute("buddies") BuddyDTO buddy) {
		String connectedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByEmail(connectedEmail).get();
		log.info("POST request - endpoint /addABuddy - buddy's email = "+buddy.getEmail());
		
		String message = connectionService.newBuddyAvailabilityMessage(buddy.getEmail(), connectedEmail);
		log.info("Message = {}",message);
		
		if (message=="") {
			connectionService.addANewBuddy(buddy.getEmail(),user.getId());
		}
		return "redirect:/buddies?message="+message;
    }
	
	
	
	@ResponseBody
    @DeleteMapping("/connection")
    public void deleteConnectionById(@RequestParam int Id) {
		log.info("DELETE request - endpoint /connection - id = "+Id);
        connectionService.deleteConnectionById(Id);
    }
	
	
	

}
