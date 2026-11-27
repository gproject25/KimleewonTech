package com.kimleewontech.common.security.password;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class RawPassword {
	private final String value;

	private RawPassword(String value) {
		if (value == null || value.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
		this.value = value;
	}

	public static RawPassword of(String value) {
		return new RawPassword(value);
	}

	@Override
	public String toString() {
		return "RawPassword(****)";
	}
}
