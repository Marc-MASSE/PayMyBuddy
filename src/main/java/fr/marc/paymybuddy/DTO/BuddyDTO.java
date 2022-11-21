package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuddyDTO {
	
	private int id;
	
	private String buddyName;
	
	private String email;
}
