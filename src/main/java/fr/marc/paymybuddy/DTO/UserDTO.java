package fr.marc.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
	
	private int id;

	private String email;

	private String firsName;

	private String lastName;

	private String password;

	private boolean rememberMe;

	private String iban;

	private String bank;

}
