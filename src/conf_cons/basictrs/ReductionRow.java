package conf_cons.basictrs;

import java.util.*;

import conf_cons.gui.*;
import conf_cons.conf.*;

/**
 * ReductionRow objects contain an array of reductionsteps, a combinedtrs
 * from which the terms and the reductionsteps are coming and a steptype:
 * the steps can be of type short, or of type tall.
 * 
 * @author Marieke Peeters
 *
 */
public class ReductionRow {
	
	/**
	 * Steptype can either be empty, short or tall. If the reductionstep was taken upon the base part of the term
	 * the step is short, if it was taken upon one of the tall aliens of the term, the step is tall. If a term
	 * is not reduced during a step, but has remained the same, the step taken upon the term is called 'the empty
	 * step'.
	 * @author Marieke Peeters
	 *
	 */
	public enum StepType {
		Shortstep, Tallstep, Emptystep
	}
	public ArrayList<Reductionstep> rr;
	public GeneralTRS trs;
	public StepType stepType;
	
	/**
	 * The constructor ReductionRow (ArrayList<Reductionstep>, GeneralTRS) is used to create 
	 * and save an array of reductionsteps for a term of rank 1 and 1 original trs, it is only called
	 * upon in ConfluenceWCRSN which is a confluence method for terms of rank 1.
	 * @param reductionrow this is the arraylist of reductionsteps
	 * @param thistrs this is an original trs
	 */
	public ReductionRow (ArrayList<Reductionstep> reductionrow, GeneralTRS thistrs) {
		rr = reductionrow;
		trs = thistrs;
		stepType = StepType.Shortstep;
	}
	
	/**
	 * the constructor ReductionRow (ArrayList<Reductionstep>, int, GeneralTRS) is used to create 
	 * and save an array of reductionsteps for a term of rank above 1 and a combined trs
	 * @param reductionrow this is the arraylist of reductionsteps
	 * @param rank this is the rank of the startterm of the first reductionstep
	 * @param ctrs this is the combined trs the steps and terms are built from
	 */
	public ReductionRow (ArrayList<Reductionstep> reductionrow, int rank, GeneralTRS ctrs) {
		rr = new ArrayList<Reductionstep>();
		for (Reductionstep rs: reductionrow)
			rr.add(rs);
		trs = ctrs;
		Rule empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
		for (Reductionstep rs: rr) {
			if (!rs.appliedRule.equals(empty))
				stepType = getStepType(rs,rank,ctrs);
		}
		if (stepType==null)
			stepType=StepType.Emptystep;
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
		// if the reductionrow does not contain a reductionrow,a trs or a steptype, something is wrong
		else if (rr==null||trs==null)
			equal = false;
		// if all the elements of the objects are equal, so are the objects
		else {
			ReductionRow redrow  = (ReductionRow)obj;
			if (redrow.rr==null || 
					redrow.trs==null || redrow.stepType==null)
				equal=false;
			else if (!trs.equals(redrow.trs)|| 
					!(stepType.equals(redrow.stepType)))
				equal = false;
			else if (rr.size()!=redrow.rr.size())
					equal = false;
			else {
				for (int i=0; i<rr.size(); i++) {
					if (!rr.get(i).equals(redrow.rr.get(i))){
						equal=false;
						break;
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
			hash = 31 * hash + (null == trs ? 0 : trs.hashCode());
			hash = 31 * hash + (null == stepType ? 0 : stepType.hashCode());
			if (rr!=null)
				for (Reductionstep rs: rr)
					hash = 31 * hash + (null == rs ? 0 : rs.hashCode());
		return hash;
	}
	
	/**
	 * This method is used to determine the stepType of a reductionstep
	 * it can be short: a step upon the base of the term
	 * or it can be tall: a step upon one of the tall aliens of the term
	 * @param rs this is the reductionstep that needs to be classified as being short or tall
	 * @param rank this is the rank of the startterm
	 * @param ctrs this is the trs in which the steps were performed
	 * @return the steptype is returned
	 */
	public static StepType getStepType (Reductionstep rs, int rank, GeneralTRS ctrs) {
		Term[] tallAliens = CrankRterm.getAliens(rank,rs.startTerm,ctrs);
		StepType type = null;
		Rule empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
		if (rs.appliedRule.equals(empty)) {
			type = StepType.Emptystep;
		} else {
			type = StepType.Shortstep;
			for (Term alien: tallAliens) {
				ArrayList<Position> alienPos = Position.findPosition(rs.startTerm,alien);
				// if there is an alien at the position, it was a tall step
				for (Position alpos: alienPos)
					if (alpos.isPrefix(rs.positionApplied))
						type = StepType.Tallstep;
			}
		}
		return type;
	}
	
	/**
	 * This method checks whether an array of steps contains only steps of the same steptype (empty, short or tall)
	 * @param rr this is the array of steps
	 * @param rank this is the rank of the startterm
	 * @param ctrs this is the trs in which the steps were performed
	 * @return the method returns true when all steps are of the same type, 
	 * it returns false if the steps are of different types
	 */
	public static boolean checkStepType (ArrayList<Reductionstep> rr,int rank, GeneralTRS ctrs) {
		// get steptype of first step
		StepType firststep = getStepType (rr.get(0),rank,ctrs);
		// start looping through steps
		for (Reductionstep rs: rr)
			// if steptype of a certain step is different from the first step, something is wrong
			if (firststep!=getStepType(rs,rank,ctrs))
					return false;
		return true;
	}
	
	/**
	 * Tis method creates an arrayList of reductionrows from an arrayList of reductionsteps
	 * @param steps these are the steps that were performed upon the startTerm (an arraylist of steps)
	 * @param rank this is the rank of the startterm that is reduced
	 * @param ctrs this is the trs in which the steps are taken
	 * @return The method returns a sorted arrayList of reductionrows (tall steps first, short steps last)
	 */
	public static ArrayList<ReductionRow> createReductionRowArray (ArrayList<Reductionstep> steps,int rank,GeneralTRS ctrs) {
		// create an arraylist of reductionrows
		ArrayList<ReductionRow> result = new ArrayList<ReductionRow>();
		// be prepared to split the array of steps into tall and short steps
		ArrayList<ArrayList<Reductionstep>> aar = new ArrayList<ArrayList<Reductionstep>>();
		// initialize
		aar.add(new ArrayList<Reductionstep>());
		StepType steptype;
		// get steptype of first step
		if (!steps.isEmpty())
			steptype = getStepType(steps.get(0),rank,ctrs);
		else
			steptype = StepType.Emptystep;
		// start looping through steps
		int i=0;
		for (Reductionstep rs: steps) {
			// if the steptype is different, start a new array of steps
			if (getStepType(rs,rank,ctrs)!=steptype) {
				steptype = getStepType(rs,rank,ctrs);
				aar.add(new ArrayList<Reductionstep>());
				i++;
			}
			// add the step to the current array
			aar.get(i).add(new Reductionstep(rs));
		}
		// create reductionrows from the arrays of steps
		if (!aar.get(0).isEmpty())
			for (ArrayList<Reductionstep> ar: aar)
				result.add(new ReductionRow(ar,rank,ctrs));
		// check again if the reductionrows all contain same steptypes
		for (ReductionRow rr: result)
			if (!checkStepType(rr.rr,rank,ctrs))
				System.out.println("Error: different steptypes in reductionrow!");
		return result;
	}
}
