package trs;

/**
 *
 * A rule connects a term on the lefthandside (of the arrow) 
 * to a term on the righthandside (of the arrow).
 * 
 * @author Marieke Peeters
 *
 */

public class Rule {

	// a rule contains two terms: lefthandside and righthandside
	Term lefthandside, righthandside;
	// a rule might be leftlinear or rightlinear (or both or neither)
	// leftlinear means: each variable occurs only once on the lefthandside
	// rightlinear means: same for righthandside
	boolean leftlinear, rightlinear;
	
	/**
	 * The constructor Rule creates an object which contains 
	 * a righthandside term and a lefthandside term.
	 * @param lhs It needs as a first argument the lefthandsideterm.
	 * @param rhs As a second argument it needs the righthandsideterm.
	 */
	Rule (Term lhs, Term rhs) {
		lefthandside = lhs;
		righthandside = rhs;
		leftlinear = lefthandside.checkLinearity();
		rightlinear = righthandside.checkLinearity();
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this rule.
	 * @return The method returns false if the objects are not equal and true if 
	 * they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal = true;
		// if they are the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty, or the objects are not from the same class, 
		// they are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
			equal=false;
		// if one of the terms is empty, something is wrong
		else if (righthandside==null||lefthandside==null)
			equal = false;
		// if the terms are not equal, the rules are not equal
		else {
			Rule r = (Rule) obj;
			if (!(righthandside.equals(r.righthandside)) ||
					!(lefthandside.equals(r.lefthandside)))
				equal=false;
		}
		// otherwise the rules are equal
		return equal;
	}

	/**
	 * This is a method that returns the hashCode of an object
	 * by computing a number from its component hashCodes.
	 */
	public int hashCode() {
			int hash = 7;
			hash = 31 * hash + (null == lefthandside ? 0 : lefthandside.hashCode());
			hash = 31 * hash + (null == righthandside ? 0 : righthandside.hashCode());
			hash = 31 * hash + (leftlinear ? 1 : 0);
			hash = 31 * hash + (rightlinear ? 1 : 0);
		return hash;
	}
	
	/**
	 * This method prints a rule.
	 */
	public void printRule() {
		lefthandside.printTerm();
		System.out.print(" -> ");
		righthandside.printTerm();
	}
	
	/**
	 * This method returns a rule as a string.
	 * @return The rule as a string.
	 */
	public String ruleToString() {
		String s = "";
		s = s + lefthandside.termToString();
		s = s + " -> ";
		s = s + righthandside.termToString();
		return s;
	}
}