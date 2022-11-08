package fr.marc.paymybuddy.model;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "transaction")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "transaction_number")
	private int transactionNumber;
	
	@Column(name = "buddy_id")
	private int buddyId;

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
