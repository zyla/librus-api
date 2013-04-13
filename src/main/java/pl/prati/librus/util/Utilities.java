package pl.prati.librus.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jsoup.nodes.Element;

public class Utilities {
	
	public static String sha1(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] bytes = md.digest(str.getBytes("utf-8"));
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (int i = 0; i < bytes.length; i++) {
				sb.append(String.format("%02x", bytes[i]));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError();
		} catch (UnsupportedEncodingException e) {
			throw new InternalError();
		}
	}
	
	private static final String LINE_SEPARATOR = "--------------PLACEHOLDER-----------";

	
	/**
	 * Extracts text from Element, replacing {@code <br>} with newline.
	 */
	public static String extractTextWithNewlines(Element elem) {
		elem.select("br").html(LINE_SEPARATOR);
		return elem.text().replace(LINE_SEPARATOR, "\n");
	}
}
