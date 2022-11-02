package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendMoneyDTO {
	
	private int user_id;
	
	private int buddy_id;
	
	private String description;

	private int amount;
	
}
