package fr.marc.paymybuddy.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "transaction")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	// user_id (FK)

	@Column(name = "transaction_number")
	private int transactionNumber;

	@Column(name = "description")
	private String description;

	@Column(name = "amount")
	private int amount;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "done")
	private boolean done;


}
