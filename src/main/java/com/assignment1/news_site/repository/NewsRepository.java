package com.assignment1.news_site.repository;

import com.assignment1.news_site.model.News;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NewsRepository extends JpaRepository<News, Integer> {

}