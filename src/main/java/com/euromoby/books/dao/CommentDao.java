package com.euromoby.books.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.euromoby.books.model.Comment;

@Component
public class CommentDao {
	private DataSource dataSource;

	private static final CommentRowMapper ROW_MAPPER = new CommentRowMapper();

	@Autowired
	public CommentDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Comment find(Integer bookId, String login, Long created) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return jdbcTemplate.queryForObject("select * from comment where book_id = ? AND login = ? AND created = ?", ROW_MAPPER, bookId, login, created);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void save(Comment comment) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("insert into comment(book_id, login, comment, grade, created) values (?,?,?,?,?)", comment.getBookId(), comment.getLogin(), comment.getComment(), comment.getGrade(), comment.getCreated());
	}

	static class CommentRowMapper implements RowMapper<Comment> {
		@Override
		public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Comment comment = new Comment();
			comment.setId(rs.getInt("id"));
			comment.setBookId(rs.getInt("book_id"));
			comment.setLogin(rs.getString("login"));
			comment.setComment(rs.getString("comment"));
			comment.setGrade(rs.getInt("grade"));
			comment.setCreated(rs.getLong("created"));			
			return comment;
		}
	}
}
