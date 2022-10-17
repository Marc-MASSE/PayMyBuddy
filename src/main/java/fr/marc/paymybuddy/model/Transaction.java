package fr.marc.paymybuddy.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "transaction")
public class Transaction implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
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
	

	// user_id (FK)
	@ManyToOne (
			fetch =FetchType.EAGER,
			optional = false)
	@JoinColumn(
			name = "user_id",
			nullable = false)
	private User user;

}
