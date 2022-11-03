package fr.marc.paymybuddy.service;

import java.util.List;

import fr.marc.paymybuddy.DTO.BuddyDTO;

public interface IConnectionService {
	
	public List<BuddyDTO> getBuddyList(Integer userId);

}
