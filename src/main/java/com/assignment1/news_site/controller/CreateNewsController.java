package com.assignment1.news_site.controller;

import com.assignment1.news_site.exception.ResourceNotFoundException;
import com.assignment1.news_site.model.News;


import com.assignment1.news_site.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
public class CreateNewsController {
	@Value("${BASE_URL}")
	private String BASE_URL;

	@RequestMapping("/create")
	public String getCreateNewsPage(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "error";
		}
		model.addAttribute("news", new News());
		return "create_news_form";
	}

	@PostMapping("/submit")
	public String saveSubmittedNews(@Valid @ModelAttribute News news, BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "error";
		}
		if (bindingResult.hasErrors()) {
			return "create_news_form";
		}
		User user = (User) session.getAttribute("user");
		Integer id = user.getId();
		String createNewsUrl = BASE_URL + "user/submit-news/" + id;
		System.out.println("submit method "+createNewsUrl);
		RestTemplate restTemplate = authenticate(session);

		System.out.println("News: --"+news);

		HttpEntity<News> request = new HttpEntity<>(news);
		ResponseEntity response = restTemplate.exchange(createNewsUrl, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK))
			return "error";
		return "redirect:/";
	}

	@RequestMapping("/edit")
	public ModelAndView showDataToEdit(@ModelAttribute("id") Integer id, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return new ModelAndView("error");
		}
		User user = (User) session.getAttribute("user");
		Integer userId = user.getId();
		ModelAndView modelAndView = new ModelAndView("update_news_form");
		String editUrl = BASE_URL + "user/edit/" + userId + "?id=" + id;
		RestTemplate restTemplate = authenticate(session);
		ResponseEntity response = restTemplate.exchange(editUrl, HttpMethod.GET, null, News.class);
		News news;
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			news = (News) response.getBody();
			modelAndView.addObject("news", news);
		} else if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
			throw new ResourceNotFoundException();
		} else {
			return new ModelAndView("error");
		}
		return modelAndView;
	}

	@RequestMapping("/updateNews")
	public String updateNews(@Valid @ModelAttribute News news, @ModelAttribute("id") Integer id, BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "error";
		}
		if (bindingResult.hasErrors()) {
			return "update_news_form";
		}
		User user = (User) session.getAttribute("user");
		Integer userId = user.getId();

		news.setId(id);
		news.setUserId(userId);
		news.setAuthor(user.getFullName());

		String updateUrl = BASE_URL + "user/update-news/" + userId + "?id=" + id;
		RestTemplate restTemplate = authenticate(session);
		HttpEntity<News> request = new HttpEntity<>(news);
		ResponseEntity response = restTemplate.exchange(updateUrl, HttpMethod.PUT, request, String.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return "redirect:/";
		} else if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
			throw new ResourceNotFoundException();
		} else {
			return "error";
		}
	}

	@RequestMapping("/remove")
	public String removeNews(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "error";
		}
		User user = (User) session.getAttribute("user");
		Integer userId = user.getId();
		String removeNewsUrl = BASE_URL + "user/remove/" + userId + "?id=" + id;
		RestTemplate restTemplate = authenticate(session);
		ResponseEntity response = restTemplate.exchange(removeNewsUrl, HttpMethod.DELETE, null, String.class);
		if (response.getStatusCode().equals(HttpStatus.FORBIDDEN))
			return "error";
		redirectAttributes.addFlashAttribute("message", "News Successfully Removed");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/";
	}

	private RestTemplate authenticate(HttpSession session) {
		String email = (String) session.getAttribute("email");
		String password = (String) session.getAttribute("password");
		RestTemplate myRestTemplate = new RestTemplate();
		myRestTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(email, password));
		return myRestTemplate;
	}

}
