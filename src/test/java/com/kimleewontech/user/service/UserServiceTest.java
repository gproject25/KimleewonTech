package com.kimleewontech.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kimleewontech.common.exception.BusinessException;
import com.kimleewontech.common.exception.ErrorCode;
import com.kimleewontech.user.dto.request.CreateUserRequest;
import com.kimleewontech.user.dto.response.CreateUserResponse;
import com.kimleewontech.user.entity.User;
import com.kimleewontech.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void 이미_가입한_이메일이면_DUPLICATE_EMAIL_예외() {
		// given: 이메일이 이미 있는 상황
		CreateUserRequest request = mock(CreateUserRequest.class);
		given(request.getEmail()).willReturn("abc@gmail.com");
		given(userRepository.existsByEmail("abc@gmail.com")).willReturn(true);

		// when & then: 실행하면 DUPLICATE_EMAIL 예외가 나야 함
		assertThatThrownBy(() -> userService.createUser(request))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATE_EMAIL);

		// then: 저장은 한 번도 안 해야 함
		then(userRepository).should(never()).save(any());
	}

	@Test
	void 회원가입_성공시_암호화된_비밀번호로_저장() {
		// given: 정상 요청 + 중복 아님 + 암호화 결과 + 저장
		CreateUserRequest request = mock(CreateUserRequest.class);
		given(request.getName()).willReturn("철수");
		given(request.getEmail()).willReturn("a@a.com");
		given(request.getPassword()).willReturn("abcd123!");
		given(request.getPhone()).willReturn("01012345678");
		given(userRepository.existsByEmail("a@a.com")).willReturn(false);
		given(passwordEncoder.encode("abcd123!")).willReturn("ENCODED");
		given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when: 회원가입 실행
		CreateUserResponse response = userService.createUser(request);

		// then: 응답 값 확인 + 암호화된 비밀번호로 저장됐는지 확인
		assertThat(response.getEmail()).isEqualTo("a@a.com");
		assertThat(response.getName()).isEqualTo("철수");
		then(userRepository).should().save(argThat(user -> user.getPassword().equals("ENCODED")));
	}
}