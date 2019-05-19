package oop.ex6.analyzing;

/**
 * An exception class for illegal declaration of variable (missing parameters).
 */
public class UnsupportedSyntaxException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Illegal declaration of variable.";

	/**
	 * Default constructor.
	 */
	public UnsupportedSyntaxException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	public UnsupportedSyntaxException(String message) {
		super(message);
	}
}
