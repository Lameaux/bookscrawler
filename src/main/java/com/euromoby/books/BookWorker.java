package com.euromoby.books;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.euromoby.books.model.Author;
import com.euromoby.books.model.Book;
import com.euromoby.books.utils.SeoUtils;
import com.euromoby.books.utils.TextUtils;
import com.euromoby.books.utils.TranslitUtils;

public class BookWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(BookWorker.class);
	private static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding=\"([^\"]+)\".*\\?>");

	private BooksManager booksManager;
	private String fileName;
	private Integer id;

	public BookWorker(BooksManager booksManager, String fileName, Integer id) {
		this.booksManager = booksManager;
		this.fileName = fileName;
		this.id = id;
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

		try {

			String descriptionXml = TextUtils.readTagContent(fileName, encoding, "description", "", 1);
			if (descriptionXml == null) {
				log.warn("Description not found for book " + fileName);
				return;
			}

			Book book = new Book();
			book.setId(id);

			Author author = new Author();

			InputSource source = new InputSource(new StringReader(descriptionXml));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document document = db.parse(source);

			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();

			String firstName = xpath.evaluate("/description/title-info/author/first-name", document);
			String middleName = xpath.evaluate("/description/title-info/author/middle-name", document);
			String lastName = xpath.evaluate("/description/title-info/author/last-name", document);

			if (middleName != null && !middleName.trim().isEmpty()) {
				firstName += " " + middleName;
			}
			author.setFirstName(firstName);
			author.setLastName(lastName);
			String authorId = author.getLastName() + " " + author.getFirstName();
			author.setId(SeoUtils.toPrettyURL(TranslitUtils.toTranslit(authorId)));
			book.setAuthor(author);

			String title = xpath.evaluate("/description/title-info/book-title", document);
			book.setTitle(title);
			book.setUrl(SeoUtils.toPrettyURL(TranslitUtils.toTranslit(title + " " + authorId)));

			String annotation = xpath.evaluate("/description/title-info/annotation", document);
			annotation = annotation.replaceAll("\\s+", " ").trim();
			book.setAnnotation(annotation);

			book.setLang(xpath.evaluate("/description/title-info/lang", document));
			book.setGenre(xpath.evaluate("/description/title-info/genre", document));

			book.setPublisher(xpath.evaluate("/description/publish-info/publisher", document));
			book.setYear(xpath.evaluate("/description/publish-info/year", document));
			book.setIsbn(xpath.evaluate("/description/publish-info/isbn", document));

			booksManager.save(book);
			
			String coverImageId = xpath.evaluate("/description/title-info/coverpage/image/@href", document);
			if (coverImageId.startsWith("#")) {
				coverImageId = coverImageId.substring(1);
				String base64Data = TextUtils.readTagContent(fileName, encoding, "binary", coverImageId, 2);
				String coverFileName = fileName.replace(".fb2", ".jpg");
				FileUtils.writeByteArrayToFile(new File(coverFileName), Base64.decodeBase64(base64Data));				
			}

			TextUtils textUtils = new TextUtils();
			String bookText = textUtils.readBookContent(fileName, encoding, 0, 50);
			String pageFileName = fileName.replace(".fb2", ".txt");
			FileUtils.writeByteArrayToFile(new File(pageFileName), bookText.getBytes(encoding));			
			
		} catch (Exception e) {
			log.error("Error processing book " + fileName, e);
		}

	}

}
