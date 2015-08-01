package com.euromoby.books.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.euromoby.books.model.Genre;

@Component
public class GenreDao {
	private DataSource dataSource;

	private static final GenreRowMapper ROW_MAPPER = new GenreRowMapper();

	@Autowired
	public GenreDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Genre findById(String id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return jdbcTemplate.queryForObject("select * from genre where id = ?", ROW_MAPPER, id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void save(Genre genre) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("insert into genre(id, title) values (?,?)", genre.getId(), genre.getTitle());
	}

	static class GenreRowMapper implements RowMapper<Genre> {
		@Override
		public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
			Genre genre = new Genre();
			genre.setId(rs.getString("id"));
			genre.setTitle(rs.getString("title"));
			return genre;
		}
	}
}
