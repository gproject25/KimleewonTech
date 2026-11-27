package com.kimleewontech.user.entity;

import com.kimleewontech.common.entity.BaseEntity;
import com.kimleewontech.common.security.password.EncodedPassword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private EncodedPassword password;

	@Column(nullable = false, length = 20)
	private String phone;

	@Builder
	private User(String name, String email, EncodedPassword password, String phone) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
	}

	public static User create(String name, String email, EncodedPassword encodedPassword, String phone) {
		return User.builder()
			.name(name)
			.email(email)
			.password(encodedPassword)
			.phone(phone)
			.build();
	}
}