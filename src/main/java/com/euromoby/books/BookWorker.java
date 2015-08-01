package com.euromoby.books;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
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
import org.springframework.util.StringUtils;

import com.euromoby.books.model.Author;
import com.euromoby.books.model.Book;
import com.euromoby.books.model.Genre;
import com.euromoby.books.utils.SeoUtils;
import com.euromoby.books.utils.TranslitUtils;

public class BookWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(BookWorker.class);
	private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(".*(<description>.*</description>).*", Pattern.DOTALL);
	private static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding=\"([^\"]+)\".*\\?>");

	private BooksManager booksManager;
	private String fileName;
	private Integer id;

	public BookWorker(BooksManager booksManager, String fileName, Integer id) {
		this.booksManager = booksManager;
		this.fileName = fileName;
		this.id = id;
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
		String encoding = "utf-8";
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = br.readLine();
			if (line != null) {
				Matcher m = ENCODING_PATTERN.matcher(line);
				if (m.matches()) {
					encoding = m.group(1);
				}				
			}
		} catch (Exception e) {
			log.error("Unable to read xml header", e);
			return;
		}
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding))) {
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

			Matcher m = DESCRIPTION_PATTERN.matcher(sb.toString());
			if (m.matches()) {

				String descriptionXml = m.group(1);
				Document doc = Jsoup.parse(descriptionXml);

				Book book = new Book();
				book.setId(id);

				Author author = new Author();
				
				String firstName = getSingleValue(doc, "description title-info author first-name");
				String middleName = getSingleValue(doc, "description title-info author middle-name");
				if (middleName != null && !middleName.trim().isEmpty()) {
					firstName += " " + middleName;
				}
				author.setFirstName(firstName);
				
				author.setLastName(getSingleValue(doc, "description title-info author last-name"));
				String authorId = author.getLastName() + " " + author.getFirstName();
				author.setId(SeoUtils.toPrettyURL(TranslitUtils.toTranslit(authorId)));
				book.setAuthor(author);

				String title = getSingleValue(doc, "description title-info book-title");
				book.setTitle(title);
				book.setUrl(SeoUtils.toPrettyURL(TranslitUtils.toTranslit(title)));
				
				List<String> annotations = getListValue(doc, "description title-info annotation p");
				book.setAnnotation(StringUtils.arrayToDelimitedString(annotations.toArray(), " "));
				
				List<Genre> genres = new ArrayList<Genre>();
				List<String> genreIds = getListValue(doc, "description title-info genre");
				for (String genreId : genreIds) {
					Genre genre = new Genre();
					genre.setId(genreId.replace("_", "-"));
					genre.setTitle(StringUtils.capitalize(genreId.replace("_", " ")));
					genres.add(genre);
				}
				book.setGenres(genres);

				book.setPublisher(getSingleValue(doc, "description publish-info publisher"));
				book.setYear(getSingleValue(doc, "description publish-info year"));
				book.setIsbn(getSingleValue(doc, "description publish-info isbn"));

				booksManager.save(book);

			} else {
				log.warn("<description> not found in {}", fileName);
			}
		} catch (Exception e) {
			log.error("Error processing book " + fileName, e);
		}

	}

}
