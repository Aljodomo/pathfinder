package webaccess.additionalInfos;

import java.io.IOException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.Serie;

public class InformationGetterSJ {

	private Serie serie;
	int seasons;
	int[] episodes;
	Document doc;
	Elements eles;

	public static void main(String[] args) {
		InformationGetterSJ ig = new InformationGetterSJ(null);
		System.out.println(ig);
	}

	public InformationGetterSJ(Serie s) {
		this.serie = s;
		this.inizializie();
	}

	public String test() {
		try {
			return serie.getName() + " S:" + serie.getSeason() + "/" + this.seasons + " E:" + serie.getEpisode() + "/"
					+ this.episodes[serie.getSeason() - 1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void inizializie() {
		String url = this.getMainURL();
		try {
			this.doc = Jsoup.connect(url).get();

			this.seasons = iniSeasonsNumber();
			episodes = new int[this.seasons];
			for (int i = 1; i <= this.seasons; i++) {
				episodes[i - 1] = iniEpisodeNumber(i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getMainURL() {
		try {
			String searchURL = "http://www.serienjunkies.de/tags/"
					+ URLEncoder.encode(this.serie.getName().toLowerCase().trim(), "UTF-8") + "/serie/";

			Document d = Jsoup.connect(searchURL).get();
			Element e = d.select("div[class=pad10]").get(0);
			Elements el = e.select("div[class=sminibox");
			Element e1 = el.get(0);
			Element e2 = e1.select("a").get(0);
			String url = e2.attr("href");
			url = "http://www.serienjunkies.de" + url.substring(1, url.length() - 1) + "alle-serien-staffeln.html";
			return url;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private int iniSeasonsNumber() throws Exception {
		this.eles = doc.select("table[id=epsum]").get(0).select("tbody").get(0).children();
		return this.eles.size() - 1;
	}

	private int iniEpisodeNumber(int seas) throws Exception {
		return Integer.parseInt(this.eles.get(seas).select("td").get(1).text());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.seasons; i++) {
			sb.append(i + 1);
			sb.append(" :" + episodes[i]);
			sb.append("\n");
		}
		return sb.toString();
	}

}