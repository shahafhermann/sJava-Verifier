package oop.ex6.variables;

/**
 * An exception class for incompatible variable types.
 */
public class IncompatibleTypeException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Incompatible variable types comparison.";

	/**
	 * Default constructor.
	 */
	public IncompatibleTypeException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	public IncompatibleTypeException(String message) {
		super(message);
	}
}
