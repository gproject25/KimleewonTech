package com.kimleewontech.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.admin.enums.AdminLevel;
import com.kimleewontech.admin.service.AdminService;
import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.common.security.auth.AuthMember;
import com.kimleewontech.common.security.jwt.JwtProvider;
import com.kimleewontech.common.security.password.EncodedPassword;
import com.kimleewontech.common.security.password.PasswordHasher;
import com.kimleewontech.common.security.password.RawPassword;
import com.kimleewontech.user.dto.request.UserCreateRequest;
import com.kimleewontech.user.dto.request.UserLoginRequest;
import com.kimleewontech.user.dto.response.UserCreateResponse;
import com.kimleewontech.user.dto.response.UserLoginResponse;
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
	private final AdminService adminService;
	private final JwtProvider jwtProvider;

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

	// 2. 로그인
	public UserLoginResponse login(UserLoginRequest request) {

		// 1. 이메일 회원 조회
		User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

		// 2. 비밀번호 비교
		if (!passwordHasher.matches(RawPassword.of(request.getPassword()), user.getPassword())) {

			throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
		}

		// 3. 관리자 등급 조회
		Optional<AdminLevel> adminLevel = adminService.findAdminLevel(user.getId());

		// 4. 토큰 발급
		AuthMember authMember = adminLevel
			.map(level -> AuthMember.admin(user.getId(), level))
			.orElse(AuthMember.user(user.getId()));
		String accessToken = jwtProvider.createAccessToken(authMember);

		// 5. 응답 변환
		return UserLoginResponse.of(accessToken, adminLevel.orElse(null));
	}
}
