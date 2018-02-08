package webaccess.streamingSitesAccess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.Serie;
import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;

public class SerienStream extends StreamingSite {

	public static final String mainPage = "https://serienstream.to";

	public SerienStream(Serie s) {
		super(s);
	}

	@Override
	protected void generateStreamLink() throws TVShowException, IOException {
		// SerienStream.mainPage + "/serie/stream/" +
		// this.serie.getName().toLowerCase().replace(" ", "-")
		if (this.serie.getLinks().getSerienstream() == null) {
			throw new TVShowException(ErrorTyp.UNKNOWN);
		}
		this.streams = new ArrayList<ArrayList<String>>();

		String episodeURL = this.serie.getLinks().getSerienstreamString() + "/staffel-" + this.serie.getSeason()
				+ "/episode-" + this.serie.getEpisode();

		Document doc = null;

		try {
			doc = Jsoup.connect(episodeURL).get();
		} catch (IOException e) {
			throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
		}

		this.getStreams(doc);

		String url = this.checkStreams(StreamingSitesEnum.SERIENSTREAM);

		this.nextEpisodeURL = new URL(url);

	}

	@Override
	protected void getStreams(Document d) throws TVShowException {

		Elements slist = d.select("div[class=hosterSiteVideo]").get(0).select("ul[class=row]").get(0)
				.select("li[data-lang-key=1");

		for (Element se : slist) {

			ArrayList<String> stream = new ArrayList<String>();

			stream.add(se.select("div").get(0).select("a[href]").get(0).select("h4").get(0).text());

			stream.add(SerienStream.mainPage + se.select("div").get(0).select("a[href]").get(0).attr("href"));

			this.streams.add(stream);
		}
		// TODO Auto-generated method stub

	}

	public static String searchShow(String name) {

		String google = "https://www.google.de/search?q=";
		String userAgent = "StreamingBot 0.8 (+http://example.com/bot)";

		String search = name + " serie site:serienstream.to";

		Elements links = null;
		try {
			links = Jsoup.connect(google + search.replace(" ", "+").trim()).userAgent(userAgent).get()
					.select(".g>.r>a");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Element link : links) {
			String url = link.absUrl("href");
			try {
				url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!url.contains("http") && !url.contains("html")) {
				continue; // Ads/news/etc.
			}
			if (!url.contains("serienstream.to")) {
				continue; // Ads/news/etc.
			}
			if (!url.contains(name.toLowerCase().trim().replace(" ", "-"))) {
				continue; // Ads/news/etc.
			}
			if (url.contains("film")) {
				continue; // Ads/news/etc.
			}
			if (url.contains("staffel")) {
				url = url.substring(0, url.indexOf("/staffel"));
			}
			return url;
		}
		return null;
	}

}
