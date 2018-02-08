package webaccess.additionalInfos;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import domain.Serie;

public class InformationGetterDEDDL {

	private Serie serie;

	int[] seasons;

	public InformationGetterDEDDL(Serie s) {
		this.serie = s;
		try {
			this.seasons = this.getSeasons();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected int[] getSeasons() throws Exception {
		Document doc = Jsoup.connect(this.serie.getLinks().getMovie4kString()).get();

		Elements seas = doc.select("select[name=season]").get(0).children();

		int[] season = new int[seas.size()];

		for (int i = 0; i < season.length; i++) {
//			season[i] = 1
		}

		return seasons;
	}

}
