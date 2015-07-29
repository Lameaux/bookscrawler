package com.euromoby.books;


public class BooksCrawler {

	public static final void main(String args[]) {

		String fileName = "c:/books/book.fb2";
		
		BookWorker bookWorker = new BookWorker(fileName);
		bookWorker.run();

	}
	
}
