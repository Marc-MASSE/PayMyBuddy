package fr.marc.paymybuddy.DTO;

import lombok.Data;

@Data
public class ActivityDTO {
	
	// arrow=true(right) if amount>=0, arrow= false(left) unless
	private boolean arrow;
	
	private String buddyName;
	
	private String date;
	
	private String description;
	
	private int amount;
}
