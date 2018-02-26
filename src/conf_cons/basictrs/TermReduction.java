package conf_cons.basictrs;

import java.util.*;

import conf_cons.gui.*;
import conf_cons.conf.*;

/**
 * 
 * TermReduction is a class containing general methods that are used to reduce a term.
 * 
 * @author Marieke Peeters
 *
 */
public class TermReduction {
	
	/**
	 * This method is used to initialize the method reductionArrays with the empty arraylist
	 * @param startTerm the term that is to be reduced
	 * @param numberOfSteps the number of steps, the length of the reductionrow that is to be created
	 * @param useNumberofSteps false if the term is to be reduced up to its normal form, true if the term is to be reduced for a certain number of steps
	 * @param trs the TRS from which rules are used for reduction
	 * @return the array of reductionsteps is returned
	 */
	public static ArrayList<Reductionstep> reductionArrayInitialize (Term startTerm, int numberOfSteps, boolean useNumberofSteps, GeneralTRS trs) {
		ArrayList<Reductionstep> empty = new ArrayList<Reductionstep>();
		return reductionArray (startTerm, empty, numberOfSteps, useNumberofSteps, trs);
	}
	
	/**
	 * This method produces a reductionArray for a certain term in a certain set of TRS's
	 * @param startTerm the term that is to be reduced
	 * @param reductionrow the reductionrow that is passed through the method recursively
	 * @param numberOfSteps the number of steps, the length of the reductionrow that is to be created
	 * @param useNumberofSteps false if the term is to be reduced up to its normal form, true if the term is to be reduced for a certain number of steps
	 * @param trs the TRS from which rules are used for reduction
	 * @return the array of reductionsteps is returned
	 */
	private static ArrayList<Reductionstep> reductionArray (Term startTerm, ArrayList <Reductionstep> reductionrow, 
														int numberOfSteps, boolean useNumberofSteps, GeneralTRS trs) {
		// a new array of reductionsteps is created
		ArrayList <Reductionstep> newArray = null;
		// all possible contractions of the startterm are computed
		recursiveContraction (startTerm,trs);
		// if the term is in normal form or the number of steps chosen is reached
		if (startTerm.normalForm | (useNumberofSteps&&numberOfSteps==0)) {
			// the end of the reductionrow is reached, so if it was not empty
			if (!reductionrow.isEmpty())
				return reductionrow;
		} else {
			// If the term is not in normal form, another contraction is possible
			// keep track of the previous steps by putting them in an arraylist
			// that is passed through the recursion.
			ArrayList<Reductionstep> passingArray = new ArrayList<Reductionstep>();
			// create an array of the past steps
			for (Reductionstep rs: reductionrow)
				passingArray.add(new Reductionstep(rs));
			// pick a new reductionstep for the current resultterm
			for (Reductionstep rs: startTerm.possibleContractions) {
				// add the chosen step to the list
				passingArray.add(new Reductionstep(rs));
				// recursion on resultterm
				newArray = reductionArray(rs.resultTerm, passingArray, numberOfSteps-1, useNumberofSteps, trs);
				break;
			}
		}
		// if no new reduction was found, the method returns null
		// this means the term could not be reduced, so it was either a normal form,
		// or all possible reductions were already found.
		return newArray;
	}
	
	/**
	 * This method recursively calls on the method contract. The first 
	 * (couple of) time(s) the term itself is contracted. If that does not work (anymore), 
	 * it will try to recursively contract the subterms. Reductionsteps for the term are 
	 * created and added to the set of possible contractions for the term.
	 * @param startTerm the term that is to be contracted
	 * @param trs the TRS from which rules are used for reduction
	 */
	private static void recursiveContraction (Term startTerm, GeneralTRS trs){
		// check if term is not already contracted, otherwise contract term...
		if (startTerm.contractAttempt==false) {
			Reductionstep reductionstep = contract(startTerm,trs);
			// if contraction possible
			if (reductionstep!=null) {
				startTerm.normalForm=false;
				// add reductionstep
				addReductionstep(reductionstep,startTerm,trs);
			}
			// when done with contracts, reduce subterms
			if (startTerm.subterms!=null) {
				for (int s=0; s<startTerm.subterms.length;s++) {
					Term currentSubterm = null;
					if (startTerm.subterms[s]!=null)
						currentSubterm = new Term(startTerm.subterms[s]);
					if (currentSubterm!=null) {
						// try to reduce current subterm
						recursiveContraction (currentSubterm, trs);
						// if possible reductions for current subterm exist, work them up to root of startterm
						if (!currentSubterm.possibleContractions.isEmpty()){
							// create a reductionstep that nests the possible reduction into the original term
							for (Reductionstep currentStep: currentSubterm.possibleContractions) {
								Term resultTerm = new Term(startTerm);	// reduced term is startterm
								resultTerm.subterms[s] = new Term(currentStep.resultTerm);
								Rule appliedRule = new Rule (currentStep.appliedRule.lefthandside,currentStep.appliedRule.righthandside);
								Position positionApplied = new Position (currentStep.positionApplied.nestPositions(s).position);
								// create reductionstep of these findings
								Reductionstep nestedReductionstep = new Reductionstep
										(startTerm,resultTerm,appliedRule,positionApplied);
								addReductionstep (nestedReductionstep, startTerm, trs);
							}
						}
					}
				}
			}
		// if startTerm has no possible reductions after this attempt, it is a normal form
		if (startTerm.possibleContractions.isEmpty())
			startTerm.normalForm();
		// remember the term is already contracted
		startTerm.contractAttempt=true;
		}
	}
	
	/**
	 * This method is used to contract a term by applying a rule to it.
	 * @param startTerm the term that is being contracted
	 * @param trs the TRS from which rules are used for reduction
	 * @return The method returns the resulting reductionstep
	 */
	private static Reductionstep contract (Term startTerm, GeneralTRS trs) {
		Rule appliedRule = null;
		ArrayList<Integer> positionApplied = new ArrayList<Integer>();
		positionApplied.add(0);
		Reductionstep redst = null;
		if (startTerm.x!=null)
			return redst;
		// Try all possible rules from given trs's.
		for (Rule r: trs.rules) {
			appliedRule = r;
			// Check if terms (startterm and lefthandside of rule) are equal.
			if (r.lefthandside.equals(startTerm)) {
				// If terms equal: result is righthandside of rule.
				Position position = new Position(positionApplied);
				redst = new Reductionstep(startTerm,new Term(r.righthandside),appliedRule,position);
				// check if the reductionstep was already found
				boolean newRedFound = checkIfContractionNew (startTerm, redst);
				// else: stop, goal reached
				if (newRedFound==true) {
					break;
				}
			// If not equal, check if headsymbols equal.
			} else if (startTerm.function.equals(r.lefthandside.function)) {
				// Check for possible substitution to make term and lefthandside match.
				if (r.lefthandside.equalTermsVariables(startTerm)) {
					// There might exist a substitution that will make terms equal. 
					// So try to create substitution.
					Substitution substitution = Substitution.substitutionUnification(r.lefthandside,startTerm);
					if (!substitution.substitution.isEmpty()) {
						// If substitution exists, apply to righthandside of rule.
						Term resultTerm = Substitution.applySubstitution(r.righthandside, substitution);
						Position position = new Position(positionApplied);
						// Create reductionstep from found information.
						redst = new Reductionstep (startTerm, resultTerm, r, position);
						// Check if found reductionstep is new
						boolean newRedFound = checkIfContractionNew (startTerm, redst);
						if (newRedFound==true) {
							// If reductionstep is new: Goal reached.
							break;
						}
					}
				}
			}
		}
		return redst;
	}
	
	/**
	 * This method checks if a reductionstep is new, and if so, 
	 * it adds the reductionstep to the set of possible reductionsteps for term t.
	 * @param redst the reductionstep that was found
	 * @param t the term for which the step was found
	 * @param trs the TRS from which rules are used for reduction
	 */
	private static void addReductionstep(Reductionstep redst, Term t, GeneralTRS trs) {
		// Check if reductionstep is new.
		boolean newRedFound = checkIfContractionNew (t, redst);
		// If so: add reductionstep to possible reductionsteps.
		if (newRedFound==true) {
			t.possibleContractions.add(redst);
			// And look for more possible reductionsteps on this term.
			recursiveContraction(t,trs);
		}
	}
	
	/**
	 * This method checks if a found reduction was already in the
	 * set of possible reductionsteps. It returns true if the found reduction is new, 
	 * otherwise it returns false.
	 * @param t The term that is contracted in this reductionstep.
	 * @param redst The reductionstep that is to be added if it is new.
	 * @return true if new, false if already in set.
	 */
	private static boolean checkIfContractionNew (Term t, Reductionstep redst){
		if (redst == null)
			return false;
		if (!t.possibleContractions.isEmpty()) {
			for (Reductionstep rs: t.possibleContractions) {
				if (redst.equals(rs))  {
					return false;
				}
			}
		}
		return true;
	}
}