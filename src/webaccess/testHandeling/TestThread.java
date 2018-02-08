package webaccess.testHandeling;

import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.streamingSitesAccess.Bs;
import webaccess.streamingSitesAccess.Movie4k;

public class TestThread extends Thread {

	public String name;

	public String url;

	public boolean testState;

	public StreamingSitesEnum enu;

	public TestThread(String name, String url, StreamingSitesEnum site) {
		this.name = name;
		this.url = url;
		this.enu = site;
	}

	public void convert4kURL() throws Exception {
		this.url = Movie4k.getStreamingURL(this.url);
	}

	public void convertBsURL() throws Exception {
		this.url = Bs.getHosterURLFromBsURL(this.url);
	}

	@Override
	public void run() {

		System.out.println("Check");

		try {

			switch (this.enu) {
			case MOVIEK4K:
				this.convert4kURL();
				break;
			case DEDDL:
				break;
			case SERIENSTREAM:
				// Response r =
				// Jsoup.connect(url).followRedirects(true).execute();
				// url = r.url().toString();
				break;
			case BS:
				this.convertBsURL();
				break;
			default:
				break;
			}

			this.testState = ErrorCheck.check(this.name, this.url);

		} catch (Exception e) {

		}

	}
}
