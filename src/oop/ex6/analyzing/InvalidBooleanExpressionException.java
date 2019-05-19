package oop.ex6.analyzing;

/**
 * An exception class for invalid boolean condition expressions (Incompatible types or missing parameters).
 */
public class InvalidBooleanExpressionException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Invalid parameters or operators in boolean " +
			"expression.";

	/**
	 * Default constructor.
	 */
	InvalidBooleanExpressionException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	InvalidBooleanExpressionException(String message) {
		super(message);
	}
}
