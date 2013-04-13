package pl.prati.librus.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Librus {
	/** Default base address. */
	public static final String DEFAULT_LIBRUS_URL = "https://dziennik.librus.pl/";

	private static final String LOGIN_PAGE = "loguj";
	private static final String LOGOUT_PAGE = "wyloguj";
	private static final String SESSION_COOKIE = "DZIENNIKSID";
	
	private static final String SUCCESS_LOCATION = "/uczen_index";
	private static final Pattern FAILURE_LOCATION = Pattern.compile("/blad_logowania/(\\d+)");

	private static final String LOGIN_FIELD = "login";
	private static final String PASSWORD_FIELD = "passwd";
	
	private static final String NOTICES_PAGE = "ogloszenia";

	private String baseUrl;

	private String sessionCookie;

	private String realName;
	
	public Librus(String baseUrl) {
		if(!baseUrl.endsWith("/"))
			baseUrl += "/";
		
		this.baseUrl = baseUrl;
	}
	
	public Librus() {
		this(DEFAULT_LIBRUS_URL);
	}

	public void login(String login, String passwd) throws IOException {
		Response res = doConnect(LOGIN_PAGE).execute();
		sessionCookie = res.cookie(SESSION_COOKIE);
		
		if(sessionCookie == null)
			throw new IOException("Did not receive session cookie");
		
		Document loginPage = res.parse();
		
		Map<String, String> formFields = new HashMap<String, String>();
		for(Element elem: loginPage.select("form input"))
			formFields.put(elem.attr("name"), elem.val());
		
		formFields.put(LOGIN_FIELD, login);
		formFields.put(PASSWORD_FIELD, passwd);
		
		formFields.put("czy_js", "1");
		formFields.put("jest_captcha", "1");
		
		res = doConnect(LOGIN_PAGE).data(formFields).method(Method.POST).followRedirects(false).execute();
		String location = res.header("Location");
		if(location == null)
			throw librusError();
		if(location.equals(SUCCESS_LOCATION)) {
			Document doc = doConnect(location).get();
			Elements user = doc.select("div#user-section b");
			if(user.isEmpty())
				throw librusError();
			realName = user.text();
		} else {
			Matcher m = FAILURE_LOCATION.matcher(location);
			if(m.matches()) {
				String error = doConnect(location).get().select("div.warning p").text();
				throw new IOException("Login error: " + error + " (" + m.group(1) + ")");
			} else {
				throw librusError();
			}
		}
	}

	/* indicate an incompatibile change in page structure. */
	private IOException librusError() {
		return new IOException("Something's wrong with librus page.");
	}
	
	public void logout() throws IOException {
		Response execute = doConnect(LOGOUT_PAGE).followRedirects(false).execute();
		if(execute.statusCode() != 302)
			throw librusError();
	}
	
	public String getRealName() {
		return realName;
	}
	
	protected Connection doConnect(String page) {
		Connection con = Jsoup.connect(baseUrl + page);
		con.userAgent("Mozilla/4.0 (compatibile)");
		if(sessionCookie != null)
			con.cookie(SESSION_COOKIE, sessionCookie);
		con.cookie("TestCookie", "1");
		return con;
	}
	
	public List<Notice> getNotices() throws IOException {
		Document doc = doConnect(NOTICES_PAGE).get();
		return Notice.parsePage(doc);
	}
}
