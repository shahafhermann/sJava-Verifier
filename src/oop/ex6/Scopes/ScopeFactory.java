package oop.ex6.Scopes;

import oop.ex6.analyzing.*;
import oop.ex6.main.Sjavac;
import oop.ex6.variables.IncompatibleTypeException;
import oop.ex6.variables.TypeNotSupportedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class implementing the factory design pattern, creates Scope instances.
 */
public class ScopeFactory {

	/**
	 * Validates a given line of while or if declaration.
	 * @param lineAnalyzer A LineAnalyzer instance.
	 * @param line The current line of sourceFile the program is processing.
	 * @throws IllegalDeclarationException thrown if an illegal declaration was made.
	 * @throws InvalidBooleanExpressionException Thrown if a boolean expression is found to be invalid.
	 * @throws IncompatibleTypeException Thrown if a value's type is'nt compatible a variable's.
	 */
	private static void validateIfWhileScope(LineAnalyzer lineAnalyzer, String line, Scope curScope) throws
			IllegalDeclarationException, InvalidBooleanExpressionException, IncompatibleTypeException {
		if (curScope.getParent() == null) {
			throw new IllegalDeclarationException();
		}
		ArrayList<String> parameters = lineAnalyzer.getConditionParameters(line);
		curScope.checkParameters(parameters);
	}

	/**
	 * Create a new Scope instance, considering the given parameters.
	 * @param format One of enumerated kinds of legal code lines.
	 * @param lineAnalyzer A LineAnalyzer instance.
	 * @param line The current line of sourceFile the program is processing.
	 * @param curScope The current scope.
	 * @param globalScope The global scope.
	 * @param checkGlobal A boolean value, Indicating if we're in our first run over the file.
	 * @return The new scope to become curScope.
	 * @throws IllegalDeclarationException Thrown in case of an illegal declaration.
	 * @throws MissingMethodNameException Thrown in case of a missing method name.
	 * @throws InvalidArgumentsException Thrown in case of an invalid argument.
	 * @throws IncompatibleTypeException Thrown Thrown if a value's type is'nt compatible a variable's.
	 * @throws TypeNotSupportedException Thrown in case of an operation unsuported by s-Java.
	 * @throws InvalidBooleanExpressionException Thrown Thrown if a boolean expression is found to be invalid
	 */
	public static Scope createScope(LineAnalyzer.LineType format, LineAnalyzer lineAnalyzer, String line,
			Scope curScope, GlobalScope globalScope, boolean checkGlobal) throws
			IllegalDeclarationException, TypeNotSupportedException, InvalidBooleanExpressionException,
			MissingMethodNameException, InvalidArgumentsException, IncompatibleTypeException {
		switch (format) {
			case METHOD_DECLARATION:
				if (curScope.getParent() != null) {
					throw new IllegalDeclarationException();
				}
				HashMap<String, ArrayList<Argument>> methodElements = lineAnalyzer
						.getElementsOfMethod(line);
				Sjavac.updateScopeCounter();
				if (!checkGlobal && Sjavac.getScopeCounter() > 0) {
					Map.Entry<String, ArrayList<Argument>>
						data = methodElements.entrySet().iterator().next();
					String methodName = data.getKey();
					return globalScope.getMethod(methodName);
				}
				return globalScope.addMethod(methodElements);
			case IF_DECLARATION:
				Sjavac.updateScopeCounter();
				if (!checkGlobal && Sjavac.getScopeCounter() > 0) {
					validateIfWhileScope(lineAnalyzer, line, curScope);
				}
				return new IfScope(curScope);
			case WHILE_DECLARATION:
				Sjavac.updateScopeCounter();
				if (!checkGlobal && Sjavac.getScopeCounter() > 0) {
					validateIfWhileScope(lineAnalyzer, line, curScope);
				}
				return new WhileScope(curScope);
			default: throw new IllegalDeclarationException();  // This will never be reached.
		}
	}
}
