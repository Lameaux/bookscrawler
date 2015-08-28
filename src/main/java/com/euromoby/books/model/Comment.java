package com.euromoby.books.model;

public class Comment {

	private Integer id;
	private Integer bookId;
	private String login;
	private Long created;
	private String comment;
	private Integer grade;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", bookId=" + bookId + ", login=" + login + ", created=" + created + ", comment=" + comment + ", grade=" + grade + "]";
	}

}
