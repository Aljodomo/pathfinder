package domain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class Links implements Serializable {
	private static final long serialVersionUID = 1L;
	private URL movie4k;
	private URL deddl;
	private URL bs;
	private URL serienstream;

	public Links() {
		this.movie4k = null;
		this.deddl = null;
		this.bs = null;
		this.serienstream = null;
	}

	public Links(String movie4k, String deddl, String bs, String serienstream) throws MalformedURLException {

		if (movie4k.equals("")) {
			this.movie4k = null;
		} else {
			this.movie4k = new URL(movie4k);
		}

		if (deddl.equals("")) {
			this.deddl = null;
		} else {
			this.deddl = new URL(deddl);
		}

		if (bs.equals("")) {
			this.bs = null;
		} else {
			this.bs = new URL(bs);
		}

		if (serienstream.equals("")) {
			this.serienstream = null;
		} else {
			this.serienstream = new URL(serienstream);
		}

	}

	public Links(URL movie4k, URL deddl, URL bs, URL serienstream) {
		this.movie4k = movie4k;
		this.deddl = deddl;
		this.bs = bs;
		this.serienstream = serienstream;
	}

	public URL getMovie4k() {
		return movie4k;
	}

	public String getMovie4kString() {
		if (this.movie4k == null) {
			return "";
		} else {
			return this.movie4k.toString();
		}
	}

	public void setMovie4k(URL movie4k) {
		this.movie4k = movie4k;
	}

	public URL getDeddl() {
		return deddl;
	}

	public String getDeddlString() {
		if (this.deddl == null) {
			return "";
		} else {
			return this.deddl.toString();
		}
	}

	public void setDeddl(URL deddl) {
		this.deddl = deddl;
	}

	public URL getSerienstream() {
		return serienstream;
	}

	public String getSerienstreamString() {
		if (this.serienstream == null) {
			return "";
		} else {
			return this.serienstream.toString();
		}
	}

	public void setSerienstream(URL serienstream) {
		this.serienstream = serienstream;
	}

	public URL getBs() {
		return bs;
	}

	public String getBsString() {
		if (this.bs == null) {
			return "";
		} else {
			return this.bs.toString();
		}
	}

	public void setBs(URL bs) {
		this.bs = bs;
	}
}
