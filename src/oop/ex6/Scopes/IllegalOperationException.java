package oop.ex6.Scopes;

/**
 * An exception class for illegal operation.
 */
public class IllegalOperationException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Illegal or not supported operation.";//Todo

	/**
	 * Default constructor.
	 */
	IllegalOperationException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	public IllegalOperationException(String message) {
		super(message);
	}
}
