package com.assignment1.news_site.model;

import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


@Entity
@Table(name = "news")
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Size(min = 1, max = 200, message = "Title must be between 10 and 100 characters")
	@Column(name = "title", nullable = false)
	private String title;

	@Size(min = 1, max = 2000, message = "Body must be between 10 and 2000 characters")
	@Column(name = "body", nullable = false)
	private String body;

	@Size(min = 1, max = 100, message = "Author name must be between 10 and 100 characters")
	@Column(name = "author", nullable = false)
	private String author;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Date Cannot be empty")
	@Column(name = "date", nullable = false)
	private Date date;

//	private Integer userId;


	public Date getDate() {
		return date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public News(String title, String body, String author, Date date) {
		this.title = title;
		this.body = body;
		this.author = author;
		this.date = date;
	}

	public News() {
	}


	public String getTitle() {
		return title;
	}


	public String getBody() {
		return body;
	}


	public String getAuthor() {
		return author;
	}


	@Override
	public String toString() {
		return "News{" +
			"id=" + id +
			", title='" + title + '\'' +
			", body='" + body + '\'' +
			", author='" + author + '\'' +
			'}';
	}
}
