package com.euromoby.books.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.euromoby.books.model.Grade;

@Component
public class GradeDao {
	private DataSource dataSource;

	private static final GradeRowMapper ROW_MAPPER = new GradeRowMapper();

	@Autowired
	public GradeDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Grade find(Integer bookId, String login) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return jdbcTemplate.queryForObject("select * from grade where book_id = ? AND login = ?", ROW_MAPPER, bookId, login);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Grade> findByBookId(Integer bookId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query("select * from grade where book_id = ?", ROW_MAPPER, bookId);
	}	
	
	public void save(Grade grade) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("insert into grade(book_id, login, grade, created) values (?,?,?,?)", grade.getBookId(), grade.getLogin(), grade.getGrade(), grade.getCreated());
	}

	public void recalculateBookRating(Integer bookId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("update book set rating = (select sum(grade)/count(grade) from grade where book_id = ?) where id = ?", bookId, bookId);
	}
	
	static class GradeRowMapper implements RowMapper<Grade> {
		@Override
		public Grade mapRow(ResultSet rs, int rowNum) throws SQLException {
			Grade grade = new Grade();
			grade.setId(rs.getInt("id"));
			grade.setBookId(rs.getInt("book_id"));
			grade.setLogin(rs.getString("login"));
			grade.setGrade(rs.getInt("grade"));
			grade.setCreated(rs.getLong("created"));			
			return grade;
		}
	}
}
