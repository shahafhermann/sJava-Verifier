package oop.ex6.Scopes;

//----------imports------//
import oop.ex6.analyzing.Argument;
import oop.ex6.variables.IncompatibleTypeException;
import oop.ex6.variables.TypeNotSupportedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class implementing a Global Scope node (root) in the program's hierarchy.
 */
public class GlobalScope extends Scope {

	//----------data members--------//
	private HashMap<String, MethodScope> methods = new HashMap<String, MethodScope>();


	//----------constructors--------//
	public GlobalScope(){
		super(null);
		this.scopeType = "global";
	}

	//-----------methods------------//

	/**
	 * Add a new method to the Global Scope.
	 * @param methodData A HashMap with the method's name as key and arguments as an ArrayList of Arguments.
	 * @return The newly created MethodScope.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws IncompatibleTypeException Thrown if the referenced variable's type's incompatible with
	 * newVar's
	 * @throws TypeNotSupportedException Thrown if the type of the variable is'nt dupported by s-Java.
	 */
	public MethodScope addMethod(HashMap<String,ArrayList<Argument>> methodData) throws
			IllegalDeclarationException, IncompatibleTypeException,TypeNotSupportedException{
		Map.Entry<String, ArrayList<Argument>> data = methodData.entrySet().iterator().next();
		String methodName = data.getKey();
		if (methods.containsKey(methodName)) {
			throw new IllegalDeclarationException();
		}
		MethodScope newMethod = new MethodScope(this,data.getValue());
		methods.put(methodName,newMethod);
		return newMethod;
	}

	/**
	 * Check if the given method already exists.
	 * @param methodData A HashMap with the method's name as key and arguments as an ArrayList of Strings.
	 * @return true of it does exist.
	 * @throws IllegalOperationException Thrown if the method does not exist.
	 */
	public boolean containsMethod(HashMap<String,ArrayList<String>> methodData) throws
			IllegalOperationException {
		Map.Entry<String, ArrayList<String>> data = methodData.entrySet().iterator().next();
		String methodName = data.getKey();
		if (methods.containsKey(methodName)) {
			return true;
		}
		throw new IllegalOperationException();
	}

	/**
	 * @param methodName The name of the required method.
	 * @return The MethodScope corresponding the given name.
	 */
	public MethodScope getMethod(String methodName) {
		return methods.get(methodName);
	}
}
