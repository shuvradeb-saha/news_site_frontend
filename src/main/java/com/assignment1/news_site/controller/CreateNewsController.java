package com.assignment1.news_site.controller;

import com.assignment1.news_site.model.News;

import com.assignment1.news_site.service.NewsService;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import javax.validation.Valid;
import java.text.ParseException;

@Controller
public class CreateNewsController {
	private NewsService newsService;
	private RestTemplate restTemplate;
	private String BASE_URL = "http://localhost:1515/";

	public CreateNewsController(NewsService newsService, RestTemplate restTemplate) {
		this.newsService = newsService;
		this.restTemplate = restTemplate;
	}

	@RequestMapping("/create")
	public String getCreateNewsPage(Model model) {

		model.addAttribute("news", new News());
		return "create_news_form";
	}

	@PostMapping("/submit")
	public String saveSubmittedNews(@Valid @ModelAttribute News news, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "create_news_form";
		}
		String createNewsUrl = BASE_URL + "submit-news";
		HttpEntity<News> request = new HttpEntity<>(news);
		ResponseEntity response = null;
		try {
			response = restTemplate.postForEntity(createNewsUrl, request, String.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}

	@RequestMapping("/edit")
	public ModelAndView showDataToEdit(@ModelAttribute("id") Integer id) throws ParseException {
		ModelAndView modelAndView = new ModelAndView("update_news_form");
		String editUrl = BASE_URL + "edit?id=" + id;
		ResponseEntity response = restTemplate.getForEntity(editUrl, News.class);
		News news = null;

		if (response.getStatusCode().equals(HttpStatus.OK)) {
			news = (News) response.getBody();
			modelAndView.addObject("news", news);
		}

		return modelAndView;
	}

	@RequestMapping("/updateNews")
	public String updateNews(@Valid @ModelAttribute News news, @ModelAttribute("id") Integer id, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "update_news_form";
		}
		news.setId(id);

		String updateUrl = BASE_URL + "update-news?id=" + id;
		HttpEntity<News> request = new HttpEntity<>(news);
		restTemplate.put(updateUrl, request, String.class);
		return "redirect:/";
	}

	@RequestMapping("/remove")
	public String removeNews(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {

		String removeNewsUrl = BASE_URL + "remove?id=" + id;
		restTemplate.delete(removeNewsUrl);
		redirectAttributes.addFlashAttribute("message", "News Successfully Removed");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/";
	}

}
