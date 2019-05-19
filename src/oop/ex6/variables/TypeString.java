package oop.ex6.variables;

import java.util.regex.Pattern;

/**
 * A class representing a String variable.
 */
class TypeString extends Variable{

	/*
	Pattern for compatible types with this class.
	 */
	private static final Pattern COMPATIBLE_WITH = Pattern.compile("\".*\"");
	/*
	String representation of this type.
	 */
	private static final String TYPE = "String";

	/**
	 * Constructor of this type.
	 * @param initialized Indicates if the variable is initialized.
	 * @param isFinal Indicates if the variable is final.
	 * @param isArgument Indicates if the variable is a method argument.
	 * @param isGlobal Indicates if the variable was declared in the Global Scope.
	 */
	TypeString(boolean initialized, boolean isFinal, boolean isArgument, boolean isGlobal) {
		super(initialized, isFinal, isArgument, COMPATIBLE_WITH, TYPE, isGlobal);
	}
}
