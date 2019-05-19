package oop.ex6.Scopes;

//----------imports-----//
import oop.ex6.analyzing.Argument;
import oop.ex6.variables.IncompatibleTypeException;
import oop.ex6.variables.TypeNotSupportedException;
import oop.ex6.variables.Variable;

import java.util.ArrayList;

/**
 * A class implementing a Method's Scope node in the program's hierarchy.
 */
public class MethodScope extends Scope {

	//--------------data members-----------//
	private  ArrayList<Variable> methodArguments = new ArrayList<Variable>();
	private static final String FICTIVE_VALUE_OF_ARGUMENT = "";

	//----------constructors----------//
	MethodScope(Scope parent, ArrayList<Argument> arguments)throws IllegalArgumentException,
			IllegalDeclarationException,TypeNotSupportedException,IncompatibleTypeException{
		super(parent);
		if (arguments != null) {
			for(Argument argument: arguments){
				declareVariable(argument.getName(),argument.isFinal(),true,argument.getType(),
						FICTIVE_VALUE_OF_ARGUMENT);
				methodArguments.add(scopeVariables.get(argument.getName()));
			}
		}
		this.scopeType = "method";
	}

	//----------methods---------------//
	public int getNumberOfArguments(){
		return methodArguments.size();
	}

	/**
	 * @param index index to get the argument from.
	 * @return the method argument (as Variable) at the given index.
	 */
	public Variable getArgument(int index){
		return methodArguments.get(index);
	}

}
