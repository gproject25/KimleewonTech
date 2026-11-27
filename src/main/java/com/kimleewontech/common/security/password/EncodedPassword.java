package com.kimleewontech.common.security.password;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EncodedPassword {
	@Column(name = "password", nullable = false)
	private String value;

	private EncodedPassword(String value) {
		this.value = value;
	}

	//PasswordHaser에서만 사용
	static EncodedPassword of(String value) {
		return new EncodedPassword(value);
	}

	@Override
	public String toString() {
		return "EncodedPassword(****)";
	}
}
