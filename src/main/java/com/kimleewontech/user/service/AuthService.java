package com.kimleewontech.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.common.security.password.EncodedPassword;
import com.kimleewontech.common.security.password.PasswordHasher;
import com.kimleewontech.common.security.password.RawPassword;
import com.kimleewontech.user.dto.request.UserCreateRequest;
import com.kimleewontech.user.dto.response.UserCreateResponse;
import com.kimleewontech.user.entity.User;
import com.kimleewontech.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	// 속성
	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;

	// 기능

	// 1. 회원가입
	@Transactional
	public UserCreateResponse createUser(UserCreateRequest request) {

		// 1. 이메일 중복 유무 확인
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}

		// 2. BCrypt 비밀번호 암호화
		EncodedPassword encodedPassword = passwordHasher.encode(RawPassword.of(request.getPassword()));

		// 3. user 생성
		User user = User.create(request.getName(), request.getEmail(), encodedPassword,
			request.getPhone()
		);

		// 4. 저장
		User savedUser = userRepository.save(user);

		// 5. 반환
		return UserCreateResponse.from(savedUser);

	}
}
