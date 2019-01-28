package com.assignment1.news_site.controller;

import com.assignment1.news_site.exception.ResourceNotFoundException;
import com.assignment1.news_site.model.News;
import com.assignment1.news_site.service.NewsService;
import com.fasterxml.jackson.xml.XmlMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ViewNewsController {
	private NewsService newsService;
	private RestTemplate restTemplate;
	private String BASE_URL = "http://localhost:1515/";

	public ViewNewsController(NewsService newsService, RestTemplate restTemplate) {
		this.newsService = newsService;
		this.restTemplate = restTemplate;
	}

	@RequestMapping("/view")
	public String viewThisNews(@ModelAttribute("id") Integer id, Model model) {
		boolean not_found = false;
		if (id == null) {
			not_found = true;
		} else {

			String viewNewsUrl = BASE_URL+"view/json?id="+id;
			ResponseEntity response = restTemplate.getForEntity(viewNewsUrl,News.class);
			//System.out.println(response.toString());
			if (response.getBody() == null) {
				not_found = true;
			} else {
				News showNews = (News) response.getBody();
				System.out.println(showNews.toString());
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
