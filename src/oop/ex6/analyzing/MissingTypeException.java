package oop.ex6.analyzing;

/**
 * An exception class for missing variable type at declaration.
 */
public class MissingTypeException extends Exception{

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Missing variable type at declaration.";

	/**
	 * Default constructor.
	 */
	MissingTypeException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	MissingTypeException(String message) {
		super(message);
	}

}
