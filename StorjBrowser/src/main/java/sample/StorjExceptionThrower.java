package sample;

import storj.io.client.exceptions.StorjException;

/**
 * Functional interface to represent code with no return value that may throw a  {@link StorjException}.
 * @author Lewis Foster
 *
 */
@FunctionalInterface
public interface StorjExceptionThrower {

	/**
	 * Void method that may throw a StorjException.
	 * @throws StorjException 
	 */
	void run() throws StorjException;

}
