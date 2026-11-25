package com.kimleewontech.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kimleewontech.admin.entity.Admin;
import com.kimleewontech.admin.enums.AdminLevel;
import com.kimleewontech.admin.repository.AdminRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private AdminRepository adminRepository;

	@InjectMocks
	private AdminService adminService;

	@Test
	void 관리자로_등록된_회원이면_true() {
		given(adminRepository.existsByUserId(1L)).willReturn(true);

		assertThat(adminService.isAdmin(1L)).isTrue();
	}

	@Test
	void 관리자가_아닌_회원이면_false() {
		given(adminRepository.existsByUserId(2L)).willReturn(false);

		assertThat(adminService.isAdmin(2L)).isFalse();
	}

	@Test
	void 관리자가_등급을_반환() {
		Admin admin = mock(Admin.class);
		given(admin.getAdminLevel()).willReturn(AdminLevel.SUPER);
		given(adminRepository.findByUserId(1L)).willReturn(Optional.of(admin));

		assertThat(adminService.findAdminLevel(1L)).contains(AdminLevel.SUPER);
	}

	@Test
	void 관리자가_아니면_빈_값() {
		given(adminRepository.findByUserId(2L)).willReturn(Optional.empty());

		assertThat(adminService.findAdminLevel(2L)).isEmpty();
	}
}
