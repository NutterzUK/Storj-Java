package storj.io.client.exceptions;

/**
 * Checked exception class representing a generic Storj client exception.
 * Intended to be extended to represent more specific exceptions.
 * 
 * @author Lewis Foster
 *
 */
public class StorjException extends Exception {

	private static final long serialVersionUID = -2431904925330919080L;

	private String title;
	private String description;

	/**
	 * Constructor taking required values to populate this exception.
	 * @param title
	 *            a short summary of the exception
	 * @param description
	 *            a more verbose description of the exception
	 */
	public StorjException(String title, String description) {
		this.title = title;
		this.description = description;
	}

	/**
	 * @return title a short summary of the exception
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return description a more berbose description of the exception.
	 */
	public String getDescription() {
		return description;
	}

}
