package webaccess;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import domain.Links;
import domain.Serie;
import javafx.application.Platform;
import main.MainTVSM;
import ui.optionPane.OptionPanes;
import webaccess.streamingSitesAccess.Bs;
import webaccess.streamingSitesAccess.Deddl;
import webaccess.streamingSitesAccess.Movie4k;
import webaccess.streamingSitesAccess.SerienStream;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;

/*
 * Dieser Klasse hat eine Methode der die Serie übergeben
 * wird. Sie durchsucht die Websites nach meinem Fav streamer. Die Websites
 * klassen geben den Most Fav link zurück. Diese Links werden hier verglichen. Und der Beste wird dann hier geöffnet.
 * 
 * @author Aljoscha
 *
 */

public class MainWebAccessController {
	private Serie serie;
	private Movie4k movie4k;
	private Deddl deddl;
	private SerienStream serienstream;
	private Bs bs;
	private boolean found;
	private boolean tryed;

	public MainWebAccessController(Serie s) {
		this.serie = s;
		this.movie4k = new Movie4k(this.serie);
		this.deddl = new Deddl(this.serie);
		this.serienstream = new SerienStream(this.serie);
		this.bs = new Bs(this.serie);
		this.found = false;
		this.tryed = false;
	}

	public boolean startSerie() {

		HashSet<ErrorTyp> fehler = new HashSet<>();

		try {
			try {
				this.bs.initialize();
				this.found = true;
				openURL(this.bs.getNextEpisodeURL());
				return true;
			} catch (TVShowException e) {
				fehler.add(e.getTyp());
			}
			try {
				this.deddl.initialize();
				this.found = true;
				openURL(this.deddl.getNextEpisodeURL());
				return true;
			} catch (TVShowException e) {
				fehler.add(e.getTyp());
			}
			try {
				this.movie4k.initialize();
				this.found = true;
				openURL(this.movie4k.getNextEpisodeURL());
				return true;
			} catch (TVShowException e) {
				fehler.add(e.getTyp());
			}
			try {
				this.serienstream.initialize();
				this.found = true;
				openURL(this.serienstream.getNextEpisodeURL());
				return true;
			} catch (TVShowException e) {
				fehler.add(e.getTyp());
			}

			if (fehler.contains(ErrorTyp.FAVORITENOTFOUND)) {
				throw new TVShowException(ErrorTyp.FAVORITENOTFOUND);
			}

			if (fehler.contains(ErrorTyp.EPISODENOTFOUND)) {
				throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
			}

			if (fehler.contains(ErrorTyp.SEASONNOTFOUND)) {
				throw new TVShowException(ErrorTyp.SEASONNOTFOUND);
			}

			if (fehler.contains(ErrorTyp.UNKNOWN)) {
				throw new TVShowException(ErrorTyp.UNKNOWN);
			}

		} catch (TVShowException e1) {

			switch (e1.getTyp()) {
			case FAVORITENOTFOUND:
				Platform.runLater(() -> {
					OptionPanes.showConfirm("Keine Hoster",
							"Ich habe leider keinen Hoster gefunden der deinen Favoriten entspricht...",
							"Deine Ansprüche sind einfach zu hoch.");
				});
				break;
			case EPISODENOTFOUND:
				if (!this.tryed) {
					this.tryed = true;

					int seas = this.serie.getSeason();
					int epi = this.serie.getEpisode() - 1;

					this.serie.setSeason(seas + 1);
					this.serie.setEpisode(1);
					if (this.startSerie()) {
						return true;
					} else {
						this.serie.setSeason(seas);
						this.serie.setEpisode(epi);
					}
					this.tryed = false;
				} else {
					Platform.runLater(() -> {
						OptionPanes.showConfirm("Nicht gefunden", "Ich konnte leider keine weitere Staffel finden...",
								"Viellicht gibt es bald neue!");
					});
				}

				break;
			case SEASONNOTFOUND:
				Platform.runLater(() -> {
					OptionPanes.showConfirm("Nicht gefunden", "Ich konnte leider keine weiteren Folgen finden...",
							"Viellicht gibt es bald neue!");
				});
				break;
			case UNKNOWN:
				break;
			default:
				break;
			}

		}

		return false;

	}

	public Deddl getDeddl() {
		this.deddl = new Deddl(this.serie);
		try {
			this.deddl.initialize();
		} catch (TVShowException e) {

		}
		return this.deddl;
	}

	public Movie4k getMovie4k() {
		this.movie4k = new Movie4k(this.serie);
		try {
			this.movie4k.initialize();
		} catch (TVShowException e) {
		}
		return this.movie4k;
	}

	public Bs getBs() {
		this.bs = new Bs(this.serie);
		try {
			this.bs.initialize();
		} catch (TVShowException e) {
		}
		return this.bs;
	}

	public SerienStream getSs() {
		this.serienstream = new SerienStream(this.serie);
		try {
			this.serienstream.initialize();
		} catch (TVShowException e) {
		}
		return this.serienstream;
	}

	/*
	 * Öffnet übergebene URL
	 */
	public static void openURL(URL url) {
		if (MainTVSM.copyallurlstoclipboard) {
			StringSelection stringSelection = new StringSelection(url.toString());
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
			return;
		}
		try {
			Desktop.getDesktop().browse(url.toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openURL(String url) {
		if (MainTVSM.copyallurlstoclipboard) {
			StringSelection stringSelection = new StringSelection(url);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
			return;
		}
		try {
			URL urle = new URL(url);
			Desktop.getDesktop().browse(urle.toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getFound() {
		return this.found;
	}

	/*
	 * Durchsucht seiten und google nach links zu moviek4k und etc
	 */
	public static Links searchLinks(String name) {
		name = name.toLowerCase();
		Links l = new Links();
		String url;

		if ((url = Bs.searchShow(name)) != null) {
			try {
				l.setBs(new URL(url));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if ((url = Deddl.searchShow(name)) != null) {
			try {
				l.setDeddl(new URL(url));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if ((url = Movie4k.searchShow(name)) != null) {
			try {
				l.setMovie4k(new URL(url));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if ((url = SerienStream.searchShow(name)) != null) {
			try {
				l.setSerienstream(new URL(url));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (l.getBs() == null && l.getDeddl() == null && l.getMovie4k() == null && l.getSerienstream() == null) {
			return null;
		}

		return l;
	}

}
