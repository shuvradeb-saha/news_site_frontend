package com.assignment1.news_site.controller;


import com.assignment1.news_site.model.News;
import com.assignment1.news_site.service.NewsService;


import com.google.gson.Gson;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class WelcomeController {


	private final RestTemplate restTemplate;


	private static final int INITIAL_PAGE = 0;
	private static final int INITIAL_PAGE_SIZE = 3;
	private static final int[] PAGE_SIZES = {3, 5, 6, 10};


	private NewsService newsService;
	@Autowired
	public WelcomeController(NewsService newsService, RestTemplate restTemplate) {
		this.newsService = newsService;
		this.restTemplate = restTemplate;
	}



	@GetMapping("/")
	public ModelAndView getWelcomePage(@RequestParam("pageSize") Optional<Integer> pageSize,
									   @RequestParam("page") Optional<Integer> page, HttpSession session) throws JSONException, NoSuchFieldException, IllegalAccessException {

		ModelAndView modelAndView = new ModelAndView("welcome");

		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

		String url = "http://localhost:1515/?pageSize=" + evalPageSize + "&page=" + evalPage;
		String response = null;
		try {
			if(restTemplate.getForObject(url, String.class) != null){
				response = restTemplate.getForObject(url, String.class);

			}
		} catch (Exception e) {
			e.printStackTrace();

			return new ModelAndView("error/error_404");
		}


		JSONObject newsListJsonResponse = new JSONObject(response);

		int pagerStart = newsListJsonResponse.getInt("pagerStart");
		int pagerEnd = newsListJsonResponse.getInt("pagerEnd");
		int totalPages = newsListJsonResponse.getInt("totalPages");
		int number = newsListJsonResponse.getInt("number");


		JSONArray jsonArray = newsListJsonResponse.getJSONArray("newsList");

		if (jsonArray.length() == 0) {

			modelAndView.addObject("noNews", "No News Available");
			return modelAndView;
		}

		List<News> newsObjectList = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			News obj = new Gson().fromJson(jsonObject.toString(), News.class);
			newsObjectList.add(obj);
		}


		modelAndView.addObject("newsList", newsObjectList);
		modelAndView.addObject("totalPages", totalPages);
		modelAndView.addObject("number", number);
		modelAndView.addObject("selectedPageSize", evalPageSize);
		modelAndView.addObject("pageSizes", PAGE_SIZES);
		modelAndView.addObject("pagerStart", pagerStart);
		modelAndView.addObject("pagerEnd", pagerEnd);
		modelAndView.addObject("viewNews", new News());


		return modelAndView;


	}
}
