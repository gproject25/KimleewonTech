package com.kimleewontech.admin.entity;

import com.kimleewontech.admin.enums.AdminLevel;
import com.kimleewontech.common.entity.BaseEntity;
import com.kimleewontech.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "admin_level", nullable = false, length = 20)
	private AdminLevel adminLevel;

	private Admin(User user, AdminLevel adminLevel) {
		this.user = user;
		this.adminLevel = adminLevel;
	}

	public static Admin create(User user, AdminLevel adminLevel) {
		return new Admin(user, adminLevel);
	}

	public boolean isSuper() {
		return adminLevel == AdminLevel.SUPER;
	}
}
