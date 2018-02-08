package webaccess.constantClasses;

import java.io.Serializable;

public class StreamingSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	public String[] favoriteHoster = new String[3];

	public StreamingSettings(String erster, String zweiter, String dritter) {

		this.favoriteHoster = new String[] { erster, zweiter, dritter };

	}

	public StreamingSettings() {

		this.favoriteHoster = null;

	}

	public void setHoster(String erster, String zweiter, String dritter) {

		this.favoriteHoster = new String[] { erster, zweiter, dritter };

	}

}
