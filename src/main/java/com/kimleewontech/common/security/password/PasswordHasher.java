package com.kimleewontech.common.security.password;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordHasher {

	private final PasswordEncoder passwordEncoder;

	public EncodedPassword encode(RawPassword raw) {
		return EncodedPassword.of(passwordEncoder.encode(raw.getValue()));
	}

	public boolean matches(RawPassword raw, EncodedPassword encoded) {
		return passwordEncoder.matches(raw.getValue(), encoded.getValue());
	}
}
