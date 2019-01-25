package com.assignment1.news_site.service;

import com.assignment1.news_site.exception.ResourceNotFoundException;
import com.assignment1.news_site.model.News;
import com.assignment1.news_site.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsService {

	private NewsRepository newsRepository;

	@Autowired
	public NewsService(NewsRepository newsRepository) {
		this.newsRepository = newsRepository;
	}

	public void saveNews(News news) {
			newsRepository.saveAndFlush(news);

	}



	public News findNewsById(Integer id){
		Optional<News> showNews = newsRepository.findById(id);

		if (showNews.isPresent()){
			return showNews.get();
		}else{
			throw new ResourceNotFoundException();
		}
	}

	public Page<News> findPages(Pageable pageRequest){
		return newsRepository.findAll(pageRequest);
	}

	public boolean deleteNewsById(Integer id){
		if(newsRepository.existsById(id)) {
			newsRepository.deleteById(id);
			return true;
		}
		else throw new ResourceNotFoundException();


	}




}
