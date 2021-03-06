package com.euromoby.books;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.euromoby.books.dao.AuthorDao;
import com.euromoby.books.dao.BookDao;
import com.euromoby.books.dao.CommentDao;
import com.euromoby.books.dao.GradeDao;
import com.euromoby.books.model.Author;
import com.euromoby.books.model.Book;
import com.euromoby.books.model.Comment;
import com.euromoby.books.model.Grade;

@Component
public class BooksManager {
	private static final Logger log = LoggerFactory.getLogger(BooksManager.class);

	@Autowired
	private AuthorDao authorDao;
	@Autowired
	private BookDao bookDao;
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private GradeDao gradeDao;
	
	@Transactional(readOnly=true)	
	public boolean commentExists(Comment comment) {
		Comment commentExists = commentDao.find(comment.getBookId(), comment.getLogin(), comment.getCreated());
		return commentExists != null;		
	}

	@Transactional(readOnly=true)	
	public boolean commentsForBookExists(Integer bookId) {
		List<Comment> comments = commentDao.findByBookId(bookId);
		return !comments.isEmpty();		
	}	
	
	@Transactional(readOnly=true)	
	public Book findById(Integer id) {
		return bookDao.findById(id);
	}

	@Transactional
	public void save(Comment comment) {
		Comment commentExists = commentDao.find(comment.getBookId(), comment.getLogin(), comment.getCreated());
		if (commentExists != null) {
			return;
		}
		commentDao.save(comment);
	}
	
	@Transactional
	public void save(Grade grade) {
		Grade gradeExists = gradeDao.find(grade.getBookId(), grade.getLogin());
		if (gradeExists != null) {
			return;
		}
		gradeDao.save(grade);
		gradeDao.recalculateBookRating(grade.getBookId());
	}

	@Transactional
	public void update(Book book) {
		bookDao.update(book);
	}
	
	@Transactional
	public void save(Book book) {
		
		Book bookExists = bookDao.findById(book.getId());
		if (bookExists != null) {
			return;
		}
		if (book.getUrl() == null || book.getUrl().trim().isEmpty()) {
			return;
		}
		bookDao.save(book);

		if (book.getAuthor().getId() == null || book.getAuthor().getId().trim().isEmpty()) {
			return;
		}		
		
		Author authorExists = authorDao.findById(book.getAuthor().getId());
		if (authorExists == null) {
			try {
				authorDao.save(book.getAuthor());
			} catch (DuplicateKeyException dke){
				// ignore
			} catch (Exception e) {
				log.error("Error saving author", e);
			}
		}

		
		log.info("{}", book);
	}

}
