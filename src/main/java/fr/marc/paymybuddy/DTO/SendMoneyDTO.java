package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMoneyDTO {
	
	private int userId;
	
	private int buddyId;
	
	private String description;

	private Integer amount;
	
}
