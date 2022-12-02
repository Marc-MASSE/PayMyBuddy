package fr.marc.paymybuddy.service;

import java.util.List;

import fr.marc.paymybuddy.DTO.BuddyDTO;
import fr.marc.paymybuddy.model.Connection;

public interface IConnectionService {
	
	public List<BuddyDTO> getBuddyList(Integer userId);
	
	Connection getConnectionByUserIdAndBuddyId(Integer userId, Integer buddyId);
	
	Connection addConnection(Connection connection);
	
	Connection addANewBuddy(String email, Integer UserId);
	
	void deleteConnectionById(Integer id);
	
	String newBuddyAvailabilityMessage (String buddyEmail, String userEmail);

}
