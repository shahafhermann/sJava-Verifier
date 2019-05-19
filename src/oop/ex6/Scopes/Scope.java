package oop.ex6.Scopes;

//--------imports-----------//
import oop.ex6.main.Sjavac;
import oop.ex6.variables.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class implementing a generic scope's node.
 */
public abstract class Scope {

	//-------data members-----//

	/**The Scope node's parent */
	private Scope parent;

	/**The type of of the scope (global/method/if/while)*/
	String scopeType;

	/**A HashMap containing all the variables that were declared in this scope*/
	HashMap<String, Variable> scopeVariables = new HashMap<>();

	/**A HashMap containing global variables that were'nt initialized in the global scope but were assigned
	 *  a value in this scope */
	private HashMap<String, Variable> uninitialisedGlobalVariables = new HashMap<>();

	/**A VariableFactory instance for the creation of Variables that will be declared in this scope.*/
	private VariableFactory vFactory = new VariableFactory();

	//-------constructors-----//

	/**
	 * A constructor of the class.
	 * @param parent the parent node of the scope's node.
	 */
	public Scope(Scope parent){
		this. parent = parent;
	}

	//-------methods---------//
	/**
	 * Checks if a given variable's name is in the scopes hash map.
	 * @param searchedVariable a given variable's name.
	 * @return true if the scpoe's hash map contains searchedVariable, false otherwise.
	 */
	private boolean contains(String searchedVariable){
		return scopeVariables.containsKey(searchedVariable);
	}

	/**
	 * Returns a Variable object with the key varName from the class's scopeVariables hash map.
	 * @param varName a key to look for.
	 * @return a Variable object with the key varName.
	 */
	private Variable getVariable(String varName){
		if(contains(varName)){
			return scopeVariables.get(varName);
		}else{
			return null;
		}
	}

	 /**
	 *Declares a new variable using a referenced variable as var's value - if a referenced variable such as
	  * newVariable does indeed exist.
	 * @param newVar The variable to be declared.
	 * @param variableName The name of the declared variable.
	 * @param isFinal Boolean value representing whether the variable's final or not.
	 * @param isArgument Boolean value representing whether the variable's an argument in a method or not.
	 * @param value The value with which the variable will be declared with.
	 * @throws IllegalDeclarationException Thrown if the declaration is illegal.
	 * @throws IncompatibleTypeException Thrown if the referenced variable's type's incompatible with
	  * newVar's
	 * @throws TypeNotSupportedException Thrown if the type of the variable isn't supported by s-Java.
	 */
	private void declareWithReference(Variable newVar,String variableName, boolean isFinal,boolean
			isArgument, String value)throws IllegalDeclarationException,
			IncompatibleTypeException, TypeNotSupportedException{
		Variable reference = findVariable(value);
		if (reference != null) {//if it is indeed a reference to a previously declared variable:
			// we'll check if  the referenced var is initialised + if it's type's compatible with newVar's
			if (newVar.compatibleWith(reference)&&reference.isInitialized()){
				Variable varToDeclare = vFactory.createVariable(true,isFinal,isArgument,reference.getType(),
						parent==null);
				scopeVariables.put(variableName, varToDeclare);//if it is, we'll declare a new variable
				return;
			}else{
				//check if value's an uninitialised global var that we've already initialised in this scope
				reference = uninitialisedGlobalVariables.get(value);
				if(reference!=null&&newVar.compatibleWith(reference)){
					Variable varToDeclare = vFactory.createVariable(true,isFinal,isArgument,reference.getType(),
							parent==null);
					scopeVariables.put(variableName, varToDeclare);//if it is, declare a new variable
					return;
				}
			}
		}//if value's type isn't compatible with type ,value isn't a reference or isn't a good one:
		throw  new IllegalDeclarationException();

	}

	/**
	 * Tries to declare a new variable by adding it to the scope's scopeVariables HashMap.
	 * @param variableName The name of the variable to be declared.
	 * @param isFinal A boolean value, Indicating whether the variable is final or not.
	 * @param isArgument A boolean value, Indicating whether the variable is a method argument or not.
	 * @param type The variable's type as String.
	 * @param value The value to be assigned to the new variable.
	 * @throws IllegalDeclarationException Thrown if the declaration's illegal.
	 * @throws TypeNotSupportedException Thrown if the variable's type isn't supported by s-Java.
	 * @throws IncompatibleTypeException Thrown if the value's type isn't compatible with the variable's.
	 */
	public void declareVariable (String variableName,boolean isFinal,boolean isArgument,String type,
									String value)throws IllegalDeclarationException,
									TypeNotSupportedException,IncompatibleTypeException{
		//if there's already such var in this scope\we're trying to make an uninitialised final declaration
		if((isFinal&&value==null)||this.contains(variableName)){
			throw new IllegalDeclarationException();
		}else {
				Variable newVar = vFactory.createVariable(false, isFinal, isArgument,type,parent==null);
				if(value==null) {//if we're trying to declare an uninitialised variable legally:
					scopeVariables.put(variableName, newVar);//we'll declare a new uninitialised variable
				} else if (newVar.isArgument()||(newVar.compatibleWith(value))) {
					//if value's type's compatible with the var's type/we're declaring a method's argument as
					// an argument inside it's own scope(so we'll want to treat it as initialised):
					newVar.initialize();
					scopeVariables.put(variableName, newVar);//we'll declare a new initialised variable
					} else {//if it's not compatible, we'll check if value is a reference to another variable:
						declareWithReference(newVar,variableName,isFinal,isArgument,value);
					}
		}
	}

	/**
	 * Finds a variable in the path from a given scope to the root (the global scope).
	 * @param varToFind the variable's name.
	 * @return the variable if such was found, null otherwise.
	 */
	private Variable findVariable(String varToFind){
		Scope curScope = this;
		while (curScope!=null) {
			if(curScope.contains(varToFind)){
				return curScope.getVariable(varToFind);
			}else{
				curScope = curScope.parent;
			}
		}
		return null;
	}

	/**
	 * Because lastAppearanceOfVar is a global variable and wasn't initialised at the global scope, the
	 * method will register it as an initialised variable of the current scope, in order to avoid changing
	 * it's "isInitialised" data member to "true". The purpose here is to force all other methods to assign a
	 * value to lastAppearanceOfVar before using it.
	 * @param lastAppearanceOfVar a Variable instance
	 * @param valueName a String representing a value to be assigned to lastAppearanceOfVar
	 * @throws TypeNotSupportedException thrown if value's type isn't compatible with lastAppearanceOfVar's.
	 */
	private void assignUninitialisedGlobalVar(Variable lastAppearanceOfVar,String valueName)throws
			TypeNotSupportedException{
			Variable variablesCopy = vFactory.createVariable(true, false, lastAppearanceOfVar.isArgument(),
					lastAppearanceOfVar.getType(), true);
			uninitialisedGlobalVariables.put(valueName, variablesCopy);
	}


	/**
	 * Checks if it's legal assign a declared variable with a given value, possibly another declared variable
	 * @param valueName  The given value.
	 * @param lastAppearanceOfVar The declared variable.
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 * @throws TypeNotSupportedException Thrown if value's type isn't supported by s-Java.
	 */
	private void assignWithReference(String valueName, Variable lastAppearanceOfVar) throws
			IllegalOperationException,IncompatibleTypeException,TypeNotSupportedException {
		//we'll check if the assignment is made with a reference to another variable:
		Variable reference = findVariable(valueName);
		if(reference==null){//if not, valueName variable was ever declared and it's type isn't legal.
			throw new IllegalOperationException();
		}else if(reference.isGlobal()&&!reference.isInitialized()){//reference's an uninitialised global var
			reference = uninitialisedGlobalVariables.get(valueName);//see if it was assigned in this scope
			if(reference==null){//if not:
				throw new IllegalOperationException();
			}
		}
		if (!reference.isInitialized()) {//if the referenced variable was never initialised:
			throw new IllegalOperationException();
		}
		lastAppearanceOfVar.compatibleWith(reference);//val's type's illegal-> exception
		if(lastAppearanceOfVar.isGlobal()&&!lastAppearanceOfVar.isInitialized()&&!scopeType.equals//
				("global")){//we're trying, in a scope other than globalScope, to assign a value to an
			                // uninitialised global variable
			assignUninitialisedGlobalVar(lastAppearanceOfVar,valueName);//see methods's documentation.
		}else{
			lastAppearanceOfVar.initialize();
		}
	}

	/**
	 * Checks if variableName is a declared variable and if it is, is it legal for it to be assigned with a
	 * given value - valueName. It returns nothing and throws no exceptions if the assignment's legal.
	 * @param assignment a HashMap containing a variable's name as key and a value as value.
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws TypeNotSupportedException Thrown if value's type isn't supported by s-Java.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 */
	public void assignValueToVariable(HashMap<String, String> assignment) throws
			IllegalOperationException,TypeNotSupportedException, IncompatibleTypeException{
		Map.Entry<String, String> data = assignment.entrySet().iterator().next();
		String variableName = data.getKey();
		String valueName = data.getValue();
		Variable lastAppearanceOfVar = findVariable(variableName);//we'll search a variable named variableName
		if(lastAppearanceOfVar==null||lastAppearanceOfVar.isFinal()){//did'nt find variableName\ found final
			throw new IllegalOperationException();
		}else {
			//if we did find a declared variable named variableName:
			if (lastAppearanceOfVar.compatibleWith(valueName)) {//if value's type's illegal -> exception
				//if we're trying, in a scope other than globalScope, to declare a variable by assigning it
				// with an uninitialised global variable:
				if((lastAppearanceOfVar.isGlobal()&&!lastAppearanceOfVar.isInitialized())&&!scopeType
						.equals("global")){
					//we'll check if lastAppearanceOfVar was initialised in this scope (else it's illegal):
					assignUninitialisedGlobalVar(lastAppearanceOfVar, valueName);
				}else{ //else, the assignment's legal and we'll mark lastAppearanceOfVar as initialised:
					lastAppearanceOfVar.initialize();
				}
			} else {
				assignWithReference(valueName, lastAppearanceOfVar);
			}
		}
	}

	/**
	 * @return This scope's parent.
	 */
	public Scope getParent() {
		return parent;
	}

	/**
	 * Check if the given boolean parameters (as condition) are valid.
	 * @param parameters The given boolean parameters.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 */
	public void checkParameters(ArrayList<String> parameters) throws IncompatibleTypeException {
		TypeBoolean booleanTester =new TypeBoolean(true, false, false, true);
		boolean isCompatible;
		for (String parameter: parameters) {
			isCompatible = booleanTester.compatibleWith(parameter);
			if (!isCompatible) {
				Variable lastAppearance = findVariable(parameter);
				if (lastAppearance == null) {
					throw new IncompatibleTypeException();
				}
				isCompatible = booleanTester.compatibleWith(lastAppearance);
				if (!isCompatible || !lastAppearance.isInitialized()) {
					throw new IncompatibleTypeException();
				}
			}
		}
	}

	/**
	 * @return This scope's type.
	 */
	public String getScopeType() {
		return scopeType;
	}

	/**
	 * Validate a method call line of code (check the method name and arguments).
	 * @param callElements A hash map with the method's name as key and the arguments as an ArrayList of
	 *        String (in the value slot).
	 * @throws IllegalOperationException Thrown if the assignment is illegal.
	 * @throws IncompatibleTypeException Thrown if the value's type is incompatible with the variable's.
	 */
	public void validateMethodCall(HashMap<String, ArrayList<String>> callElements) throws
			IllegalOperationException, IncompatibleTypeException {
		Map.Entry<String, ArrayList<String>> data = callElements.entrySet().iterator().next();
		String methodName = data.getKey();
		MethodScope curMethod = Sjavac.getGlobalScope().getMethod(methodName);
		ArrayList<String> methodArgs = data.getValue();
		if (methodArgs == null) {
			if (curMethod.getNumberOfArguments() != 0) {
				throw new IllegalOperationException();
			}
		} else if (Sjavac.getGlobalScope().containsMethod(callElements)) {
			 if (methodArgs.size() == curMethod.getNumberOfArguments()) {
				for (int i = 0; i < methodArgs.size(); i++) {
					if(!curMethod.getArgument(i).compatibleWith(methodArgs.get(i))){
						Variable reference = this.findVariable(methodArgs.get(i));
						if(reference==null||!reference.isInitialized()){
							throw new IllegalOperationException();
						}
						curMethod.getArgument(i).compatibleWith(reference);
					}
				}
			} else {
				throw new IllegalOperationException();
			}
		} else {
			throw new IllegalOperationException();
		}
	}
}
