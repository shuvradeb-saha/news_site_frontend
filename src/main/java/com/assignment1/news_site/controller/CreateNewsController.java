package com.assignment1.news_site.controller;

import com.assignment1.news_site.model.News;
import com.assignment1.news_site.service.NewsService;

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
	private NewsService newsService;

	public CreateNewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	@RequestMapping("/create")
	public String getCreateNewsPage(Model model, HttpSession session) {

		if (session.getAttribute("user") == null) {

			return "/error/error_404";
		}
		model.addAttribute("news", new News());
		return "create_news_form";
	}

	@PostMapping("/submit")
	public String saveSubmittedNews(@Valid @ModelAttribute News news, BindingResult bindingResult, HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "/error/error_404";
		}
		if (bindingResult.hasErrors()) {
			return "create_news_form";
		}
		newsService.saveNews(news);
		return "redirect:/";
	}

	@RequestMapping("/edit")
	public ModelAndView showDataToEdit(@ModelAttribute("id") Integer id, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return new ModelAndView("/error/error_404");
		}


		ModelAndView modelAndView = new ModelAndView("update_news_form");
		News news = newsService.findNewsById(id);
		modelAndView.addObject("news", news);
		return modelAndView;
	}

	@RequestMapping("/updateNews")
	public String updateNews(@Valid @ModelAttribute News news, @ModelAttribute("id") Integer id, BindingResult bindingResult, Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "/error/error_404";
		}

		if (bindingResult.hasErrors()) {
			return "update_news_form";
		}
		news.setId(id);
		newsService.saveNews(news);
		News updatedNews = newsService.findNewsById(id);
		model.addAttribute("message", "This News Successfully Updated");
		model.addAttribute("alertClass", "alert-success");
		model.addAttribute("showNews", updatedNews);

		return "view_news";
	}

	@RequestMapping("/remove")
	public String removeNews(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "/error/error_404";
		}
		if (newsService.deleteNewsById(id)) {
			redirectAttributes.addFlashAttribute("message", "News Successfully Removed");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");
			return "redirect:/";
		}
		return null;
	}

}
