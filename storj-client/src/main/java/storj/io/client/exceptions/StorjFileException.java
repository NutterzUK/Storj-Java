package storj.io.client.exceptions;

/**
 * Checked Exception class extending {@link StorjException}. Represents exceptions that
 * may occur when attempting to store files, Not enough disk space etc.
 * 
 * @author Lewis Foster
 *
 */
public class StorjFileException extends StorjException {

	private static final long serialVersionUID = 7568019545818349564L;

	/**
	 * Constructor taking required values to populate this exception.
	 * @param title
	 *            a short summary of the exception
	 * @param description
	 *            a more verbose description of the exception
	 */
	public StorjFileException(final String title, final String description) {
		super(title, description);
	}

}
