package oop.ex6.main;

// **** IMPORTS **** //
import oop.ex6.Scopes.*;
import oop.ex6.analyzing.*;
import oop.ex6.variables.IncompatibleTypeException;
import oop.ex6.variables.TypeNotSupportedException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//------------------//

/**
 * The main driver of the S-Java verifier.
 */
public class Sjavac {

	//----------data members--------//
	/**The global scope (the root of the tree)*/
	private static GlobalScope globalScope;

	/**The scope in which the program is at the moment*/
	private static Scope curScope;

	/**Counts the curly brackets opened (+1) and closed (-1) in order to keep track of change in curScope*/
	private static long scopeCounter;


	//---------------methods--------------//

	/**
	 * @return globalScope
	 */
	public static GlobalScope getGlobalScope() {
		return globalScope;
	}

	/**
	 * @return the value of scopeCounter
	 */
	public static Long getScopeCounter() {
		return scopeCounter;
	}

	/**
	 * updates scopeCounter by adding one to it .
	 */
	public static void updateScopeCounter() {
		scopeCounter++;
	}

	/**
	 * Run the program.
	 * @param line The current line in the file.
	 * @param reader BufferedReader for reading the file.
	 * @param checkGlobal A boolean value, Indicat if we're in our first run over the file.
	 * @throws UnsupportedSyntaxException Stands for an unsupported code line syntax.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 * @throws MissingTypeException The declaration is missing a variable type.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws TypeNotSupportedException Thrown if the type of the variable isn't supported by s-Java.
	 * @throws IncompatibleTypeException Thrown if the referenced variable's type's incompatible with
	 * @throws MissingMethodNameException Thrown when the method has no name.
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws InvalidBooleanExpressionException Stands for illegal combination of expressions.
	 * @throws IOException Thrown when there's an issue regarding the file.
	 */
	private static void run(String line, BufferedReader reader, boolean checkGlobal) throws
			UnsupportedSyntaxException, InvalidArgumentsException, MissingTypeException,
			IllegalDeclarationException, TypeNotSupportedException, IncompatibleTypeException,
			MissingMethodNameException, IllegalOperationException, InvalidBooleanExpressionException,
			IOException {
		String previousLine = null;
		LineAnalyzer lineAnalyzer = new LineAnalyzer();
		LineFormatFactory lineFormatFactory = new LineFormatFactory();
		while (line != null) { // Go over the rest of the file
			LineAnalyzer.LineType format = lineAnalyzer.checkFormat(line, lineFormatFactory);
			handleFormat(format, lineAnalyzer, line, checkGlobal, previousLine, lineFormatFactory);
			if (scopeCounter < 0) {
				throw new UnsupportedSyntaxException("Exit code 1: Not enough or too many brackets.");
			}
			previousLine = line;
			line = reader.readLine(); // Read the next line
		}
	}

	/**
	 * Receive a line format and decide what to do next.
	 * @param format One of enumerated kinds of legal code lines.
	 * @param lineAnalyzer A LineAnalyzer instance.
	 * @param line The current line of sourceFile the program is processing.
	 * @param checkGlobal Indicate if we're in our first run over the file.
	 * @param previousLine The previous line in the file.
	 * @param lineFormatFactory The LineFormatFactory instance.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 * @throws MissingTypeException The declaration is missing a variable type.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws TypeNotSupportedException Thrown if the type of the variable isn't supported by s-Java.
	 * @throws IncompatibleTypeException Thrown if the referenced variable's type's incompatible with
	 * @throws MissingMethodNameException Thrown when the method has no name.
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws InvalidBooleanExpressionException Stands for illegal combination of expressions.
	 * @throws UnsupportedSyntaxException Stands for an unsupported code line syntax.
	 */
	private static void handleFormat(LineAnalyzer.LineType format, LineAnalyzer lineAnalyzer, String line,
				boolean checkGlobal, String previousLine, LineFormatFactory lineFormatFactory) throws
			InvalidArgumentsException, MissingTypeException, IllegalDeclarationException,
			TypeNotSupportedException, IncompatibleTypeException, MissingMethodNameException,
			IllegalOperationException, InvalidBooleanExpressionException, UnsupportedSyntaxException {
		switch (format) {
			case VAR_DECLARATION: varDeclarationCase(lineAnalyzer, line, checkGlobal);
				break;
			case METHOD_CALL: methodCallCase(lineAnalyzer, line, checkGlobal);
				break;
			case VAR_ASSIGNMENT: varAssignmentCase(lineAnalyzer, line, checkGlobal);
				break;
			case CLOSE_BLOCK: closeBlockCase(lineAnalyzer, previousLine, lineFormatFactory);
				break;
			case COMMENT:  // Ignore it
				break;
			case RETURN: returnCase(checkGlobal);
				break;
			case EMPTY_LINE:  // Ignore it
				break;
			default: curScope = ScopeFactory.createScope(format, lineAnalyzer, line, curScope,
						globalScope, checkGlobal);
				break;
		}
	}

	/**
	 * Try to declare a new variable in the current scope. Called from the relevant case in the run() method.
	 * @param lineAnalyzer The LineAnalyzer instance.
	 * @param line The current line being checked.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws TypeNotSupportedException Thrown if the type of the variable isn't supported by s-Java.
	 * @throws MissingTypeException Thrown when trying to declare a variable without a type.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 */
	private static void varDeclarationCase(LineAnalyzer lineAnalyzer, String line, boolean checkGlobal)
			throws IllegalDeclarationException, TypeNotSupportedException, MissingTypeException,
			IncompatibleTypeException {
		if ((checkGlobal && scopeCounter == 0) || (!checkGlobal && scopeCounter > 0)) {
			boolean isFinal = lineAnalyzer.isFinal(line);
			String type = lineAnalyzer.getDeclarationType(line);
			HashMap<String, String> variables = lineAnalyzer.getElementsOfVariables(line);
			for (Map.Entry<String, String> entry: variables.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				curScope.declareVariable(name, isFinal, false, type, value);
			}
		}
	}

	/**
	 * Try to declare a new method in the current scope. Called from the relevant case in the run() method.
	 * @param lineAnalyzer The LineAnalyzer instance.
	 * @param line The current line being checked.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws InvalidArgumentsException Stands for invalid declaration of arguments.
	 * @throws MissingMethodNameException Thrown when the method has no name.
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 */
	private static void methodCallCase(LineAnalyzer lineAnalyzer, String line, boolean checkGlobal)
			throws IllegalDeclarationException, InvalidArgumentsException, MissingMethodNameException,
			IllegalOperationException, IncompatibleTypeException{
		if ((checkGlobal && scopeCounter == 0) || (!checkGlobal && scopeCounter > 0)) {
			if (curScope.getParent() == null) {
				throw new IllegalDeclarationException();
			}
			HashMap<String, ArrayList<String>> callElements = lineAnalyzer
					.getMethodCallElements(line);
			curScope.validateMethodCall(callElements);
		}
	}

	/**
	 * Handle a block closing line.
	 * @param lineAnalyzer The LineAnalyzer instance.
	 * @param previousLine The previous line in the file.
	 * @param lineFormatFactory The LineFormatFactory instance.
	 * @throws UnsupportedSyntaxException Stands for an unsupported code line syntax.
	 */
	private static void closeBlockCase (LineAnalyzer lineAnalyzer, String previousLine,
								   LineFormatFactory lineFormatFactory) throws UnsupportedSyntaxException {
		if (previousLine == null) {
			throw new UnsupportedSyntaxException("Exit code 1: First line cannot close a " +
					"block.");
		}
		if (curScope.getScopeType().equals("method") && lineAnalyzer.checkFormat
				(previousLine, lineFormatFactory) != LineAnalyzer.LineType.RETURN) {
			throw new UnsupportedSyntaxException("Exit code 1: Missing return statement.");
		}
		curScope = curScope.getParent();
		scopeCounter--;
	}

	/**
	 * Handle variable assignment line.
	 * @param lineAnalyzer The LineAnalyzer instance.
	 * @param line The current line being checked.
	 * @param checkGlobal Indicate if we're in our first run over the file.
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws TypeNotSupportedException Thrown if the type of the variable isn't supported by s-Java.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 */
	private static void varAssignmentCase (LineAnalyzer lineAnalyzer, String line, boolean checkGlobal)
			throws IllegalOperationException, IllegalDeclarationException, TypeNotSupportedException,
			IncompatibleTypeException{
		if ((checkGlobal && scopeCounter == 0) || (!checkGlobal && scopeCounter > 0)) {
			HashMap<String, String> assignment = lineAnalyzer.getElementsOfVariables(line);
			curScope.assignValueToVariable(assignment);
		}
	}

	/**
	 * Handle a return line.
	 * @param checkGlobal Indicate if we're in our first run over the file.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 */
	private static void returnCase (boolean checkGlobal) throws IllegalDeclarationException{
		if ((checkGlobal && scopeCounter == 0) || (!checkGlobal && scopeCounter > 0)) {
			if (curScope.getParent() == null) {
				throw new IllegalDeclarationException();
			}
		}
	}

	/**
	 * Check the arguments given to the program.
	 * @param args The list of arguments given by the user.
	 * @throws IOException Thrown when there is more than 1 argument.
	 */
	private static void checkArgs(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IOException("Exit error 2: Wrong number of args.");
		}
	}

	/**
	 * The main driver.
	 * @param args The list of arguments given by the user.
	 */
	public static void main (String[] args) {
		globalScope = new GlobalScope();
		curScope = globalScope;
		scopeCounter = 0;
		try {
			checkArgs(args);
		} catch (IOException ioe) {
			System.out.println("2");
			System.err.println(ioe.getMessage());
			System.exit(2);
		}
		String fileName = args[0];
		try (BufferedReader firstRun = new BufferedReader(new FileReader(fileName));
			 BufferedReader secondRun = new BufferedReader(new FileReader(fileName))){
			String line = firstRun.readLine(); // Read the first line
			run(line, firstRun, true);
			line = secondRun.readLine();
			run(line, secondRun, false);
			System.out.println("0");
		} catch (IOException e) {
			System.out.println("2");
			System.err.println("Exit code 2: An IO error occurred.");
		} catch (UnsupportedSyntaxException | IllegalDeclarationException | MissingTypeException |
				TypeNotSupportedException | IncompatibleTypeException | InvalidArgumentsException |
				MissingMethodNameException | InvalidBooleanExpressionException | IllegalOperationException
				ex) {
			System.out.println("1");
			System.err.println(ex.getMessage());
		}
	}
}
