package fr.marc.paymybuddy.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDTO {
	
	private int id;
	
	private int transactionNumber;

	private String description;

	private Integer amount;

	private LocalDate date;

	private boolean done;
	
	private int userId;

}
