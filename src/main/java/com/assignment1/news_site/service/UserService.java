package com.assignment1.news_site.service;


import com.assignment1.news_site.model.User;
import com.assignment1.news_site.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
	private final BCryptPasswordEncoder bCryptPasswordEncoder;



	private UserRepository userRepository;
	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public boolean checkEmailExists(String email){
		if (userRepository.count()==0)
			return false;
		return userRepository.existsByEmail(email);
	}

	public void saveUser(User user){
		String encoddedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encoddedPassword);
		userRepository.saveAndFlush(user);
	}

	public User getUserByEmail(String email){
		Optional<User> u = userRepository.findByEmail(email);
		return u.orElse(null);
	}

	public boolean matchPassword(String inputtedPassword, String realPassword){
		return bCryptPasswordEncoder.matches(inputtedPassword, realPassword);
	}
}
