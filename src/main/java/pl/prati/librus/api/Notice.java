package pl.prati.librus.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.prati.librus.util.Utilities;

public class Notice {
	private static final Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
	
	private String title;
	private String content;
	private String date;
	private String author;
	
	private String hash;

	public Notice(String date, String title, String content, String author) {
		this.date = date;
		this.title = title;
		this.content = content;
		this.author = author;
		computeHash();
	}

	private void computeHash() {
		this.hash = Utilities.sha1(this.toString());
	}

	public Notice(String date, String title, String content, String author, String hash) {
		this.date = date;
		this.title = title;
		this.content = content;
		this.author = author;
		this.hash = hash;
	}
	
	public Notice(Element elem) {
		this.title = elem.select("thead tr td").text();
		Elements lines = elem.select("tr[class^=line] td");
		this.author = lines.first().text();
		this.content = Utilities.extractTextWithNewlines(lines.last()).replaceAll("\\s+\n\\s+", "\n");
		
		Matcher m = datePattern.matcher(lines.eq(1).text());
		if(m.find())
			this.date = m.group();
		
		computeHash();
	}
	
	public static List<Notice> parsePage(Document doc) {
		List<Notice> list = new ArrayList<Notice>();
		for(Element elem: doc.select("form table")) {
			list.add(new Notice(elem));
		}
		
		return list;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
	
	public String getDateString() {
		return date;
	}
	
	public String getHash() {
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("%s\n%s %s\n%s\n%s", hash, date, author, title, content);
	}
}
