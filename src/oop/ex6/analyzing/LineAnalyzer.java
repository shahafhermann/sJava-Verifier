package oop.ex6.analyzing;

import oop.ex6.Scopes.IllegalDeclarationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that defines analyzing tools for breaking up a single line of code.
 */
public class LineAnalyzer {

	/**
	 * Enumerate all different kinds of legal code lines
	 */
	public enum LineType {
		VAR_DECLARATION {
			/*
			Pattern for a variable declaration (with/out assignment).
	 		 */
			final Pattern INT = Pattern.compile("\\s*(final\\b\\s*)?(final\\b\\s*)?int\\s+("+VAR_NAME+
					"\\s*(=\\s*(((-?)\\d+)|"+VAR_NAME+")\\s*)?)\\s*(,\\s*"+VAR_NAME+"\\s*(=\\s*(((-?)\\d+)" +
					"|"+VAR_NAME+")\\s*)?)*;\\s*");
			final Pattern DOUBLE = Pattern.compile("\\s*(final\\b\\s*)?(final\\b\\s*)?double\\s+("+VAR_NAME+
					"\\s*(=\\s*(((-?)\\d+(\\.?\\d+)?)|"+VAR_NAME+")\\s*)?)\\s*(,\\s*"+VAR_NAME+"\\s*(=\\s*" +
					"(((-?)\\d+(\\.?\\d+)?)|"+VAR_NAME+")\\s*)?)*;\\s*");
			final Pattern BOOLEAN = Pattern.compile("\\s*(final\\b\\s*)?(final\\b\\s*)?boolean\\s+("
					+VAR_NAME+"\\s*(=\\s*((((-?)\\d+(\\.\\d+)?)|true|false)|"+VAR_NAME+")\\s*)?)\\s*(,\\s*"
					+VAR_NAME+"\\s*(=\\s*((((-?)\\d+(\\.\\d+)?)|true|false)|"+VAR_NAME+")\\s*)?)*;\\s*");
			final Pattern CHAR = Pattern.compile("\\s*(final\\b\\s*)?(final\\b\\s*)?char\\s+("+VAR_NAME+
					"\\s*(=\\s*(('.')|"+VAR_NAME+")\\s*)?)\\s*(,\\s*"+VAR_NAME+"\\s*(=\\s*(('.')|"
					+VAR_NAME+")\\s*)?)*;\\s*");
			final Pattern STRING = Pattern.compile("\\s*(final\\b\\s*)?(final\\b\\s*)?String\\s+("+VAR_NAME+
					"\\s*(=\\s*((\"[^,\"\']*\")|"+VAR_NAME+")\\s*)?)\\s*(,\\s*"+VAR_NAME+"\\s*(=\\s*" +
					"((\"[^,\"\']*\")|"+VAR_NAME+")\\s*)?)*;\\s*");
			final Pattern DECLARE_VAR = Pattern.compile(INT + "|" + DOUBLE + "|" + BOOLEAN + "|" + CHAR +
					"|" + STRING);
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return DECLARE_VAR;
			}},
		METHOD_DECLARATION {
			/*
			Pattern for method declaration.
	 		 */
			final Pattern DECLARE_METHOD = Pattern.compile("\\s*void\\b\\s*"+METHOD_NAME+"\\s*\\(((\\s*\\)" +
					")|(\\s*"+VAR_TYPE+"\\b\\s*"+VAR_NAME+"\\s*(,\\s*"+VAR_TYPE+"\\b\\s*"+VAR_NAME+"\\s*)*" +
					"\\)))\\s*\\{\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return DECLARE_METHOD;
			}},
		IF_DECLARATION{
			/*
			Pattern for if declaration.
	 		 */
			final Pattern DECLARE_IF = Pattern.compile("\\s*if\\s*\\(\\s*"+CONDITION+"\\s*\\)\\s*\\{\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return DECLARE_IF;
			}},
		WHILE_DECLARATION{
			/*
			Pattern for while declaration.
	 		 */
			final Pattern DECLARE_WHILE = Pattern.compile("\\s*while\\s*\\(\\s*"+CONDITION+
					"\\s*\\)\\s*\\{\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return DECLARE_WHILE;
			}},
		METHOD_CALL{
			/*
			Pattern for method call.
	 		 */
			final Pattern CALL_METHOD = Pattern.compile("\\s*"+METHOD_NAME+"\\s*\\(\\s*"+METHOD_ARGS+
					"\\s*\\)\\s*;\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return CALL_METHOD;
			}},
		VAR_ASSIGNMENT{
			/*
			Pattern for assigning one variable to another variable.
	 		 */
			final Pattern VAR_ASSIGNMENT = Pattern.compile("\\s*" + VAR_NAME + "\\s*=\\s*.+\\s*;");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return VAR_ASSIGNMENT;
			}},
		CLOSE_BLOCK{
			/*
			Pattern for block closing.
	 		 */
			final Pattern CLOSE_BLOCK = Pattern.compile("\\s*}\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return CLOSE_BLOCK;
			}},
		COMMENT{
			/*
			Pattern for a comment line.
	 		 */
			final Pattern COMMENT = Pattern.compile("^[/]{2}.*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return COMMENT;
			}},
		RETURN{
			/*
			Pattern for return line.
	 		 */
			final Pattern RETURN = Pattern.compile("\\s*return\\s*;\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return RETURN;
			}},
		EMPTY_LINE{
			/*
			Pattern for empty line.
	 		 */
			final Pattern EMPTY_LINE = Pattern.compile("\\s*");
			/**
			 * @return This line type's pattern.
			 */
			Pattern getPattern() {
				return EMPTY_LINE;
			}};

		/**
		 * @return This line type's pattern.
		 */
		abstract Pattern getPattern();
		}

	//----------------//
	//*** PATTERNS ***//
	//----------------//

	/*
	Pattern for a variable value.
	 */
	private static final Pattern VAR_VALUE =Pattern.compile("(\".*\")|('.*')|(true)|(false)|(\\d+(.\\d+)?)");
	/*
	A String representation of a variable name pattern.
	 */
	private static final String VAR_NAME = "(?!final|int|double|boolean|char|String)((?:[a-zA-Z]\\w*)|" +
			"(?:_\\w+))";
	/*
	Pattern for a method name
	 */
	private static final String METHOD_NAME = "(?!void)([a-zA-Z]\\w*)";
	/*
	A String representation of a variable type pattern.
	 */
	private static final String VAR_TYPE = "(final\b\\s*)?(int|String|double|boolean|char)";
	/*
	A String representation of method arguments pattern.
	 */
	private static final String METHOD_ARGS = "((("+VAR_VALUE+")|("+VAR_NAME+"))(\\s*,\\s*(("+VAR_VALUE+")" +
			"|("+VAR_NAME+")))*)?";
	/*
	A String representation of a boolean expression pattern.
	 */
	private static final String CONDITION = "[\\w.-]+(\\s*(&&|[|]{2})\\s*[\\w.-]+)*";
	/*
	A String representation of an empty arguments group pattern.
	 */
	private static final String NO_ARGS = "\\(\\s*\\)";
	/*
	A String representation of a final prefix pattern.
	 */
	private static final String FINAL = "\\bfinal\\b";
	/*
	A String representation of a comma.
	 */
	private static final String ARGUMENT_DELIMITER = ",";
	/*
	A String representation of boolean expressions delimiter pattern.
	 */
	private static final String CONDITION_DELIMITER = "(&&)|([|]{2})";
	/*
	A String representation of an opening parameters group delimiter pattern.
	 */
	private static final String PARAMETER_DELIMITER = "(";


	//---------------//
	//*** METHODS ***//
	//---------------//

	/**
	 * Check if the line is in the right format for variable declaration. If it's legal return the line
	 * type (as enum).
	 * @param line The source file line of code the program is currently processing.
	 * @param lfFactory A instance of LineFormatFactroy Class.
	 * @return The type of the given line (Method declaration, variable declaration etc)
	 * @throws UnsupportedSyntaxException Stands for an unsupported code line syntax.
	 */
	public LineType checkFormat(String line, LineFormatFactory lfFactory) throws UnsupportedSyntaxException {
		return lfFactory.checkFormat(line);
	}

	/**
	 * @return The regex pattern for a variable name.
	 */
	public static Pattern getNamePattern() {
		return Pattern.compile(VAR_NAME);
	}


	/**
	 * Extract the value to insert to a variable from a line of code.
	 * @param line The source file line of code the program is currently processing.
	 * @return The values assigned as a HashMap of Strings, with variable names as keys and their assigned
	 * values as value (null if no assignment was done).
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 */
	public HashMap<String, String> getElementsOfVariables(String line) throws IllegalDeclarationException {
		Matcher type = Pattern.compile(VAR_TYPE).matcher(line);
		if (type.find()) {
			line = line.substring(type.end());
		}
		String[] lineElements;
		if (line.contains(ARGUMENT_DELIMITER)) {
			lineElements = line.split(ARGUMENT_DELIMITER);
		} else {
			lineElements = new String[]{line};
		}
		HashMap<String, String> varAssignments = new HashMap<>();
		for (String element: lineElements) {
			Matcher name = Pattern.compile(VAR_NAME).matcher(element);
			Matcher value = VAR_VALUE.matcher(element);
			if (name.find()) {
				if (varAssignments.containsKey(name.group())) {
					throw new IllegalDeclarationException();
				}
				String foundName = name.group();
				if (value.find()) {
					varAssignments.put(foundName, value.group());
				} else if (!element.contains("=")){
					varAssignments.put(foundName, null);
				} else if (name.find()){
					varAssignments.put(foundName, name.group());
				}
			}
		}
		return varAssignments;
	}

	/**
	 * Cut the start of a variable declaration line.
	 * @param line The line of code.
	 * @return A String representing the start of the line (until the first "," if exists or the whole
	 * line if not).
	 */
	private String getLinePrefix(String line) {
		if (line.contains(ARGUMENT_DELIMITER)) {
			return line.substring(0, line.indexOf(ARGUMENT_DELIMITER));
		} else {
			return line;
		}
	}

	/**
	 * Extracts the type from a variable declaration line.
	 * @param line The line of code.
	 * @return The type for this declaration.
	 * @throws MissingTypeException The declaration is missing a variable type.
	 */
	public String getDeclarationType(String line) throws MissingTypeException{
		String subLine = getLinePrefix(line);
		Matcher type = Pattern.compile(VAR_TYPE).matcher(subLine);
		if (type.find()) {
			return type.group();
		}
		throw new MissingTypeException();
	}

	/**
	 * Check if the ariable should be declared as final or not.
	 * @param line The line of code.
	 * @return true if it's final, false otherwise.
	 */
	public boolean isFinal(String line) {
		String subLine = getLinePrefix(line);
		Matcher finalPrefix = Pattern.compile(FINAL).matcher(subLine);
		return finalPrefix.find();
	}

	/**
	 * Extract the arguments from a method declaration line.
	 * @param line the line of code.
	 * @return an ArrayList of Argument objects, representing the method's arguments.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 */
	private ArrayList<Argument> getMethodArguments(String line) throws InvalidArgumentsException{
		Matcher noArguments = Pattern.compile(NO_ARGS).matcher(line);
		if (noArguments.find()) {
			return null;
		}
		String[] lineElements = line.split(ARGUMENT_DELIMITER);
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		for (String element: lineElements) {
			Matcher finalPrefix = Pattern.compile(FINAL).matcher(element);
			Matcher type = Pattern.compile(VAR_TYPE).matcher(element);
			if (type.find()) {
				element = element.substring(type.end());
				Matcher name = Pattern.compile(VAR_NAME).matcher(element);
				if (name.find()) {
					if (finalPrefix.find()) {
						arguments.add(new Argument(true, type.group(), name.group()));
					} else {
						arguments.add(new Argument(false, type.group(), name.group()));
					}
				} else {
					throw new InvalidArgumentsException();
				}
			} else {
				throw new InvalidArgumentsException();
			}
		}
		return arguments;
	}

	/**
	 * Extract the name of a method and it's arguments.
	 * @param line The line of code
	 * @return A HashMap where the key is the method's name and the value is a pointer to an ArrayList of
	 * Arguments representing the method's arguments.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 * @throws MissingMethodNameException Thrown when the method has no name.
	 */
	public HashMap<String, ArrayList<Argument>> getElementsOfMethod(String line) throws
			InvalidArgumentsException, MissingMethodNameException {
		ArrayList<Argument> arguments =getMethodArguments(line.substring(line.indexOf(PARAMETER_DELIMITER)));
		Matcher voidMatch = Pattern.compile("\\s*void").matcher(line);
		if (voidMatch.find()) {  // Will always find.
			line = line.substring(voidMatch.end(), line.indexOf(PARAMETER_DELIMITER));
		}
		Matcher methodName = Pattern.compile(METHOD_NAME).matcher(line);
		if (methodName.find()) {
			return new HashMap<String, ArrayList<Argument>>() {{put(methodName.group(), arguments);}};
		}
		throw new MissingMethodNameException();
	}

	/**
	 * Extract the arguments from a method call line.
	 * @param line the line of code.
	 * @return an ArrayList of Strings, representing the arguments the method is being called with.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 */
	private ArrayList<String> getCallArguments(String line) throws InvalidArgumentsException{
		Matcher noArguments = Pattern.compile(NO_ARGS).matcher(line);
		if (noArguments.find()) {
			return null;
		}
		String[] lineElements = line.split(ARGUMENT_DELIMITER);
		ArrayList<String> arguments = new ArrayList<String>();
		for (String element: lineElements) {
			Matcher name = Pattern.compile(VAR_NAME).matcher(element);
			Matcher value = VAR_VALUE.matcher(element);
			if (value.find()) {
				arguments.add(value.group());
			} else if (name.find()) {
				arguments.add(name.group());
			} else {
				throw new InvalidArgumentsException();
			}
		}
		return arguments;
	}

	/**
	 * Extract the name of the method being called and the arguments it's being called with.
	 * @param line The line of code.
	 * @return A HashMap where the key is the method's name and the value is a pointer to an ArrayList of
	 * Strings representing the method's argument names.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 * @throws MissingMethodNameException Thrown when the method has no name.
	 */
	public HashMap<String, ArrayList<String>> getMethodCallElements(String line) throws
			InvalidArgumentsException, MissingMethodNameException {
		ArrayList<String> arguments = getCallArguments(line.substring(line.indexOf(PARAMETER_DELIMITER)));
		line = line.substring(0, line.indexOf(PARAMETER_DELIMITER));
		Matcher methodName = Pattern.compile(METHOD_NAME).matcher(line);
		if (methodName.find()) {
			return new HashMap<String, ArrayList<String>>() {{put(methodName.group(), arguments);}};
		}
		throw new MissingMethodNameException();
	}

	/**
	 * Extract the condition parameters from an If / While block declaration.
	 * @param line The line of code.
	 * @return An ArrayList of String containing the parameters used in the condition expression.
	 * @throws InvalidBooleanExpressionException Stands for illegal combination of expressions.
	 */
	public ArrayList<String> getConditionParameters(String line) throws InvalidBooleanExpressionException {
		line = line.substring(line.indexOf(PARAMETER_DELIMITER));
		String[] lineElements = line.split(CONDITION_DELIMITER,-1);
		ArrayList<String> parameters = new ArrayList<String>();
		for (String element: lineElements) {
			Matcher name = Pattern.compile(VAR_NAME).matcher(element);
			Matcher value = VAR_VALUE.matcher(element);
			if (name.find()) {
				parameters.add(name.group());
			} else if (value.find()) {
				parameters.add(value.group());
			} else {
				throw new InvalidBooleanExpressionException();
			}
		}
		return parameters;
	}

}
