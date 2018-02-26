package conf_cons.basictrs;

import conf_cons.gui.*;
import conf_cons.conf.*;

/**
 * 
 * Reductionstep is one step in the reductionrow. It contains the term that was contracted,
 * the term that is the result of the contraction, the rule that was applied, and the position
 * at which the rule was applied in the term.
 * 
 * @author Marieke Peeters
 *
 */
public class Reductionstep {
	// a reductionstep contains the startterm, 
	// the rule that was applied to the startterm, 
	// and the position at which the rule was applied,
	// and what resultterm was computed by this application
	public Term startTerm;
	public Term resultTerm;
	public Rule appliedRule;
	public Position positionApplied;
	
	/**
	 * The constructor Reductionstep makes it possible to save a 
	 * reductionstep after you apply a rule to a term, which 
	 * results in a reduction of the term.
	 * @param startterm This constructor needs term that was contracted.
	 * @param resultterm This constructor needs the result of the contraction.
	 * @param appliedrule This constructor needs the rule that was applied to the startterm to get
	 * the resultterm.
	 * @param positionapplied This constructor needs the position at which the rule was applied
	 * to the startterm to get the resultterm.
	 */
	public Reductionstep (Term startterm, Term resultterm, Rule appliedrule, 
														Position positionapplied) {
		startTerm = startterm;
		resultTerm = resultterm;
		positionApplied = positionapplied;
		appliedRule = appliedrule;
	}
	
	/**
	 * This constructor makes a copy of a reductionstep
	 * @param rs This is the reductionstep that is to be copied.
	 */
	public Reductionstep (Reductionstep rs) {
		startTerm = rs.startTerm;
		resultTerm = rs.resultTerm;
		appliedRule = rs.appliedRule;
		positionApplied = rs.positionApplied;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this reductionstep.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal=true;
		// if the objects refer to the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty, or the objects come from different classes, they are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
			equal = false;
		// if one of the elements of the reductionstep is empty something went wrong
		else if (startTerm==null||resultTerm==null||
				appliedRule==null||positionApplied==null)
			equal = false;
		// if one of the elements of the objects is unequal, the objects themselves are not equal
		else {
			Reductionstep redst = (Reductionstep)obj;
			if (!(startTerm.equals(redst.startTerm))||
					!(resultTerm.equals(redst.resultTerm))||
					!(appliedRule.equals(redst.appliedRule))||
					!(positionApplied.equals(redst.positionApplied))) {
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
			hash = 31 * hash + (null == startTerm ? 0 : startTerm.hashCode());
			hash = 31 * hash + (null == resultTerm ? 0 : resultTerm.hashCode());
			hash = 31 * hash + (null == appliedRule ? 0: appliedRule.hashCode());
			hash = 31 * hash + (null == positionApplied ? 0 : positionApplied.hashCode());
		return hash;
	}
	
	/**
	 * This method prints a reductionstep and its content.
	 */
	public void printReduction() {
		System.out.print("Contracted ");
		startTerm.printTerm();
		System.out.print(" resulting in ");
		resultTerm.printTerm();
		System.out.print(" by applying rule ");
		appliedRule.printRule();
		System.out.print(" at position ");
		positionApplied.printPos();
		System.out.println();
	}
	
	/**
	 * This method creates a string
	 * that contains all the information about a reductionstep.
	 * @return The method returns a string that describes the reductionstep.
	 */
	public String reductionToString() {
		String reductionString = ("Contracted " + startTerm.termToString() + 
				" resulting in " + resultTerm.termToString() + " by applying rule " + 
				appliedRule.lefthandside.termToString() + "->" + 
				appliedRule.righthandside.termToString() + " at position [");
		for (int i=0;i<positionApplied.position.size();i++){
			if(i<positionApplied.position.size()-1)
				reductionString = reductionString + positionApplied.position.get(i) + (", ");
			else
				reductionString = reductionString + positionApplied.position.get(i);
		}
		reductionString = reductionString + ("].");
		return reductionString;
	}
}