package com.euromoby.books;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BooksCrawler {

	public static final void main(String args[]) {

		String fileName = "c:/books/book.fb2";

		Pattern p = Pattern.compile(".*(<description>.*</description>).*", Pattern.DOTALL);		
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			StringBuffer sb = new StringBuffer();
			boolean descriptionFound = false;
			for(String line; (line = br.readLine()) != null; ) {
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

			Matcher m =  p.matcher(sb.toString());
			if (m.matches()) {
				
				String descriptionXml = m.group(1);
				Document doc = Jsoup.parse(descriptionXml);
				
				Elements authorTags = doc.select("description title-info author");
				Element author = authorTags.get(0);
				System.out.println(author.toString());
				
				String firstName = author.select("first-name").get(0).text();
				System.out.println(firstName.toString());

				
				//Elements descriptionTags = doc.getElementsByTag("description");
				//Element description = descriptionTags.get(0);
				//System.out.println(description.toString());
				
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
