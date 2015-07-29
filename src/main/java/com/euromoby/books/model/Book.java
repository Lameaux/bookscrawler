package com.euromoby.books.model;

import java.util.List;

public class Book {

	private Author author;
	private String title;
	private List<String> annotation;
	private List<String> genres;

	private String publisher;
	private String year;
	private String isbn;

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(List<String> annotation) {
		this.annotation = annotation;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	@Override
	public String toString() {
		return "Book [author=" + author + ", title=" + title + ", annotation=" + annotation + ", genres=" + genres + ", publisher=" + publisher + ", year="
				+ year + ", isbn=" + isbn + "]";
	}

}
