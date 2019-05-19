package oop.ex6.analyzing;

/**
 * This class represents a method argument.
 */
public class Argument {

	private final boolean FINAL;  // Indicates if the argument is final.
	private final String TYPE;  // The argument's type.
	private final String NAME;  // The argument's name.

	/**
	 * Constructor of an Argument object.
	 * @param isFinal A boolean representing the existence of "final" prefix.
	 * @param type The argument's type as String.
	 * @param name The argument's name as String.
	 */
	Argument(boolean isFinal, String type, String name) {
		this.FINAL = isFinal;
		this.TYPE = type;
		this.NAME = name;
	}

	/**
	 * @return The Argument's name.
	 */
	public String getName() {
		return this.NAME;
	}

	/**
	 * @return The argument's type.
	 */
	public String getType() {
		return this.TYPE;
	}

	/**
	 * @return True if the Argument is final, false otherwise.
	 */
	public boolean isFinal() {
		return this.FINAL;
	}
}
