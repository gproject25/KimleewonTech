package com.kimleewontech.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.user.dto.request.CreateUserRequest;
import com.kimleewontech.user.dto.response.CreateUserResponse;
import com.kimleewontech.user.entity.User;
import com.kimleewontech.user.repository.UserRepository;

@Service
public class UserService {

	// 속성
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// 생성자
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// 기능

	// 1. 회원가입
	@Transactional
	public CreateUserResponse createUser(CreateUserRequest request) {

		// 1. 이메일 중복 유무 확인
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}

		// 2. BCrypt 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 3. user 생성
		User user = User.create(request.getName(), request.getEmail(), encodedPassword,
			request.getPhone()
		);

		// 4. 저장
		User savedUser = userRepository.save(user);

		// 5. 반환
		return CreateUserResponse.from(savedUser);

	}
}
