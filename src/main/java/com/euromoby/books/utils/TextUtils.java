package com.euromoby.books.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class TextUtils {

	private static Pattern TAG_PATTERN = Pattern.compile("<(/)?([a-z\\-]+)(\\s+[^>]*)?/?>", Pattern.CASE_INSENSITIVE);
	private static Pattern TITLE_PATTERN = Pattern.compile("<title[^>]*>(.*?)</title>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	private List<String> styleSet = new ArrayList<String>();
	private static final List<String> PARTS_LIST = Arrays.asList("epigraph", "annotation", "cite", "poem", "history", "title", "subtitle", "poem", "stanza");
	private static final List<String> P_LIST = Arrays.asList("text-author", "p", "v");
	private static final List<String> SPAN_LIST = Arrays.asList("emphasis", "strikethrough", "sub", "sup", "code");

	public String readBookContent(String fileName, String encoding, int skip, int limit) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding))) {
			StringBuffer html = new StringBuffer();

			boolean bodyFound = false;
			int lineNum = 0;
			for (String line; (line = br.readLine()) != null;) {

				if (line.trim().isEmpty()) {
					continue;
				}

				if (!bodyFound && line.contains("<body")) {
					bodyFound = true;
				}

				if (bodyFound) {
					lineNum++;

					if (lineNum < skip) {
						continue;
					}

					if (lineNum > skip + limit) {
						break;
					}

					int bodyStart = line.indexOf("<body");
					if (bodyStart != -1) {
						line = line.substring(bodyStart);
					}
					int bodyEnd = line.indexOf("</body>");
					if (bodyEnd != -1) {
						line = line.substring(0, bodyEnd);
					}
					
					String transformed = transform(line).trim();
					if (!transformed.isEmpty()) {
						html.append(transformed).append("\r\n");
					}
					
					if (line.contains("</body>")) {
						break;
					}
					
					
				}

			}

			int lastP = html.lastIndexOf("</p>");
			if (lastP != -1) {
				return html.substring(0, lastP + "</p>".length());
			}

			return html.toString();

		}
	}

	public String readBookTitles(String fileName, String encoding) throws IOException {
		List<String> titles = new ArrayList<String>();
		String body = readTagContent(fileName, encoding, "body", "", 1);
		Matcher m = TITLE_PATTERN.matcher(body);
		while (m.find()) {
			String title = m.group(1).replaceAll("<[^>]+>", " ").trim();
			titles.add(title);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<ul>");
		for (String title : titles) {
			sb.append("<li>").append(title).append("</li>");
		}
		sb.append("</ul>");
		return sb.toString();
	}
	
	private String transform(String line) {
		StringBuffer sb = new StringBuffer();
		Matcher m = TAG_PATTERN.matcher(line);
		while (m.find()) {
			String closing = m.group(1);
			String tag = m.group(2);

			// default skip
			String replacement = " ";

			if (tag.equals("empty-line")) {
				replacement = "<br>";
			}

			if (PARTS_LIST.contains(tag)) {
				if (closing != null && closing.equals("/")) {
					styleSet.remove(tag);
				} else {
					styleSet.add(tag);
				}
			}

			if (P_LIST.contains(tag)) {
				if (closing != null && closing.equals("/")) {
					replacement = "</p>";
				} else {
					String classes = StringUtils.arrayToDelimitedString(styleSet.toArray(), " ");
					if (!tag.equals("p")) {
						classes = classes + " " + tag;
					}
					classes = classes.trim();
					if (classes.isEmpty()) {
						replacement = "<p>";
					} else {
						replacement = "<p class=\"" + classes + "\">";
					}

				}
			}

			if (SPAN_LIST.contains(tag)) {
				if (closing != null && closing.equals("/")) {
					replacement = "</span>";
				} else {
					String classes = StringUtils.arrayToDelimitedString(styleSet.toArray(), " ");
					classes = classes.trim();
					if (classes.isEmpty()) {
						replacement = "<span>";
					} else {
						replacement = "<span class=\"" + classes + "\">";
					}

				}
			}

			m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static String readTagContent(String fileName, String encoding, String tag, String id, int group) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding))) {
			StringBuffer sb = new StringBuffer();

			boolean tagFound = false;
			for (String line; (line = br.readLine()) != null;) {

				if (!tagFound && line.contains("<" + tag) && line.contains(id)) {
					tagFound = true;
				}
				if (tagFound) {
					sb.append(line).append("\r\n");
				}
				if (tagFound && line.contains("</" + tag + ">") && line.indexOf("</" + tag + ">") > line.indexOf("<" + tag)) {
					break;
				}
			}

			if (tagFound) {
				Pattern p = Pattern.compile("<" + tag + "[^>]*>(.*)</" + tag + ">", Pattern.DOTALL);
				Matcher m = p.matcher(sb.toString());
				if (m.find()) {
					return m.group(group);
				}
			}

			return null;
		}
	}

}
