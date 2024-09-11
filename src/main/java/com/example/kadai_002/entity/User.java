package com.example.kadai_002.entity;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "furigana")
	private String furigana;

	@Column(name = "date_of_birth")
	private Date dateOfBirth;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "is_paid_member")
	private Boolean isPaidMember;

	public Boolean isPaidMember() {
		return this.isPaidMember;
	}

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	@Column(name = "stripe_customer_id")
	private String stripeCustomerId;

	@Column(name = "stripe_subscription_id")
	private String stripeSubscriptionId;

	@Column(name = "card_last4")
	private String cardLast4;

	@Column(name = "card_brand")
	private String cardBrand;

	@Column(name = "card_exp_month")
	private Integer cardExpMonth;

	@Column(name = "card_exp_year")
	private Integer cardExpYear;

}
