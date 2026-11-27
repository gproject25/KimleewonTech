package com.kimleewontech.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.common.security.password.EncodedPassword;
import com.kimleewontech.common.security.password.PasswordHasher;
import com.kimleewontech.common.security.password.RawPassword;
import com.kimleewontech.user.dto.request.UserCreateRequest;
import com.kimleewontech.user.dto.response.UserCreateResponse;
import com.kimleewontech.user.entity.User;
import com.kimleewontech.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordHasher passwordHasher;

	@InjectMocks
	private AuthService authService;

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
}
