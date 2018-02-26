package conf_cons.basictrs;

import java.util.ArrayList;

import conf_cons.gui.*;
import conf_cons.conf.*;

/**
 *
 * A variable is actually just a letter.
 * 
 * @author Marieke Peeters
 *
 */
public class Variable {
	// a variable simply contains a name
	public String name;
	
	/**
	 * The constructor Variable creates a new variable-object which can contain 
	 * either a String, a Function, a Term or a Variable.
	 * @param x this is the name of the variable.
	 */
	public Variable (String x) {
		name = x;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this variable.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal=true;
		// if the objects refer to the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty or the classes are different they are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
				equal = false;
		// if the name is empty, something is wrong
		else if (name==null)
			equal = false;
		// if the names are equal, the variables are equal
		else {
			Variable var = (Variable)obj;
			if (!(name.equals(var.name))) {
				equal = false;
			} 
		}
		return equal;
	}

	/**
	 * This is a method that returns the hashCode for an object
	 * by computing a number from its component hashCodes.
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == name ? 0 : name.hashCode());
		return hash;
	}
	
	/**
	 * This method looks for variables in the term and returns 
	 * all the found variables in an array.
	 * @param t The term that is sought through.
	 * @return An array of all of the found variables in the term t.
	 */
	public static ArrayList<Variable> searchVariables (Term t) {
		// create a list of variables
		ArrayList<Variable> foundVariables = new ArrayList<Variable>();
		// If term itself is variable, add that variable.
		if (t.x != null) {
			foundVariables.add(t.x);
		}
		// If term contains subterms, recursion on subterms.
		else if (t.subterms!=null)
			for (Term subterm: t.subterms) {
				ArrayList<Variable> vars = searchVariables(subterm);
				for (Variable foundVariable: vars)
					// add all found variables to the list of found variables
					foundVariables.add(foundVariable);
			}
		return foundVariables;
	}
	
	/**
	 * This method is used remove duplicates from an array.
	 * @param variables This is the array of variables that is to be shortened.
	 * @return The shortened array.
	 */
	public static ArrayList<Variable> removeDuplicates (ArrayList<Variable> variables){
		ArrayList <Variable> newvariables = new ArrayList<Variable>();
		// Loop through variables.
		for (Variable variable1: variables){
			// Add all new found variables to the ArrayList newvariables.
			if (!newvariables.contains(variable1))
				newvariables.add(variable1);
		}
		return newvariables;
	}
}