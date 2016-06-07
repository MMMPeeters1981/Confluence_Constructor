package trs;

/**
 *
 * An update is a tuple of a variable and a term.
 * 
 * @author Marieke Peeters
 *
 */

public class Update {
	
	// An update contains a variable and a term
	Variable variable;
	Term term;
	
	/**
	 * The constructor Update creates a new update containing a variable and a term.
	 * @param v This is the variable that is to be substituted by the term.
	 * @param t This is the term that is filled in at the position of the variable.
	 */
	Update (Variable v, Term t){
		variable = v;
		term = t;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this update.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal=true;
		// if the objects refer to the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty or the objects come from different classes, they are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
				equal = false;
		// if one of the elements is empty, something is wrong
		else if (variable==null || term==null)
			equal = false;
		// if the elements of the objects are equal, the objects are equal
		else {
			Update upd = (Update)obj;
			if (!(variable.equals(upd.variable))|| 
					!(term.equals(upd.term))) {
				equal = false;
			} 
		}
		return equal;
	}

	/**
	 * This is a method that returns the hashCode of an object
	 * by computing a number from its component hashCodes.
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == variable ? 0 : variable.hashCode());
		hash = 31 * hash + (null == term ? 0 : term.hashCode());
		return hash;
	}
	
	/**
	 * An Update can be changed or overwritten by the method changeUpdate.
	 * @param t This method needs the new term that is to be the replacement of some variable v.
	 */
	public void changeUpdate (Term t) {
		term = t;
	}
}