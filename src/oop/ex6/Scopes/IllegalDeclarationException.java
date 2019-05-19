package oop.ex6.Scopes;

/**
 * An exception class for invalid variable declaration (missing parameters).
 */
public class IllegalDeclarationException extends Exception {

	/*
	The default error message for this exception.
	 */
	private static final String ERROR_MESSAGE = "Exit code 1: Illegal variable or method declaration " +
			"(unsupported/missing parameters).";

	/**
	 * Default constructor.
	 */
	public IllegalDeclarationException() {
		super(ERROR_MESSAGE);
	}

	/**
	 * Constructor with a custom message.
	 * @param message The message to deliver.
	 */
	public IllegalDeclarationException(String message) {
		super(message);
	}
}
