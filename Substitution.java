package trs;
import java.util.*;

/**
 * This class contains information about and methods that use or alter substitutions.
 * A position contains an ArrayList of Updates. It can be used to
 * change variables into terms.
 * 
 * @author Marieke Peeters
 *
 */
public class Substitution {
	ArrayList<Update> substitution = new ArrayList<Update>();
	
	/**
	 * Constructor: the empty substitution
	 */
	public Substitution() {
	}
	
	/**
	 * Constructor: save a certain arraylist of updates as a substitution
	 * @param subst This is the arraylist of updates you want to store into the object
	 */
	public Substitution (ArrayList<Update> subst) {
		substitution = subst;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this substitution.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal = true;
		// if the objects are the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty or the objects are from different classes, they are not equal
		if (obj==null||(this.getClass()!=obj.getClass()))
			equal=false;
		// if substitution is null, something is wrong
		else if (substitution==null)
			equal = false;
		// check if the arrays of updates are equal, if they are not, the objects are not equal
		else {
			Substitution subst = (Substitution) obj;
			if (substitution.size()==subst.substitution.size()) {
				for (int i=0; i<substitution.size(); i++) {
					if (substitution.get(i) == null) {
						equal=false;
						break;
					}
					else if (!(substitution.get(i).equals(subst.substitution.get(i)))) {
						equal=false;
						break;
					}
				}
			} else
				equal=false;
		}
		return equal;
	}

	/**
	 * This is a method that returns the hashCode of an object
	 * by computing a number from its component hashCodes.
	 */
	public int hashCode() {
			int hash = 7;
			for (Update upd: substitution) {
				hash = 31 * hash + (null == upd ? 0 : upd.hashCode());
			}
		return hash;
	}
	
	/**
	 * This is a method that looks for 
	 * a possible substitution to make two terms equal.
	 * It assumes that one of the terms contains variables that 
	 * can be substituted for the proper subterms by looking
	 * for them in the other term.
	 * @param lefthandside This is the term that contains the variables
	 * @param substitutionTerm This is the term that contains the proper subterms
	 * @return the method returns an array of Updates, for each variable in the term, there is an update,
	 * the entire array is the substitution.
	 */
	public static Substitution substitutionUnification(Term lefthandside, Term substitutionTerm){
		boolean nonLinearityNoProblem = true;
		// First look for variables to substitute
		ArrayList<Variable> variablesInTerm = 
			Variable.removeDuplicates(Variable.searchVariables(lefthandside));
		// then create array of updates
		Substitution substitution = new Substitution();
		for (Variable v: variablesInTerm) {
			if (v!=null){
				Term varterm = new Term(v);
				// Check positions of variable in term.
				ArrayList<Position> positions = Position.findPosition(lefthandside,varterm);
				// Check for linearity: if lefthandside contains 
				// variable more then once, subterms must be equal at  
				// positions of variable in lefthandside.
				if (positions.size()>1) {
					// Equality of subterms at those positions
					// checked by method 'checkForNonLinearity'.
					nonLinearityNoProblem = substitutionTerm.checkForNonLinearity 
											(positions);
				}
				if (nonLinearityNoProblem) {
					// If non-linearity does not cause problem, 
					// create update for variable in array of updates.
					for (Position position1: positions){
						// Look for proper subterm at position and create new update.
						Term foundSubterm = substitutionTerm.giveSubterm(position1);
						Update newupdate = new Update(v,foundSubterm);
						substitution.substitution.add(newupdate);
						break;
					}
				} 
			}
		}
		return substitution;
	}
	
	/**
	 * This method applies a substitution to a term.
	 * @param t This is the term to which the substitution is applied.
	 * @param s This is the substitution that is to be applied to t.
	 * @return This is the result of the substitution applied to the term.
	 */
	public static Term applySubstitution (Term t, Substitution s) {
		if (Variable.searchVariables(t).isEmpty())
			return t;
		// create a new term by substituting a variable in the original term
		Term newt = applySubstitution2(t,s);
		Set<Variable> vars = new HashSet<Variable>();
		for (Update u: s.substitution)
			vars.add(u.variable);
		for (Variable v1: vars)
			for (Variable v2: Variable.searchVariables(newt))
				// check if there are still variables to be substituted in the resulting term
				if (v1.equals(v2)) {
					// if so, recursion
					Term newnewt =  applySubstitution(newt,s);
					newt = newnewt;
				}
		return newt;
	}
	
	/**
	 * This method makes it possible to apply a substitution to a term, 
	 * which results in a new term: one of the variables
	 * has been replaced by its substitutions according to the given substitution.
	 * @param t This is the term to which the substitution will be applied.
	 * @param s This is the substitution that is to be applied to t.
	 * @return The term that is returned is the result of the application of s to t.
	 */
	private static Term applySubstitution2 (Term t, Substitution s) {
		if (s.substitution.isEmpty())
			System.out.println("Error: substitution is empty.");
		for (Update u: s.substitution) {
			if (u==null)
				System.out.println("Error: update is empty.");
			break;
		}
		Term newt = null;
		// Create a new Term consisting of exactly the same things as the original term.
		if (t.function!=null && t.subterms!=null) {
			newt = new Term(t);
			// except replace all the variables with their substitution
			for (int i=0;i<newt.subterms.length;i++) {
				if (newt.subterms[i]!=null) {
					Term newsubterm = applySubstitution2(newt.subterms[i],s);
					if (!(newsubterm==null) && !(newt.subterms[i].equals(newsubterm))) {
						newt.subterms[i] = newsubterm;
						break;
					}
				}
			}
		} else if (t.x!=null) {
			// If the term contains a variable, substitute it accordingly.
			for (Update update: s.substitution) {
				if (update!=null && update.variable!=null 
						&& t.x.name == update.variable.name) {
					newt = new Term(update.term);
					break;
				}
			}
		}
		return newt;
	}
	
	/**
	 * This method creates a substitution that maps the decomposed term 
	 * (the base context and the alien vector), as to be able to reassemble 
	 * the components into the original/changed term.
	 * @param term This is the term that is decomposed
	 * @param tallAliens These are the tall aliens in the term
	 * @return this is the substitution that is created to decompose the term into
	 * base and tall aliens.
	 */
	public static Substitution getDecomposition (Term term, Term[] tallAliens) {
		// create a new substitution to save the updates
		Substitution decomposition = new Substitution();
		int i=0;
		for (Term alien: tallAliens) {
			// create a variable term
			Variable newvar = new Variable("x"+i);
			// save the variable term and the tall alien as an update into 'decomposition'
			decomposition.substitution.add (new Update(newvar,alien));
			i++;
		}
		return decomposition;
	}
}
