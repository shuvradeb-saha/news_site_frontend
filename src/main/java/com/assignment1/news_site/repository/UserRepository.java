package com.assignment1.news_site.repository;

import com.assignment1.news_site.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}
