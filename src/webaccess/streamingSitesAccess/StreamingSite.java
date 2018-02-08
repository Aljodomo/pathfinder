package webaccess.streamingSitesAccess;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.nodes.Document;

import domain.Serie;
import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;
import webaccess.testHandeling.TestThread;

public abstract class StreamingSite {

	protected Serie serie;

	protected URL nextEpisodeURL;

	protected ArrayList<ArrayList<String>> streams;

	public StreamingSite(Serie s) {
		this.serie = s;
		this.nextEpisodeURL = null;
	}

	public void initialize() throws TVShowException {
		try {

			this.generateStreamLink();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public URL getNextEpisodeURL() {
		return this.nextEpisodeURL;
	}

	abstract protected void generateStreamLink() throws TVShowException, IOException;

	abstract protected void getStreams(Document d) throws TVShowException;

	public ArrayList<ArrayList<String>> getHoster() {
		return this.streams;
	}

	protected String checkStreams(StreamingSitesEnum streamingSite) throws TVShowException {
		String url = null;
		for (String favorite : this.serie.getFavoritenHosts().favoriteHoster) {
			if (favorite.equals("")) {
				continue;
			}
			for (ArrayList<String> stream : this.streams) {
				if (favorite.toLowerCase().contains(stream.get(0).toLowerCase())
						|| stream.get(0).toLowerCase().contains(favorite.toLowerCase())) {
					TestThread[] tts = new TestThread[stream.size() - 1];
					for (int i = 1; i < stream.size(); i++) {
						tts[i - 1] = new TestThread(stream.get(0).toLowerCase(), stream.get(i), streamingSite);
						tts[i - 1].start();
					}
					for (TestThread tt : tts) {
						try {
							tt.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(tt.testState);
						if (tt.testState == true) {
							url = tt.url;
							break;
						}
					}
					if (url != null) {
						break;
					}
				}
			}
			if (url != null) {
				break;
			}
		}
		if (url == null) {
			throw new TVShowException(ErrorTyp.FAVORITENOTFOUND);
		}
		return url;
	}
}
