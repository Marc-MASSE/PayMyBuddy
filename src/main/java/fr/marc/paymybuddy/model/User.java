package fr.marc.paymybuddy.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "email")
	private String email;

	@Column(name = "firstName")
	private String firsName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "password")
	private String password;

	@Column(name = "remember_me")
	private boolean rememberMe;

	@Column(name = "iban")
	private String iban;

	@Column(name = "bank")
	private String bank;

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	List<Transaction> transactions = new ArrayList<>();

	/*
	@OneToMany(
			mappedBy = "user",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	List<Transaction> transactions = new ArrayList<>();
	*/
	
	/*
	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	List<Connection> connections = new ArrayList<>();
	*/
}
