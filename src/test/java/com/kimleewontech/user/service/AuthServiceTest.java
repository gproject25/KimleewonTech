package com.kimleewontech.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kimleewontech.admin.enums.AdminLevel;
import com.kimleewontech.admin.service.AdminService;
import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.common.security.auth.AuthMember;
import com.kimleewontech.common.security.auth.AuthRole;
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

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordHasher passwordHasher;

	@Mock
	private AdminService adminService;

	@Mock
	private JwtProvider jwtProvider;

	@InjectMocks
	private AuthService authService;

	// ===== 회원가입 =====

	@Test
	void 이미_가입한_이메일이면_DUPLICATE_EMAIL_예외() {
		// given
		UserCreateRequest request = mock(UserCreateRequest.class);
		given(request.getEmail()).willReturn("abc@gmail.com");
		given(userRepository.existsByEmail("abc@gmail.com")).willReturn(true);

		// when & then
		assertThatThrownBy(() -> authService.createUser(request))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATE_EMAIL);

		// then
		then(userRepository).should(never()).save(any());
	}

	@Test
	void 회원가입_성공시_암호화된_비밀번호로_저장() {
		// given
		UserCreateRequest request = mock(UserCreateRequest.class);
		given(request.getName()).willReturn("철수");
		given(request.getEmail()).willReturn("a@a.com");
		given(request.getPassword()).willReturn("abcd123!");
		given(request.getPhone()).willReturn("01012345678");
		given(userRepository.existsByEmail("a@a.com")).willReturn(false);

		EncodedPassword encodedPassword = mock(EncodedPassword.class);
		given(passwordHasher.encode(any(RawPassword.class))).willReturn(encodedPassword);
		given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		UserCreateResponse response = authService.createUser(request);

		// then
		assertThat(response.getEmail()).isEqualTo("a@a.com");
		assertThat(response.getName()).isEqualTo("철수");
		then(userRepository).should().save(argThat(user -> user.getPassword() == encodedPassword));
	}

	// ===== 로그인 =====
	@Test
	void 로그인_가입하지_않았거나_탈퇴한_이메일이면_INVALID_CREDENTIALS_예외() {
		// given
		UserLoginRequest request = mock(UserLoginRequest.class);
		given(request.getEmail()).willReturn("a@a.com");
		given(userRepository.findByEmailAndDeletedAtIsNull("a@a.com")).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_CREDENTIALS);

		then(jwtProvider).should(never()).createAccessToken(any());
	}

	@Test
	void 로그인_비밀번호가_틀리면_INVALID_CREDENTIALS_예외() {
		// given
		UserLoginRequest request = mock(UserLoginRequest.class);
		given(request.getEmail()).willReturn("a@a.com");
		given(request.getPassword()).willReturn("wrong123!");

		User user = mock(User.class);
		EncodedPassword encodedPassword = mock(EncodedPassword.class);
		given(user.getPassword()).willReturn(encodedPassword);
		given(userRepository.findByEmailAndDeletedAtIsNull("a@a.com")).willReturn(Optional.of(user));
		given(passwordHasher.matches(any(RawPassword.class), eq(encodedPassword))).willReturn(false);

		// when & then
		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_CREDENTIALS);

		then(jwtProvider).should(never()).createAccessToken(any());
	}

	@Test
	void 로그인_관리자면_ADMIN_토큰과_등급_반환() {
		// given
		UserLoginRequest request = mock(UserLoginRequest.class);
		given(request.getEmail()).willReturn("admin@a.com");
		given(request.getPassword()).willReturn("abcd123!");

		User user = mock(User.class);
		EncodedPassword encodedPassword = mock(EncodedPassword.class);
		given(user.getId()).willReturn(1L);
		given(user.getPassword()).willReturn(encodedPassword);
		given(userRepository.findByEmailAndDeletedAtIsNull("admin@a.com")).willReturn(Optional.of(user));
		given(passwordHasher.matches(any(RawPassword.class), eq(encodedPassword))).willReturn(true);
		given(adminService.findAdminLevel(1L)).willReturn(Optional.of(AdminLevel.SUPER));
		given(jwtProvider.createAccessToken(any(AuthMember.class))).willReturn("ADMIN_TOKEN");

		// when
		UserLoginResponse response = authService.login(request);

		// then
		assertThat(response.getAccessToken()).isEqualTo("ADMIN_TOKEN");
		assertThat(response.isAdmin()).isTrue();
		assertThat(response.getAdminLevel()).isEqualTo(AdminLevel.SUPER);
		then(jwtProvider).should().createAccessToken(argThat(member ->
			member.getMemberId().equals(1L)
				&& member.getRole() == AuthRole.ADMIN
				&& member.getAdminLevel() == AdminLevel.SUPER));
	}
}
