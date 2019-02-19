package com.assignment1.news_site.controller;

import com.assignment1.news_site.exception.ResourceNotFoundException;
import com.assignment1.news_site.model.News;

import com.assignment1.news_site.model.User;
import com.assignment1.news_site.service.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class CreateNewsController {
	@Value("${BASE_URL}")
	private String BASE_URL;

	@Autowired
	private RestClient restClient;

	@GetMapping("/create")
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
		String createNewsUrl = BASE_URL + "news";
		HttpEntity<News> request = new HttpEntity<>(news);

		ResponseEntity response = restClient.saveNews(session, createNewsUrl, request);
		if (!response.getStatusCode().equals(HttpStatus.OK))
			return "error";
		return "redirect:/";
	}

	@RequestMapping("/edit")
	public ModelAndView showDataToEdit(@ModelAttribute("id") Integer id, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return new ModelAndView("error");
		}
		ModelAndView modelAndView = new ModelAndView("update_news_form");

		String editUrl = BASE_URL + "news" + "?id=" + id;

		ResponseEntity response = restClient.getNews(session, editUrl);
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
		news.setId(id);
		news.setAuthor(user.getFullName());
		String updateUrl = BASE_URL + "news" + "?id=" + id;
		ResponseEntity response = restClient.updateNews(session, updateUrl, news);

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
		String removeNewsUrl = BASE_URL + "news" + "?id=" + id;

		ResponseEntity response = restClient.deleteNews(session, removeNewsUrl);
		if (response.getStatusCode().equals(HttpStatus.FORBIDDEN))
			return "error";
		redirectAttributes.addFlashAttribute("message", "News Successfully Removed");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/";
	}

}
