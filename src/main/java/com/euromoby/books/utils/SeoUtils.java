package com.euromoby.books.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class SeoUtils {
	public static String toPrettyURL(String string) {
	    return Normalizer.normalize(string.toLowerCase(), Form.NFD)
	        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	        .replaceAll("[^\\p{Alnum}]+", "-");
	}
}
