package com.assignment1.news_site.controller;

import com.assignment1.news_site.model.User;
import com.assignment1.news_site.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;

@Controller
public class UserController {
	private UserService userService;


	@Autowired
	public UserController(UserService userService, RestTemplate restTemplate) {
		this.userService = userService;

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
	public String submitSignUpform(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, @ModelAttribute("confirmPass") String confirmPass, HttpSession session) {

		if (session.getAttribute("user") != null) {
			return "redirect:/";
		}

		boolean error = false;
		if (bindingResult.hasErrors()) {
			error = true;

		} else if (userService.checkEmailExists(user.getEmail())) {
			error = true;
			model.addAttribute("emailError", "Email You Entered Already Exists");
		} else if (!user.getPassword().equals(confirmPass)) {
			error = true;
			model.addAttribute("passwordError", "Confirm Password Doesnot Match");
		}
		if (error) {
			return "registration";
		}
		userService.saveUser(user);
		model.addAttribute("saved", "Registration Completed. Now Login Please");
		return "login";
	}

	@PostMapping("/login")
	public @ResponseBody
	String loginVerification(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpSession session) throws JSONException {
		if ((bindingResult.getFieldError("email") != null) || (bindingResult.getFieldError("password") != null)) {
			return "login";
		}
		String email = user.getEmail();
		String password = user.getPassword();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		try {
			JSONObject jsonCredentials = new JSONObject();
			jsonCredentials.put("email", email);
			jsonCredentials.put("password", password);

			HttpEntity<String> entityCredentials = new HttpEntity<>(jsonCredentials.toString(), httpHeaders);
			String url = "http://localhost:1515/login";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url,
				HttpMethod.POST, entityCredentials, String.class);
			if (responseEntity != null) {
				return responseEntity.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;


/*
		JSONObject userLoginInfo = new JSONObject();

		userLoginInfo.put("email",email);
		userLoginInfo.put("password",password);

		*//*MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("email", email);
		map.add("password", password);
*//*
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<JSONObject> request = new HttpEntity<>(userLoginInfo, headers);
		String url = "http://localhost:1515/login";
		ResponseEntity<String> response;
		response = restTemplate.postForEntity(url, request, String.class);*/

		//return response.toString();

/*

		JSONObject userLoginInfo = new JSONObject();

		userLoginInfo.put("email",email);
		userLoginInfo.put("password",password);
*/




		/*session.setAttribute("login", true);
		//session.setAttribute("user", login_user);
		return "redirect:/";*/
	}


	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}


}
