package com.assignment1.news_site.controller;

import com.assignment1.news_site.exception.ResourceNotFoundException;
import com.assignment1.news_site.model.News;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
public class ViewNewsController {

	@Value("${BASE_URL}")
	private String BASE_URL;

	@RequestMapping("/view")
	public String viewThisNews(@ModelAttribute("id") Integer id, Model model) {
		boolean not_found = false;
		if (id == null) {
			not_found = true;
		} else {
			String viewNewsUrl = BASE_URL + "view/json?id=" + id;
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity response = restTemplate.getForEntity(viewNewsUrl, News.class);
			if (response.getBody() == null) {
				not_found = true;
			} else {
				News showNews = (News) response.getBody();
				model.addAttribute("showNews", showNews);
			}
		}
		if (not_found)
			throw new ResourceNotFoundException();
		return "view_news";
	}

	@GetMapping("/view/{format}")
	public void viewNewsInDifferentFormat(@PathVariable("format") String format, @RequestParam("id") Integer id, HttpServletResponse response) throws IOException{
		if (format.equals("json")) {
			showJson(response, id);
		} else if (format.equals("xml")) {
			showXML(response, id);
		} else {
			throw new ResourceNotFoundException();
		}
	}

	private void showXML(HttpServletResponse httpServletResponse, Integer id) throws IOException {
		String viewNewsUrl = BASE_URL + "view/xml?id=" + id;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity response = restTemplate.getForEntity(viewNewsUrl, String.class);
		httpServletResponse.getWriter().print(response.getBody());
	}

	private void showJson(HttpServletResponse httpServletResponse, Integer id) throws IOException {
		String viewNewsUrl = BASE_URL + "view/json?id=" + id;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity response = restTemplate.getForEntity(viewNewsUrl, String.class);
		httpServletResponse.getWriter().print(response.getBody());
	}
}
