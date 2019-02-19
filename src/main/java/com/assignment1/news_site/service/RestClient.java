package com.assignment1.news_site.service;

import com.assignment1.news_site.model.News;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@Service
public class RestClient {

	public ResponseEntity saveNews(HttpSession session, String createNewsUrl, HttpEntity<News> request) {
		RestTemplate restTemplate = authenticate(session);
		ResponseEntity response = restTemplate.exchange(createNewsUrl, HttpMethod.POST, request, String.class);
		return response;
	}

	public ResponseEntity getNews(HttpSession session, String editUrl) {
		RestTemplate restTemplate = authenticate(session);
		ResponseEntity response = restTemplate.exchange(editUrl, HttpMethod.GET, null, News.class);
		return response;
	}

	public ResponseEntity updateNews(HttpSession session, String updateUrl, News news) {
		RestTemplate restTemplate = authenticate(session);
		HttpEntity<News> request = new HttpEntity<>(news);
		ResponseEntity response = restTemplate.exchange(updateUrl, HttpMethod.PUT, request, String.class);
		return response;
	}

	public ResponseEntity deleteNews(HttpSession session, String removeNewsUrl){
		RestTemplate restTemplate = authenticate(session);
		ResponseEntity response = restTemplate.exchange(removeNewsUrl, HttpMethod.DELETE, null, String.class);
		return response;
	}

	private RestTemplate authenticate(HttpSession session) {
		String email = (String) session.getAttribute("email");
		String password = (String) session.getAttribute("password");
		RestTemplate myRestTemplate = new RestTemplate();
		myRestTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(email, password));
		return myRestTemplate;
	}

}
