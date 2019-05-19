package oop.ex6.analyzing;

/**
 * An exception class for invalid method declaration (missing parameters).
 */
public class InvalidArgumentsException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Invalid method arguments format (missing " +
			"type or name).";

	/**
	 * Default constructor.
	 */
	InvalidArgumentsException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	InvalidArgumentsException(String message) {
		super(message);
	}
}
