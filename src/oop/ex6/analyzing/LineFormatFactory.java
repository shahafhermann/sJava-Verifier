package oop.ex6.analyzing;

import java.util.regex.Matcher;

/**
 * A class representing the Factory design pattern. This factory is used for creating LineType enums -
 * representing the different kinds of code lines available.
 */
public class LineFormatFactory extends LineAnalyzer{

	/**
	 *
	 * @param line The line of code.
	 * @return The Enum of the corresponding type of line
	 * @throws UnsupportedSyntaxException Throw this if this line type is not supported.
	 */
	public LineType checkFormat(String line) throws UnsupportedSyntaxException{
		for (LineType type: LineType.values()) {
			Matcher match = type.getPattern().matcher(line);
			if (match.matches()) return type;
		}
		throw new UnsupportedSyntaxException();
	}
}

