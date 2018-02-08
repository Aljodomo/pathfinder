package webaccess.testHandeling;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ErrorCheck {

	public static boolean check(String name, String url) {
		try {

			if (name.contains("streamcloud")) {
				return streamcloudcheck(url);
			}

			if (url.contains("movie4k.to")) {
				return stream4kcheck(url);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}

	private static boolean streamcloudcheck(String url) throws IOException {

		Document doc = Jsoup.connect(url).get();

		if (doc.toString().contains("could not be found")) {
			return false;
		} else {
			return true;
		}

	}

	private static boolean stream4kcheck(String url) throws IOException {

		Document doc = Jsoup.connect(url).get();

		Elements e = doc.select("center");

		for (Element ele : e) {

			if (ele.toString().contains("webdrive")) {
				return true;
			} else {
				return false;
			}
		}

		return false;

	}

}
