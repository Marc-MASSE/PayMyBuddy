package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankOrderDTO {
	
	private int userId;
	
	private int amount;
	
	// -1 : sending to my bank order
	// 1 : receiving from my bank order
	private int operationType;

}
