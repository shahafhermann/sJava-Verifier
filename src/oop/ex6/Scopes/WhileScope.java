package oop.ex6.Scopes;

/**
 * A class implementing a While Scope node in the program's hierarchy.
 */
class WhileScope extends Scope {

	//---------constructors------------//
	/**
	 * Constructs a new While Scope.
	 * @param parent This Scope's parent.
	 */
	WhileScope(Scope parent){
		super(parent);
		this.scopeType = "while";
	}
}
