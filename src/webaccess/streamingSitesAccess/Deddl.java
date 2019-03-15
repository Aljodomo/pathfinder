package webaccess.streamingSitesAccess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import domain.Serie;
import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;

public class Deddl extends StreamingSite {

  public static final String mainPage = "http://de.ddl.me/";

  public Deddl(Serie s) {
    super(s);
  }

  @Override
  protected void generateStreamLink() throws TVShowException, IOException {

    if (this.serie.getLinks().getDeddl() == null) {
      throw new TVShowException(ErrorTyp.UNKNOWN);
    }
    this.streams = new ArrayList<ArrayList<String>>();

    String url = null;

    Document doc;

    doc = Jsoup.connect(this.serie.getLinks().getDeddlString()).get();

    this.getStreams(doc);

    url = this.checkStreams(StreamingSitesEnum.DEDDL);

    this.nextEpisodeURL = new URL(url);

  }

  protected void getStreams(Document d) throws TVShowException {

    Elements s;
    Document doc;

    doc = d;

    s = doc.select("script[type=text/javascript]");

    Pattern pa4, pa1, pa5, pa2;
    Matcher ma4, ma1, ma5, ma2;
    String st4, st1 = null, st5, st2;

    int epi = this.serie.getEpisode();
    int sas = this.serie.getSeason();

    pa1 = Pattern.compile("\"nr\":\"" + epi + "\",\"staffel\":\"" + sas + "\",(.*?)\\]\\]\\}\\}");
    ma1 = pa1.matcher(s.get(1).toString());
    if (ma1.find()) {
      st1 = ma1.group(0);
    } else {
      throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
    }
    // System.out.println("1: "+st1);

    pa2 = Pattern.compile("\"links\"(.*?)\\}\\}");
    ma2 = pa2.matcher(st1);
    ma2.find();
    st2 = ma2.group(0);

    String[] links = st2.split("\\]\\],");

    for (String st3 : links) {

      if (st3.contains("\"download\"")) {
        continue;
      }

      if (st3.contains("links")) {
        st3 = st3.substring(8, st3.length());
      }

      // System.out.println("3: "+st3);

      pa4 = Pattern.compile("\"(.*?)\":");
      ma4 = pa4.matcher(st3);
      ma4.find();
      st4 = ma4.group(1);

      // System.out.println("4: "+st4);

      ArrayList<String> slist = new ArrayList<String>();

      slist.add(st4);

      pa5 = Pattern.compile("http(.*?)\",");
      ma5 = pa5.matcher(st3);
      ma5.find();
      st5 = ma5.group(0);

      st5 = st5.replaceAll("\\\\/", "/");
      st5 = st5.substring(0, st5.length() - 2);

      // System.out.println("5: "+st5);

      slist.add(st5);

      this.streams.add(slist);

    }

  }

  public static String searchShow(String name) {

    String google = "http://www.google.com/search?q=";
    String userAgent = "StreamingBot 0.8 by Me";
    Elements links = null;
    String search;
    String url;

    search = "http://de.ddl.me/search_99/?q=" + name.trim().replace(" ", "+");

    Elements searchResults;
    try {
      // Document searchDoc =
      // Jsoup.connect(search).followRedirects(false).userAgent(userAgent).get();
      Document searchDoc = Jsoup.connect(search).get();
      Element searchContent = searchDoc.select("div[id=content]").get(0);
      searchResults = searchContent.select("div[id=view]").get(0).children();
      for (Element result : searchResults) {
        String link;
        link = result.select("a").get(0).attr("href");
        if (result.select("a").get(0).select("span[class=bottomtxt]").get(0).select("span").get(0)
            .text().contains("TV")) {
          url = Deddl.mainPage + link.substring(1);
          return url;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("No show with name " + name + " found on de.ddl.me with internal search.");

    }

    search = name + " serie site:de.ddl.me";

    try {
      links = Jsoup.connect(google + search.trim().replace(" ", "+")).userAgent(userAgent).get()
          .select(".g>.r>a");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for (Element link : links) {
      url = link.absUrl("href");
      try {
        url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (!url.contains("http") && !url.contains("html")) {
        continue; // Ads/news/etc.
      }
      if (!url.contains("de.ddl.me")) {
        continue; // Ads/news/etc.
      }
      if (!url.contains(name.toLowerCase().trim().replace(" ", "-"))) {
        continue; // Ads/news/etc.
      }
      if (url.contains("film")) {
        continue; // Ads/news/etc.
      }
      return url;
    }

    return null;
  }

}
