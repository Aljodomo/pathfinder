package webaccess.streamingSitesAccess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.Serie;
import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;

public class Movie4k extends StreamingSite {

	public static final String mainPage = "http://movie4k.to/";

	public Movie4k(Serie s) {
		super(s);
	}

	/**
	 * Nimmt die allgemeine URL und öffnet die URL der Episode. Gibt dann den
	 * Link zur besten StreamingSite zurück im abgleich zu den Favs.
	 * 
	 * @param l
	 *            Link
	 * @param s
	 *            Season
	 * @param e
	 *            Episode
	 * @return Die URL zum StreamingSite
	 */
	@Override
	protected void generateStreamLink() throws TVShowException, IOException {

		if (this.serie.getLinks().getMovie4k() == null) {
			throw new TVShowException(ErrorTyp.UNKNOWN);
		}
		this.streams = new ArrayList<ArrayList<String>>();

		String url = null;
		Document d;

		d = getEpisodeDOC();
		getStreams(d);
		url = this.checkStreams(StreamingSitesEnum.MOVIEK4K);

		this.nextEpisodeURL = new URL(url);
	}

	/**
	 * Gibt die URL der Episode sas,epi zurück.
	 * 
	 * @param url
	 * @param sas
	 * @param epi
	 * @return Link zur Folge auf der Website Movie4k.to
	 * @throws Exception
	 */
	private Document getEpisodeDOC() throws TVShowException {
		String url = this.serie.getLinks().getMovie4k().toString();
		Connection c = null;
		Document doc = null;
		Elements episods = null;
		Element e = null;
		c = Jsoup.connect(url);
		try {
			doc = c.get();
		} catch (IOException e1) {
			throw new TVShowException(ErrorTyp.UNKNOWN);
		}
		try {
			episods = doc.getElementById("episodediv" + this.serie.getSeason()).child(0).child(0).children();
		} catch (Exception e2) {
			throw new TVShowException(ErrorTyp.SEASONNOTFOUND);
		}
		try {
			e = episods.get(this.serie.getEpisode());
		} catch (Exception e2) {
			throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
		}
		String code = e.attr("value");
		try {
			return Jsoup.connect(mainPage + code).get();
		} catch (IOException e1) {
			throw new TVShowException(ErrorTyp.UNKNOWN);
		}
	}

	/**
	 * Gibt alle Streams einer Moviek4k Episode als ArrayList zurück. [0] = name
	 * und komische zeichen [1] = link
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected void getStreams(Document d) throws TVShowException {
		Elements streamsEles0, streamsEles1, streamsEles2;
		Document doc;
		doc = d;
		streamsEles0 = doc.select("div[id=menu]");
		streamsEles0 = streamsEles0.select("tbody");
		// Druchsucht die scipts nach streams
		streamsEles1 = streamsEles0.select("script");
		for (Element e : streamsEles1) {
			ArrayList<String> slist = new ArrayList<String>();
			Pattern linkpattern, namepattern;
			Matcher linkmatcher, namematcher;
			namepattern = Pattern.compile("&nbsp;(.*?)<");
			namematcher = namepattern.matcher(e.toString());
			if (namematcher.find()) {
				namematcher.find();
				slist.add(namematcher.group(1));
			} else {
				break;
			}
			linkpattern = Pattern.compile("href=\\\\\"(.*?)\\\\\">E");
			linkmatcher = linkpattern.matcher(e.toString());
			while (linkmatcher.find()) {
				slist.add(mainPage + linkmatcher.group(1));
			}
			this.streams.add(slist);
		}

		// durchsucht die tr nach links
		streamsEles2 = streamsEles0.select("tr[id=tablemoviesindex2]");
		for (Element e : streamsEles2) {
			ArrayList<String> slist = new ArrayList<String>();
			e = e.child(1).child(0);
			String p = e.text().trim();
			p = p.substring(1, p.length());
			slist.add(p);
			slist.add(mainPage + e.attr("href"));
			this.streams.add(slist);
		}

	}

	public static String getStreamingURL(String d) throws Exception {

		Document doc = Jsoup.connect(d).get();

		Element streamEle = doc.getElementById("maincontent5");

		Elements iframe = doc.select("iframe[src*=player4k]");

		if (iframe.size() > 0) {
			return mainPage + iframe.get(0).attr("src");
		} else {
			return streamEle.child(0).child(7).attr("href");
		}

	}

	public static String searchShow(String name) {

		String google = "https://www.google.de/search?q=";
		String userAgent = "StreamingBot 0.8 (+http://example.com/bot)";
		String search = "http://www.movie4k.to/movies.php?list=search&search=" + name.trim().replace(" ", "+");

		Elements links2 = null;
		try {
			links2 = Jsoup.connect(search).userAgent(userAgent).get().select("div[id=maincontent4]").get(0)
					.select("table[id=tablemoviesindex]").get(0).select("tbody").get(0).select("tr");
			for (Element link : links2) {
				String slink;
				slink = link.select("td").get(0).select("a").get(0).attr("href");
				if (link.select("td").get(0).select("a").get(0).text().contains("(Serie)")) {
					return Movie4k.mainPage + slink;
				}
			}
		} catch (Exception e) {

		}

		search = name + " serie site:movie4k.to";

		Document doc = null;
		try {
			doc = Jsoup.connect(google + search.trim().replace(" ", "+")).userAgent(userAgent).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements links = doc.select(".g>.r>a");

		int count = 0;

		for (Element link : links) {
			if (count >= 3) {
				break;
			}
			String url = link.absUrl("href");
			try {
				url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!url.contains("http") && !url.contains("html")) {
				continue;
			}
			if (!url.contains("movie4k.to")) {
				continue;
			}
			count++;
			if (url.contains("film")) {
				continue;
			}
			if (url.contains("-movie-")) {
				continue;
			}
			return url;
		}

		return null;
	}
}
