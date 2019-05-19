package oop.ex6.Scopes;

/**
 * A class implementing an If Scope node in the program's hierarchy.
 */
class IfScope extends Scope {

	//-------------constructors-------//

	/**
	 * Constructs a new If Scope.
	 * @param parent This Scope's parent.
	 */
	IfScope(Scope parent){
		super(parent);
		this.scopeType = "if";
	}
}
