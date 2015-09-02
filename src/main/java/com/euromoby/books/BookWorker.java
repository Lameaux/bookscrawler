package com.euromoby.books;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.euromoby.books.http.HttpClientProvider;
import com.euromoby.books.model.Author;
import com.euromoby.books.model.Book;
import com.euromoby.books.model.Comment;
import com.euromoby.books.model.Grade;
import com.euromoby.books.utils.PathUtils;
import com.euromoby.books.utils.SeoUtils;
import com.euromoby.books.utils.TextUtils;
import com.euromoby.books.utils.TranslitUtils;
import com.euromoby.books.utils.ZipUtils;

public class BookWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(BookWorker.class);
	private static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding=\"([^\"]+)\".*\\?>.*");
	private static final Pattern DIV_NEWANN_PATTERN = Pattern.compile("<div id='newann'[^>]+>.*?<b><a[^>]+>(.*?)</a></b>.*?([0-9]{2}-[0-9]{2}-[0-9]{4})<br>(.*?)<hr>.*?</div>", Pattern.DOTALL);
	private static final String LIB_RUS_EC_URL = "http://lib.rus.ec/b/";

	private BooksManager booksManager;
	private HttpClientProvider httpClientProvider;
	private String fileName;
	private Integer id;
	private String destination;

	public BookWorker(BooksManager booksManager, HttpClientProvider httpClientProvider, String fileName, Integer id, String destination) {
		this.booksManager = booksManager;
		this.httpClientProvider = httpClientProvider;
		this.fileName = fileName;
		this.id = id;
		this.destination = destination;
	}

	@Override
	public void run() {

		log.info("Processing {}", fileName);
		Book bookExists = booksManager.findById(id);
		if (bookExists != null) {
			try {
				grabBookData(bookExists);
			} catch (Exception e) {
				log.error("Unable to grab comments " + id, e);
			}			
			return;
		}
		
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

			String descriptionXml = TextUtils.readTagContent(fileName, encoding, "description", "", 0);
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

			String coverImageId = xpath.evaluate("/description/title-info/coverpage/image/@href", document);
			if (coverImageId.startsWith("#")) {
				book.setHasImage(true);
			}

			booksManager.save(book);

			try {
				grabBookData(book);
			} catch (Exception e) {
				log.error("Unable to grab comments " + id, e);
			}

			if (!book.isRemoved()) {			
				// copy fb2
				File fb2Destination = new File(destination, PathUtils.generatePath("fb2", id, ".fb2"));
				fb2Destination.getParentFile().mkdirs();
				FileUtils.copyFile(new File(fileName), fb2Destination);
	
				TextUtils textUtils = new TextUtils();
				String bookText = textUtils.readBookContent(fileName, encoding, 0, 25);
				byte[] zipped = ZipUtils.zipBytes(id + ".txt", bookText.getBytes("utf-8"));
				
				File zipDestination = new File(destination, PathUtils.generatePath("zip", id, ".zip"));
				zipDestination.getParentFile().mkdirs();
				FileUtils.writeByteArrayToFile(zipDestination, zipped);
			}			
			
			if (coverImageId.startsWith("#")) {
				coverImageId = coverImageId.substring(1);
				String base64Data = TextUtils.readTagContent(fileName, encoding, "binary", coverImageId, 1);
				if (base64Data != null) {
					File jpgDestination = new File(destination, PathUtils.generatePath("jpg", id, ".jpg"));
					jpgDestination.getParentFile().mkdirs();
					FileUtils.writeByteArrayToFile(jpgDestination, Base64.decodeBase64(base64Data));
				}
			}

		} catch (Exception e) {
			log.error("Error processing book " + fileName, e);
		}

	}

	private void grabBookData(Book book) throws Exception {

		Integer id = book.getId();
		
		if (booksManager.commentsForBookExists(id)) {
			return;
		}
		
		log.info("Processing data for " + id);
		
		byte[] content = loadUrl(LIB_RUS_EC_URL + id);
		String page = new String(content, "UTF-8");

		if (page.contains("Эта книга удалена")) {
			book.setRemoved(true);
			booksManager.update(book);
		}
		
		Matcher m = DIV_NEWANN_PATTERN.matcher(page);
		boolean hasComments = false;
		while (m.find()) {
			hasComments = true;
			Comment comment = new Comment();
			comment.setBookId(id);
			comment.setLogin(m.group(1));
			
			String dateString = m.group(2);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			try {
				Date date = sdf.parse(dateString);
				comment.setCreated(date.getTime());
			} catch (Exception e) {
				comment.setCreated(System.currentTimeMillis());
			}

			String commentText = Jsoup.parse(m.group(3)).text().trim();
			comment.setComment(commentText);
			booksManager.save(comment);
			
			int gradeValue = getGrade(commentText);
			if (gradeValue > 0) {
				Grade grade = new Grade();
				grade.setBookId(comment.getBookId());
				grade.setLogin(comment.getLogin());
				grade.setCreated(comment.getCreated());
				grade.setGrade(gradeValue);
				booksManager.save(grade);
			}
			
		}
		
		if (!hasComments) {
			Comment comment = new Comment();
			comment.setBookId(id);
			comment.setLogin("bookniga");
			comment.setCreated(0L);
			comment.setComment("Вы уже читали эту книгу? Оставьте свой отзыв!");
			booksManager.save(comment);
		}
		
	}

	private int getGrade(String comment) {
		if (comment.contains("Оценка: отлично")) {
			return 5;
		}
		if (comment.contains("Оценка: хорошо")) {
			return 4;
		}
		if (comment.contains("Оценка: неплохо")) {
			return 3;
		}		
		if (comment.contains("Оценка: плохо")) {
			return 2;
		}
		if (comment.contains("Оценка: нечитаемо")) {
			return 1;
		}		
		return 0;
	}
	
	private byte[] loadUrl(String url) throws IOException {

		HttpGet request = new HttpGet(url);
		RequestConfig.Builder requestConfigBuilder = httpClientProvider.createRequestConfigBuilder();
		request.setConfig(requestConfigBuilder.build());
		CloseableHttpResponse response = httpClientProvider.executeRequest(request);
		try {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				EntityUtils.consumeQuietly(response.getEntity());
				throw new IOException(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
			}

			HttpEntity entity = response.getEntity();
			byte[] content = EntityUtils.toByteArray(entity);
			EntityUtils.consumeQuietly(entity);
			return content;
		} finally {
			response.close();
		}
	}

}
