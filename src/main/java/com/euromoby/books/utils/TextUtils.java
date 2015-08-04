package com.euromoby.books.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

	public static String readTagContent(String fileName, String encoding, String tag, String id, int group) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding))) {
			StringBuffer sb = new StringBuffer();

			boolean tagFound = false;
			for (String line; (line = br.readLine()) != null;) {

				if (line.contains("<" + tag) && line.contains(id)) {
					tagFound = true;
				}
				if (tagFound) {
					sb.append(line).append("\r\n");
				}
				if (line.contains("</" + tag + ">") && tagFound) {
					break;
				}
			}

			if (tagFound) {
				Pattern p = Pattern.compile(".*(<" + tag + "[^>]*>(.*)</" + tag + ">).*", Pattern.DOTALL);
				Matcher m = p.matcher(sb.toString());
				if (m.matches()) {
					return m.group(group);
				}
			}
			
			return null;
		}
	}

}
