package oop.ex6.variables;

/**
 * A class representing the Factory design pattern. This factory is used for creating variables.
 */
public class VariableFactory {

	//*** String representations of allowed variable types ***//
	private static final String INT = "int";
	private static final String DOUBLE = "double";
	private static final String STRING = "String";
	private static final String CHAR = "char";
	private static final String BOOLEAN = "boolean";


	 /**
	 * Create a new variable.
	 * @param initialized Indicates if the variable is initialized.
	 * @param isFinal Indicates if the variable is final.
	 * @param isArgument Indicates if the variable is a method argument.
	 * @param type The variable's type.
	 * @param isGlobal A boolean value stating whether a variable was declared in the global scope or not.
	 * @return The created variable.
	 * @throws TypeNotSupportedException Stands for unsupported types of variables.
	 */
	public Variable createVariable(boolean initialized, boolean isFinal, boolean isArgument, String type,
								   boolean isGlobal)
			throws TypeNotSupportedException{
		switch (type) {
			case INT: return new TypeInt(initialized, isFinal, isArgument, isGlobal);
			case DOUBLE: return new TypeDouble(initialized, isFinal, isArgument, isGlobal);
			case STRING: return new TypeString(initialized, isFinal, isArgument, isGlobal);
			case CHAR: return new TypeChar(initialized, isFinal, isArgument, isGlobal);
			case BOOLEAN: return new TypeBoolean(initialized, isFinal, isArgument, isGlobal);
			default: throw new TypeNotSupportedException();
		}
	}
}
