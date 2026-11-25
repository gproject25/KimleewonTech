package com.kimleewontech.admin.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimleewontech.admin.entity.Admin;
import com.kimleewontech.admin.enums.AdminLevel;
import com.kimleewontech.admin.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

	private final AdminRepository adminRepository;

	public boolean isAdmin(Long userId) {
		return adminRepository.existsByUserId(userId);
	}

	public Optional<AdminLevel> findAdminLevel(Long userId) {
		return adminRepository.findByUserId(userId).map(Admin::getAdminLevel);
	}
}
