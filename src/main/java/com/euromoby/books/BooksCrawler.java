package com.euromoby.books;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BooksCrawler {

	private static final Logger log = LoggerFactory.getLogger(BooksCrawler.class);

	public static final void main(String args[]) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
		BooksProcessor booksProcessor = context.getBean(BooksProcessor.class);
		try {
			log.info("Starting processing");
			booksProcessor.startProcessing();
		} catch (IOException e) {
			log.error("Processing failed", e);
		} finally {
			context.close();
			log.info("Done");
		}
	}

}
