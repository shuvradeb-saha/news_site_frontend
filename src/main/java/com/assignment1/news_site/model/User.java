package com.assignment1.news_site.model;

import com.assignment1.news_site.annotaion.ValidEmail;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@NotBlank
	@Size(min=2, max=30, message = "Name Must Be Between 2 And 30 Characters")
	private String fullName;

	@NotBlank
	@ValidEmail(message = "Please Enter Email In Correct Format")
	private String email;

	@Size(min=2, max=100, message = "Password Must Be Between 2 And 100 Characters")
	private String password;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", fullName='" + fullName + '\'' +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			'}';
	}
}
