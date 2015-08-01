package com.euromoby.books.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.euromoby.books.model.Author;

@Component
public class AuthorDao {
	private DataSource dataSource;

	private static final AuthorRowMapper ROW_MAPPER = new AuthorRowMapper();

	@Autowired
	public AuthorDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Author findById(String id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return jdbcTemplate.queryForObject("select * from author where id = ?", ROW_MAPPER, id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void save(Author author) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("insert into author(id, first_name, last_name) values (?, ?,?)", author.getId(), author.getFirstName(), author.getLastName());
	}

	static class AuthorRowMapper implements RowMapper<Author> {
		@Override
		public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
			Author author = new Author();
			author.setId(rs.getString("id"));
			author.setFirstName(rs.getString("first_name"));
			author.setLastName(rs.getString("last_name"));
			return author;
		}
	}
}
