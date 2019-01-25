package com.assignment1.news_site.controller;

import com.assignment1.news_site.exception.ResourceNotFoundException;
import com.assignment1.news_site.model.News;
import com.assignment1.news_site.service.NewsService;
import com.fasterxml.jackson.xml.XmlMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ViewNewsController {
	private NewsService newsService;

	public ViewNewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	@RequestMapping("/view")
	public String viewThisNews(@ModelAttribute("id") Integer id, Model model) {
		boolean not_found = false;
		if (id == null) {
			not_found = true;
		} else {
			News showNews = newsService.findNewsById(id);

			if (showNews == null) {
				not_found = true;
			} else {
				model.addAttribute("showNews", showNews);
			}
		}
		if (not_found)
			throw new ResourceNotFoundException();

		return "view_news";
	}

	@RequestMapping("view/json")
	public @ResponseBody
	News getJSON(@ModelAttribute("id") Integer id) {
		if (id == null)
			throw new ResourceNotFoundException();
		return newsService.findNewsById(id);
	}

	@RequestMapping("view/xml")
	@ResponseBody
	public void getXML(@ModelAttribute("id") Integer id, HttpServletResponse response) {
		News showNews = newsService.findNewsById(id);
		XmlMapper mapper = new XmlMapper();
		String xml = null;
		try {
			xml = mapper.writeValueAsString(showNews);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			response.getWriter().print(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
