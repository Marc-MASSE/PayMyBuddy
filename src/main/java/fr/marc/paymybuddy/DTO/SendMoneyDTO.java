package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMoneyDTO {
	
	private int userId;
	
	private int buddyId;
	
	private String description;

	private int amount;
	
}
