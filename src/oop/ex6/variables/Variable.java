package oop.ex6.variables;

import oop.ex6.analyzing.LineAnalyzer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a variable in a source file's code.
 */
public abstract class Variable {

	private final boolean FINAL;  // Indicates if the variable is final.
	private final boolean ARGUMENT;  // Indicates if the variable is a method argument.
	private final String TYPE;  // The variable's type.
	private final Pattern COMPATIBLE;  // A regex pattern of the types compatible with this variable.
	private boolean initialized;  // Indicates if the variable was initialized.
	private boolean isGlobal;  // Indicates if the variable was declared in the Global Scope.

	/**
	 * A variable constructor.
	 * @param initialized Indicates if the variable is initialized.
	 * @param isFinal Indicates if the variable is final.
	 * @param isArgument Indicates if the variable is a method argument.
	 * @param compatibleWith A regex pattern of the types compatible with this variable.
	 * @param type The variable's type.
	 * @param isGlobal Indicates if the variable was declared in the Global Scope.
	 */
	Variable(boolean initialized, boolean isFinal, boolean isArgument, Pattern compatibleWith,
					String type, boolean isGlobal) {
		this.initialized = initialized;
		this.FINAL = isFinal;
		this.ARGUMENT = isArgument;
		this.COMPATIBLE = compatibleWith;
		this.TYPE = type;
		this.isGlobal = isGlobal;
	}

	/**
	 * @return true if this is a method argument, false otherwise.
	 */
	public boolean isArgument() {
		return ARGUMENT;
	}

	/**
	 * @return true if this variable is initialized with a value, false otherwise.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Mark this variable as initialized.
	 */
	public void initialize() {
		this.initialized = true;
	}

	/**
	 * @return true if this variable is final, false otherwise.
	 */
	public boolean isFinal() {
		return FINAL;
	}

	/**
	 * @return This variable's type.
	 */
	public String getType() {
		return this.TYPE;
	}

	/**
	 * @return true if this variable is Global, false otherwise.
	 */
	public boolean isGlobal(){
		return isGlobal;
	}

	/**
	 * Check if the given String represents a compatible type with this variable.
	 * @param value The String value to check.
	 * @return true if it's a compatible type and false if it could be another variable's name.
	 * @throws IncompatibleTypeException Stands for incompatible types.
	 */
	public boolean compatibleWith(String value) throws IncompatibleTypeException {
		Matcher type = COMPATIBLE.matcher(value);
		Matcher name = LineAnalyzer.getNamePattern().matcher(value);
		if (type.matches()) {
			return true;
		} else if(name.matches()) {
			return false;
		}
		throw new IncompatibleTypeException();
	}

	/**
	 * Check if the given Variable is compatible (in type) with this variable.
	 * @param variable The Variable to check.
	 * @return true if it's a compatible type.
	 * @throws IncompatibleTypeException Stands for incompatible types.
	 */
	public boolean compatibleWith(Variable variable) throws IncompatibleTypeException {
		String type = variable.getType();
		switch (this.TYPE) {
			case "boolean":
				if (type.equals("boolean") || type.equals("double") || type.equals("int")) {
					return true;
				}
				break;
			case "double":
				if (type.equals("double") || type.equals("int")) {
					return true;
				}
				break;
			default:
				if (type.equals(TYPE)) {
					return true;
				}
				break;
		}
		throw new IncompatibleTypeException();
	}
}
