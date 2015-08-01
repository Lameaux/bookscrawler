package com.euromoby.books.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.euromoby.books.model.BookGenre;

@Component
public class BookGenreDao {
	private DataSource dataSource;

	@Autowired
	public BookGenreDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(BookGenre bookGenre) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("insert into book_genre(book_id, genre_id) values (?,?)", bookGenre.getBookId(), bookGenre.getGenreId());
	}

}
