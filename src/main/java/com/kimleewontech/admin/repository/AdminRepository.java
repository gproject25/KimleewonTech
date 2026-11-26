package com.kimleewontech.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kimleewontech.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

	boolean existsByUserIdAndDeletedAtIsNull(Long userId);

	Optional<Admin> findByUserIdAndDeletedAtIsNull(Long userId);
}
