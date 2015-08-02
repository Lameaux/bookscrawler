package com.euromoby.books.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.euromoby.books.model.Book;

@Component
public class BookDao {

	private DataSource dataSource;

	private static final BookRowMapper ROW_MAPPER = new BookRowMapper();

	@Autowired
	public BookDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Book findById(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return jdbcTemplate.queryForObject("select * from book where id = ?", ROW_MAPPER, id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void save(Book book) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("insert into book(id, author_id, title, url, annotation, genres, publisher, year, isbn) values (?,?,?,?,?,?,?,?,?)", 
				book.getId(), book.getAuthor().getId(), book.getTitle(), book.getUrl(), book.getAnnotation(), book.getGenresString(), book.getPublisher(),
				book.getYear(), book.getIsbn());
	}

	static class BookRowMapper implements RowMapper<Book> {
		@Override
		public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
			Book book = new Book();
			book.setId(rs.getInt("id"));
			book.setTitle(rs.getString("title"));
			return book;
		}
	}
	
}
