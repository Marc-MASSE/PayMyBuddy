package fr.marc.paymybuddy.model;

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
public class Connection {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	// user_id (FK)
	@ManyToOne (
			fetch =FetchType.LAZY,
			optional = false)
	@JoinColumn(
			name = "user_id",
			nullable = false)
	private User user;
	
	// buddy_id (FK)
	@ManyToOne (
			fetch =FetchType.LAZY,
			optional = false)
	@JoinColumn(
			name = "buddy_id",
			nullable = false)
	private User buddy;

}
