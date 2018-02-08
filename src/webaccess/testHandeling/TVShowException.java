package webaccess.testHandeling;

public class TVShowException extends Exception {

	private ErrorTyp typ;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TVShowException(ErrorTyp typ) {
		this.typ = typ;
	}

	public ErrorTyp getTyp() {
		return this.typ;
	}

	@Override
	public String getMessage() {
		return "ASD2";
	}

}


