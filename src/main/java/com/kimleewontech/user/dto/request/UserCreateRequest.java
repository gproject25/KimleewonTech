package com.kimleewontech.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자 이하로 입력해 주세요.")
	private String name;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])\\S{8,20}$",
		message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자로 입력해 주세요."
	)
	private String password;

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자만 10~11자리로 입력해 주세요.")
	private String phone;
}