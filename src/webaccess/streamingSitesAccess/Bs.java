package webaccess.streamingSitesAccess;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.Serie;
import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;

public class Bs extends StreamingSite {

	public static final String mainPage = "https://bs.to/";

	public Bs(Serie s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void generateStreamLink() throws TVShowException, IOException {
		if (this.serie.getLinks().getBs() == null) {
			throw new TVShowException(ErrorTyp.UNKNOWN);
		}
		this.streams = new ArrayList<ArrayList<String>>();

		String url = this.getUrl(this.serie.getSeason(), this.serie.getEpisode());

		Document doc = Jsoup.connect(url).get();

		this.getStreams(doc);

		this.nextEpisodeURL = new URL(this.checkStreams(StreamingSitesEnum.BS));

		// TODO Auto-generated method stub

	}

	private String getUrl(int season, int episode) throws TVShowException {

		String surl = this.getSeasonURL(this.serie.getSeason());

		String url = this.getEpisodeURL(surl, this.serie.getEpisode());

		return url;
	}

	private String getSeasonURL(int season) {
		return this.serie.getLinks().getBsString() + "/" + this.serie.getSeason();
	}

	private String getEpisodeURL(String surl, int episode) throws TVShowException {
		Document doc;
		String url;
		try {
			doc = Jsoup.connect(surl).get();
			if (doc.toString().contains("Staffel nicht gefunden")) {
				throw new TVShowException(ErrorTyp.SEASONNOTFOUND);
			}
		} catch (IOException e) {
			throw new TVShowException(ErrorTyp.SEASONNOTFOUND);
		}

		try {
			Element epi = doc.select("table[class=episodes]").get(0).select("tbody").get(0).select("tr")
					.get(episode - 1);
			url = mainPage + epi.select("td").get(0).select("a").get(0).attr("href");

		} catch (Exception e) {
			throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
		}

		return url;
	}

	public static String getHosterURLFromBsURL(String url) {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			url = doc.select("a[class=hoster-player]").get(0).attr("href");
			return url;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void getStreams(Document d) throws TVShowException {
		Elements hoster = d.select("ul[class=hoster-tabs top]").get(0).select("li");

		for (Element e : hoster) {
			ArrayList<String> stream = new ArrayList<String>();
			e = e.select("a").get(0);
			String name = e.text().trim();
			String url = mainPage + e.attr("href");
			stream.add(name);
			stream.add(url);
			this.streams.add(stream);
		}

	}

	public static String searchShow(String name) {
		String userAgent = "StreamingBot 0.8 (+http://example.com/bot)";

		Element alleserien = null;
		try {
			alleserien = Jsoup.connect("https://bs.to/andere-serien").userAgent(userAgent).get()
					.select("div[id=seriesContainer]").get(0);
			Elements serien = alleserien.select("a[title*=" + name + "]");

			int nameL = name.length();

			if (nameL != 0) {

				int index = -1;
				int abs = 1000;
				for (int i = 0; i < serien.size(); i++) {
					Element s = serien.get(i);

					String url = s.attr("href");
					if (url.contains("-UK") || url.contains("-US")) {
						continue;
					}

					int length = s.attr("title").length();
					if (length - nameL < abs) {
						index = i;
						abs = length - nameL;
					}
				}
				Element link = serien.get(index);
				return Bs.mainPage + link.attr("href");
			}

			return null;
		} catch (Exception e) {

		}

		return null;

	}

}
