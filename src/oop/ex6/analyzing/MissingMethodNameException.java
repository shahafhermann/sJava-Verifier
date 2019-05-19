package oop.ex6.analyzing;

/**
 * An exception class for missing method name at declaration.
 */
public class MissingMethodNameException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Missing method name.";

	/**
	 * Default constructor.
	 */
	MissingMethodNameException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	MissingMethodNameException(String message) {
		super(message);
	}
}
