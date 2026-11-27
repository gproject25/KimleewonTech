package com.kimleewontech.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kimleewontech.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);
}
