package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SendMoneyDTO {
	
	private int user_id;
	
	private int buddy_id;
	
	private String description;

	private int amount;
	
}
