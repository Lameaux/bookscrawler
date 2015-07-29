package com.euromoby.books;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.euromoby.books.model.Author;
import com.euromoby.books.model.Book;

public class BookWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(BookWorker.class);
	private static final Pattern PATTERN = Pattern.compile(".*(<description>.*</description>).*", Pattern.DOTALL);

	private String fileName;

	public BookWorker(String fileName) {
		this.fileName = fileName;
	}

	private String getSingleValue(Document doc, String selector) {
		Elements tags = doc.select(selector);
		if (!tags.isEmpty()) {
			return tags.get(0).text();
		}
		return null;
	}

	private List<String> getListValue(Document doc, String selector) {
		List<String> list = new ArrayList<String>();
		Elements tags = doc.select(selector);
		for (Element tag : tags) {
			list.add(tag.text());
		}
		return list;
	}

	@Override
	public void run() {

		log.debug("Processing {}", fileName);

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			StringBuffer sb = new StringBuffer();
			boolean descriptionFound = false;
			for (String line; (line = br.readLine()) != null;) {
				if (line.contains("<description>")) {
					descriptionFound = true;
				}
				if (descriptionFound) {
					sb.append(line).append("\r\n");
				}
				if (line.contains("</description>")) {
					break;
				}
			}

			Matcher m = PATTERN.matcher(sb.toString());
			if (m.matches()) {

				String descriptionXml = m.group(1);
				Document doc = Jsoup.parse(descriptionXml);

				Book book = new Book();

				Author author = new Author();
				author.setFirstName(getSingleValue(doc, "description title-info author first-name"));
				author.setMiddleName(getSingleValue(doc, "description title-info author middle-name"));
				author.setLastName(getSingleValue(doc, "description title-info author last-name"));
				book.setAuthor(author);

				book.setTitle(getSingleValue(doc, "description title-info book-title"));
				book.setAnnotation(getListValue(doc, "description title-info annotation p"));
				book.setGenres(getListValue(doc, "description title-info genre"));

				book.setPublisher(getSingleValue(doc, "description publish-info publisher"));
				book.setYear(getSingleValue(doc, "description publish-info year"));
				book.setIsbn(getSingleValue(doc, "description publish-info isbn"));

				log.info(book.toString());

			} else {
				log.warn("<description> not found in {}", fileName);
			}
		} catch (Exception e) {

		}

	}

}
