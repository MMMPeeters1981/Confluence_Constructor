package trs;
import java.util.*;

/**
 *
 * A term consists of a variable or of a function-symbol with zero 
 * or more sub-terms.
 * 
 * @author Marieke Peeters
 *
 */
public class Term {
	// a term contains either a variable or a function
	Variable x = null;
	Function function = null;
	// if a term contains a function, it may contain subterms as arguments of the function
	Term[] subterms = null;
	
	// a term may be a normal form
	boolean normalForm;
	// a term may already have been contracted
	boolean contractAttempt = false;
	// a term contains (if already contracted) a set of possible contractions
	Set<Reductionstep> possibleContractions = new HashSet<Reductionstep>();
	
	/**
	 * The constructor Term creates an object of type Term, which 
	 * may contain either a variable, or a function with several 
	 * arguments or subterms.
	 * @param v In this case the constructor needs a variable.
	 */
	Term (Variable v) {
		// create term containing variable
		x=v;
	}
	
	/**
	 * The constructor Term creates an object of type Term, which may contain 
	 * either a variable, or a function with several arguments or subterms.
	 * @param f In this case the constructor needs a function with arity 0, 
	 * also known as a constant.
	 */
	Term (Function f){
		// check if arity is zero
		if (f.arity!=0) {
			System.out.println ("This term (" + f.functionsymbol + ") does not " +
					"contain any subterms, " + "while it should.");
		}
		// create constant (term containing only function)
		else {
			function = f;
		}
	}
	
	/**
	 * The constructor Term creates an object of type Term, which may contain either 
	 * a variable, or a function with several arguments or subterms.
	 * @param f In this case the Term contains a function
	 * @param st and a number of subterms that corresponds to the arity of the function.
	 */
	Term (Function f, Term[] st) {
		function = f;
		subterms = new Term[f.arity];
		// check existence of subterms
		for (int i = 0 ; i < st.length ; i++) {
			if (st[i] == null) { 
				System.out.print ("The arity (" + f.arity + 
						") and the number of subterms (" + (i) + 
						") of function " + f.functionsymbol + " do not match. Missing " 
						+ (f.arity - i) + " subterm");
				if ((f.arity-i) != 1) System.out.print("s");
				System.out.println(".");
				return;
			}
		}
		// add subterms
		for (int i=0; i<st.length;  i++){
			subterms[i]=st[i];
		}
	}
	
	/**
	 * This constructor creates an exact copy of another term
	 * @param t this is the term that needs to be copied
	 */
	Term (Term t) {
		if (t.subterms!=null) {
			function = t.function;
			subterms = new Term[t.function.arity];
			// check existence of subterms
			for (int i = 0 ; i < t.subterms.length ; i++) {
				if (t.subterms[i] == null) { 
					System.out.print ("The arity (" + t.function.arity + 
							") and the number of subterms (" + (i) + 
							") of function " + t.function.functionsymbol + " do not match. Missing " 
							+ (t.function.arity - i) + " subterm");
					if ((t.function.arity-i) != 1) System.out.print("s");
					System.out.println(".");
					return;
				}
			}
			// add subterms
			for (int i=0; i<t.subterms.length;  i++){
				subterms[i]=new Term(t.subterms[i]);
			}
		}
		else if (t.function!=null)
			if (t.function.arity!=0)
				System.out.println ("This term (" + t.function.functionsymbol + ") does not " +
						"contain any subterms, " + "while it should.");
			// create constant (term containing only function)
			else
				function = t.function;
		else
			x=t.x;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this term.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal=true;
		// if the two objects refer to the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty or the classes of the objects are different, the objects are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
				equal = false;
		// if the term does not contain a function nor a variable, something is wrong
		else if (x==null && 
					function==null)
			equal = false;
		// if all the elements of the objects are equal, so are the objects
		else {
			Term t = (Term)obj;
			if (t.x==null && 
					t.function==null)
				equal=false;
			else if ( (x!=null&&t.x==null) ||
						(t.x!=null&&x==null) ||
						(x!=null && t.x!=null && !(x.equals(t.x)))||
					(function!=null && t.function!=null &&
					!(function.equals(t.function)))) {
				equal = false;
			} else if (subterms!=null && t.subterms!=null){
				if (subterms.length!=t.subterms.length)
					equal = false;
				else {
					for (int i=0; i<subterms.length; i++) {
						if (!subterms[i].equals(t.subterms[i])){
							equal=false;
							break;
						}
					}
				}
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
			hash = 31 * hash + (null == x ? 0 : x.hashCode());
			hash = 31 * hash + (null == function ? 0 : function.hashCode());
			if (subterms!=null)
				for (Term st: subterms)
					hash = 31 * hash + (null == st ? 0 : st.hashCode());
			hash = 31 * hash + (normalForm ? 1 : 0);
			hash = 31 * hash + (contractAttempt ? 1 : 0);
		return hash;
	}
	
	/**
	 * This method can be used to print a term t (including parentheses).
	 */
	public void printTerm() {
		// if term is variable, print variable-name
		if (x!=null && x.name!=null) 
				System.out.print (x.name);
		// if term is function, print functionsymbol
		if (function!=null) {
				System.out.print (function.functionsymbol);
				if (function.arity!=0) 
					System.out.print("(");
		}
		// if term contains subterms, repeat printTerm recursively for subterms
		if (subterms != null && function!=null && function.functionsymbol!=null) {
			for (int i=0 ; i < subterms.length ; i ++) {
				subterms[i].printTerm();
				if (i!=subterms.length-1) System.out.print(",");
				else System.out.print(")");
			}
		}
	}
	
	/**
	 * This method returns a term as a string with parentheses (easier to understand and read).
	 * @return The term represented as a string.
	 */
	public String termToString() {
		String s = "";
		// if term is variable, print variable-name
		if (x!=null && x.name!=null) 
				s = s + x.name;
		// if term is function, print functionsymbol
		if (function!=null) {
				s = s + function.functionsymbol;
				if (function.arity!=0) 
					s = s + "(";
		}
		// if term contains subterms, repeat printTerm recursively for subterms
		if (subterms != null && function!=null && function.functionsymbol!=null) {
			for (int i=0 ; i < subterms.length ; i ++) {
				s =  s + subterms[i].termToString();
				if (i!=subterms.length-1) s = s + ",";
				else s = s + ")";
			}
		}
		return s;
	}
	
	/**
	 * This method returns a term as a string without parentheses (used for comparisons with original strings).
	 * @return The term represented as a string without parentheses.
	 */
	public String termToString2() {
		String s = "";
		// if term is variable, print variable-name
		if (x!=null && x.name!=null) 
				s = s + x.name;
		// if term is function, print functionsymbol
		if (function!=null) {
				s = s + function.functionsymbol;
		}
		// if term contains subterms, repeat printTerm recursively for subterms
		if (subterms != null && function!=null && function.functionsymbol!=null) {
			for (int i=0 ; i < subterms.length ; i ++) {
				if (subterms[i]!=null)
					s =  s + subterms[i].termToString2();
				else
					return null;
			}
		}
		return s;
	}

	/**
	 * This method is a method that checks if two terms are equal
	 * under unification. For instance, if THIS term is *(I,x) 
	 * and the other term is *(x,y) the terms are NOT equal, because the first 
	 * subterm needs to be I. If THIS term is *(x,y) and the OTHER term is 
	 * *(I,I) the terms ARE equal. Also if THIS term is *(x,y) and the other term
	 * is *(I,x) the terms are equal. The pattern of THIS term is important.
	 * @param t This is the OTHER term!
	 * @return The method returns 'true' if the terms are (possibly) equal. 
	 * If they are not, the method returns false.
	 */
	public boolean equalTermsVariables (Term t){
		boolean equal=true;
		if (this.equals(t))
			return equal;
		if (t==null)
			equal = false;
		// Check if one of the terms is a variable
		else if (this.x!=null)
			return equal;
		// Otherwise: check if the headsymbols are equal.
		else if (this.headsymbolsEqualVariables(t)) {
			// If there are no subterms, the terms are equal: return true.
			if (subterms == null && t.subterms == null) 
				return equal;
			// If there are subterms, check if the number of subterms is equal.
			else if	(subterms!=null && t.subterms!=null && 
					subterms.length==t.subterms.length) {
				// If number is equal, check if subterms equal recursively.
				for (int i=0;i<subterms.length;i++) {
					if (subterms[i]!=null && t.subterms[i]!=null)
					equal = subterms[i].equalTermsVariables(t.subterms[i]);
					if (equal==false)
						break;
				}
			} else {
				// If the number of subterms is different, the terms are different.
				equal=false;
			}
		} else {
			// If the headsymbols are different, the terms are different.
			equal = false;
		}
		return equal;
	}
	
	/**
	 * This method checks if the headsymbols are either equal
	 * or variables, so that a substitution is possible to make them equal.
	 * @param t This is the other term.
	 * @return The method returns true if the headsymbols are equal or variables.
	 */
	public boolean headsymbolsEqualVariables (Term t) {
		boolean equal = true;
		// If one of the terms is empty: give a warning.
		if (this.equals(t))
			return equal;
		if (t == null)
			equal = false;
		// If both terms non empty: check if they contain variables.
		else if (x==null) {
			if (t.x!=null) {
				equal = false;
			} else
			// If both of the terms contain functions, the headsymbols must be equal.
			if (!(function.equals(t.function))){
				equal = false;
			}
		}
		return equal;
	}
	
	/**
	 * This method is used to store information about a term being in normal form.
	 */
	public void normalForm (){
		normalForm=true;
	}
	
	/**
	 * This method returns the subterm at a certain position in the term.
	 * @param pos The position at which is looked inside the term.
	 * @return The subterm that is found at the given position.
	 */
	public Term giveSubterm (Position pos) {
		Term subtermFound = null;
		// If position empty, give warning.
		if (pos.position.isEmpty())
			System.out.println("Error: Cannot give subterm for empty position!");
		else {
			// If position is length 1, return term itself.
			if (pos.position.size()==1) {
				subtermFound = this;
			// Otherwise: take proper subterm and look at
			// next position-index recursively.
			} else {
				Position newposition = new Position();
				for (int i=1; i<pos.position.size();i++)
					newposition.position.add(pos.position.get(i));
				if (subterms!=null && subterms[pos.position.get(1)]!=null)
					subtermFound = subterms[pos.position.get(1)].giveSubterm (newposition);
			}
		}
		// If subterm not found, give warning.
		if (subtermFound == null) {
			System.out.println("Error: Subterm not found.");
			return subtermFound;
		}
		return subtermFound;
	}
	
	/**
	 * This method is used to check if a term 
	 * conforms to the non-linearity of a rule.
	 * @param positions These positions are the positions at which a variable occurs in the
	 * non-linear lefthandside of the rule.
	 * @return A boolean is returned: if the term conforms to the rule, the method returns true.
	 */
	public boolean checkForNonLinearity (ArrayList<Position> positions){
		Term subterm1, subterm2;
		subterm1=null;
		subterm2=null;
		boolean NonLinearityOk = true;
		// Loop over the given positions.
		one: { for (Position position1: positions) {
				for (Position position2: positions) {
					// search for the subterm in position 1
					subterm1 = giveSubterm(position1);
					// search for the subterm in position 2
					subterm2 = giveSubterm(position2);
					// check if they are equal
					NonLinearityOk = subterm1.equals(subterm2);
					// if not, return false immediately, rest of subterms
					// does not matter.
					if (NonLinearityOk==false) {
						break one;
					}
				}
		}	}
		return NonLinearityOk;
	}
	
	/**
	 * This method is used to replace a subterm in the term by another subterm.
	 * @param t This is the term in which the subterm is to be replaced.
	 * @param p This is the position at which the subterm is to be replaced.
	 * @param st This is the new subterm that will take the place of the old one.
	 * @return The term that is returned contains the new subterm at the proper position.
	 */
	public static Term replaceSubterm(Term t, Position p, Term st) {
		Term newt = new Term(t);
		// if position is of length 1 it is [0], which means
		// the term we want to replace is the term itself
		if (p.position.size()==1) {
			return st;
		// If position is of length bigger than 1, replace the proper subterm.
		} else {
			ArrayList<Integer> newposition = new ArrayList<Integer>();
			for (int i: p.position)
				newposition.add(i);
			newposition.remove(1);
			Position newp = new Position(newposition);
			Term newsubterm = replaceSubterm(t.subterms[p.position.get(1)], newp, st);
			newt.subterms[p.position.get(1)] = new Term(newsubterm);
		}
		return newt;
	}
	
	/**
	 * This method checks for a term if there are variables
	 * that occur more than once in the term. Linearity means that a variable 
	 * does not occur more than once.
	 * @return If the term is Linear the method returns true. Otherwise false.
	 */
	public boolean checkLinearity () {
		// get all the variables in the term
		ArrayList<Variable> variablesInTerm = 
			Variable.removeDuplicates(Variable.searchVariables(this));
		// for each variable in the term
		for (Variable v: variablesInTerm) {
			if (v!=null){
				Term varterm = new Term(v);
				// Get positions of variable in term.
				ArrayList<Position> positions = Position.findPosition(this,varterm);
				if (positions.isEmpty()) {
					System.out.print("Error: no positions found for subterm ");
					varterm.printTerm();
					System.out.print(" in term ");
					printTerm();
					System.out.println(".");
				}
				// Check for linearity: if lefthandside contains 
				// variable more then once, subterms must be equal at  
				// positions of variable in lefthandside.
				if (positions.size()>1) {
					// Equality of subterms at those positions
					// checked by method 'checkForNonLinearity'.
					return false;
				}
			}
		}
		return true;
	}
}