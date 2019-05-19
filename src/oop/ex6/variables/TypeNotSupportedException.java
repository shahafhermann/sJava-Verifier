package oop.ex6.variables;

/**
 * An exception class for illegal declaration type.
 */
public class TypeNotSupportedException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Declaration type not supported.";

	/**
	 * Default constructor.
	 */
	TypeNotSupportedException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	TypeNotSupportedException(String message) {
		super(message);
	}
}
