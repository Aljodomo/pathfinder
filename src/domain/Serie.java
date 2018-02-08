package domain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Date;

import main.MainTVSM;
import webaccess.MainWebAccessController;
import webaccess.constantClasses.StreamingSettings;

public class Serie implements Serializable, Comparable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int season;
	private int episode;
	private Date latestUse;
	private Links links;
	private StreamingSettings favoritenHosts;
	private transient MainWebAccessController webController;

	public Serie(String name, int season, int episode, Links links) {
		this.name = name;
		this.season = season;
		this.episode = episode;
		this.links = links;
		this.latestUse = new Date();
		this.favoritenHosts = null;
		this.webController = new MainWebAccessController(this);
	}

	public Serie() {
		this.latestUse = new Date();
	}

	@Override
	public int compareTo(Object o) {
		if (this.latestUse.equals(((Serie) o).getLatestUse()) || this.name.equals(((Serie) o).getName())) {
			return 0;
		}
		if (this.latestUse.before(((Serie) o).getLatestUse())) {
			return 1;
		} else {
			return -1;
		}
	}

	public Date getLatestUse() {
		return this.latestUse;
	}

	public void refreshLatestUse() {
		this.latestUse = new Date();
	}

	public boolean startNext() {

		// JOptionPane.showOptionDialog(null, null, "arg2", 0, 0, null, null,
		// null);

		// Werte werden gespeichert falls das abspielen fehlschlägt
		int seas = this.season;
		int epi = this.episode;

		this.setEpisode(this.getEpisode() + 1);

		if (!this.webController.startSerie()) {
			this.episode = epi;
			this.season = seas;
			return false;
		}

		return true;
	}

	public MainWebAccessController getWebCon() {
		return this.webController;
	}

	public void setWebCon(MainWebAccessController webcon) {
		this.webController = webcon;
	}

	public StreamingSettings getFavoritenHosts() {
		if (this.favoritenHosts == null) {
			return MainTVSM.settings;
		} else {
			return this.favoritenHosts;
		}
	}

	public void setFavoritenHosts(StreamingSettings favoritenHosts) {
		this.favoritenHosts = favoritenHosts;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	@Override
	public String toString() {
		return this.name + " S: " + this.season + " E: " + this.episode;
	}

	public void print() {
		System.out.println(this.toString() + " M4K: " + this.getLinks().getMovie4k());
	}

	@Override
	public boolean equals(Object s) {
		if (!(s != null && this != null)) {
			return false;
		}
		if (s.getClass().equals(this.getClass())) {
			if (((Serie) s).getName().equals(this.getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}

	public void edit(String name, int seas, int epi, String movie4k, String deddl, String bs, String ss)
			throws MalformedURLException {
		if (!this.getName().equals(name)) {
			this.setName(name);
		}

		if (this.season != seas) {
			this.season = seas;
		}

		if (this.episode != epi) {
			this.episode = epi;
		}

		this.setLinks(new Links(movie4k, deddl, bs, ss));

	}
}
