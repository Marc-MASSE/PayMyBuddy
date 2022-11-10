package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
	
	// arrow=true(right) if amount>=0, arrow= false(left) unless
	private boolean arrow;
	
	private String buddyName;
	
	private String date;
	
	private String description;
	
	private String amount;
}
