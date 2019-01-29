package com.assignment1.news_site.controller;

import com.assignment1.news_site.model.User;
import com.assignment1.news_site.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class UserController {
	private UserService userService;
	private RestTemplate restTemplate;
	private String BASE_URL = "http://localhost:1515/";


	@Autowired
	public UserController(UserService userService, RestTemplate restTemplate) {
		this.userService = userService;
		this.restTemplate = restTemplate;
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
			new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
			Arrays.asList(
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_OCTET_STREAM));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);


	}

	@GetMapping("/signup-page")
	public ModelAndView getSignUpPage(HttpSession session) {
		if (session.getAttribute("user") != null) {
			return new ModelAndView("redirect:/");
		}

		ModelAndView modelAndView = new ModelAndView("registration");
		modelAndView.addObject("user", new User());
		return modelAndView;
	}

	@GetMapping("/login-page")
	public ModelAndView getLoginPage(HttpSession session) {
		if (session.getAttribute("user") != null) {
			return new ModelAndView("redirect:/");
		}
		ModelAndView modelAndView = new ModelAndView("login");
		modelAndView.addObject("user", new User());
		return modelAndView;
	}

	@PostMapping("/signup")
	public String submitSignUpform(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, @ModelAttribute("confirmPass") String confirmPass) {
		boolean error = false;
		if (bindingResult.hasErrors()) {
			error = true;

		} else if (!user.getPassword().equals(confirmPass)) {
			error = true;
			model.addAttribute("passwordError", "Confirm Password Doesnot Match");
		}
		if (error) {
			return "registration";
		}
		String signUpUrl = BASE_URL + "signup";
		HttpEntity<User> request = new HttpEntity<>(user);


		ResponseEntity response = null;
		try {
			response = restTemplate.postForEntity(signUpUrl, request, String.class);

			if (response.getStatusCode().equals(HttpStatus.OK)) {

				model.addAttribute("saved", response.getBody());
			} else {
				model.addAttribute("emailError", response.getBody());
				return "registration";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "login";
	}


	@RequestMapping("/login")
	public String loginVerification(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, HttpSession session) {
		if (session.getAttribute("user") != null) {
			return "redirect:/";
		}

		if ((bindingResult.getFieldError("email") != null) || (bindingResult.getFieldError("password") != null)) {
			return "login";
		}

		String email = user.getEmail();
		String password = user.getPassword();


		final String plainCreds = email + ":" + password;

		final byte[] plainCredsBytes = plainCreds.getBytes();
		final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		final String base64Creds = new String(base64CredsBytes);

		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		final HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity response = null;
		try {
			response = restTemplate.exchange("http://localhost:1515/user/login", HttpMethod.GET, request, User.class);
		} catch (HttpClientErrorException e) {
		}

		if (response == null) {
			model.addAttribute("wrong", "Email and Password Doestnot Match");
			return "login";
		}
		User loggedInUser = (User) response.getBody();
		session.setAttribute("user", loggedInUser);
		session.setAttribute("login", true);
		return "redirect:/";
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}


}
