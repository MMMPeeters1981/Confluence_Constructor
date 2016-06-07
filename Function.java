package trs;

/**
 *
 * A function consists of a functionsymbol and an arity.
 *
 * @author Marieke Peeters
 * 
 */
public class Function {
	
	// a function contains a functionsymbol and an arity
	String functionsymbol;
	int arity;
	
	/**
	 * The constructor Function creates a new function with a functionsymbol 
	 * (String) and an arity (integer).
	 * @param symbol This constructor needs the functionsymbol (String) of 
	 * the function.
	 * @param numarg This constructor needs the arity (integer) of the function.
	 */
	Function (String symbol, int numarg) {
		functionsymbol = symbol;
		arity = numarg;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this function.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal=false;
		// if the objects are the same object, the objects are equal
		if (this == obj)
			return true;
		// if the object is empty or if the objects are from different classes, they are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
				equal = false;
		// if the functionsymbol is empty, something is wrong
		else if (functionsymbol==null)
			equal = false;
		// if the elements of the functions are different, these objects are different
		else {
			Function f = (Function) obj;
			if (f.functionsymbol == null)
				equal = false;
			else if (functionsymbol.equals(f.functionsymbol) &&
					arity == f.arity)
				equal = true;
		}
		// otherwise they are equal
		return equal;
	}

	/**
	 * This is a method that returns the hashCode of an object
	 * by computing a number from its component hashCodes.
	 */
	public int hashCode() {
			int hash = 7;
			hash = 31 * hash + arity;
			hash = 31 * hash + (null == functionsymbol ? 0 : functionsymbol.hashCode());
		return hash;
	}
}
