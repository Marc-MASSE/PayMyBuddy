package fr.marc.paymybuddy.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Connection addUser(@RequestBody Connection connection) {
		log.info("POST request - endpoint /connection - body = "+connection);
    	return connectionService.addConnection(connection);
    }
	
	
	/*
	 * Page "Buddies"
	 * To add a new buddy or delete a buddy
	 */
    @GetMapping("/buddies")
    public String displayBuddiesPageById(Model model,@RequestParam int id) {
		log.info("GET request - endpoint /buddies - id = "+id);
		
		User user = userService.getUserById(id).get();
		model.addAttribute("user",user);
		
		List<BuddyDTO> buddyList = connectionService.getBuddyList(id);
		model.addAttribute("buddyList",buddyList);
		log.debug("Buddy list = "+buddyList);
		
		BuddyDTO buddy = new BuddyDTO();
		model.addAttribute("buddy",buddy);
		
        return "buddies";
    }
	
	/*
	 * Page "Buddies", add a buddy by Email
	 */
    @PostMapping(value = "/addABuddy")
    public String addABuddy(@ModelAttribute("buddies") BuddyDTO buddy,@RequestParam Integer id) {
		log.info("POST request - endpoint /addABuddy - buddy's email = "+buddy.getEmail());
		try {
			connectionService.addANewBuddy(buddy.getEmail(),id);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return "redirect:/buddies?id="+id.toString();
    }
	
	
	
	@ResponseBody
    @DeleteMapping("/connection")
    public void deleteConnectionById(@RequestParam int Id) {
		log.info("DELETE request - endpoint /connection - id = "+Id);
        connectionService.deleteConnectionById(Id);
    }
	
	
	

}
