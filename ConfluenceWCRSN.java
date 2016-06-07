package trs;
import java.util.*;

/**
 * ConfluenceWCRSN is used to find the common reduct of two diverging steps for 
 * a TRS that is SN and WCR. It computes the normal form for both terms and 
 * checks if they are equal.
 * 
 * @author Marieke Peeters
 * 
 */
public class ConfluenceWCRSN {
	
	public static String[] outputConfl;
	public static String[] outputResult;
	private static Rule empty;
	
	/**
	 * This method computes the confluence proof for two terms that are the result of a certain divergence.
	 * @param term1 This is the resultterm of the left reductionrow.
	 * @param term2 This is the resultterm of the right reductionrow.
	 * @param trs This is the trs that is used to reduce the terms to their normal forms.
	 * @return the method returns an arraylist of arraylists of reductionsteps, the proof can be found
	 * in a static array of strings in the class: outputResult.
	 */
	public static ArrayList<ReductionRow> confluenceWCRSN (Term term1, Term term2, GeneralTRS trs) {
		Term t1 = new Term(term1);
		Term t2 = new Term(term2);
		// The idea is to find the two normal forms and check whether they are the same
		Term[] normalform = new Term[2];
		// It may be necessary to reduce the terms in order to reach their normal forms
		ArrayList<Reductionstep> leftRow, rightRow;
		leftRow = new ArrayList<Reductionstep>();
		rightRow = new ArrayList<Reductionstep>();
		leftRow = TermReduction.reductionArrayInitialize(t1,0,false,trs);
		// if term 1 is already in normal form, continue with term 2
		if (t1.normalForm && leftRow==null) {
			normalform[0] = t1;
		// if term 1 is not yet in normal form, reduce it until it is in normal form
		} else {
			for (Reductionstep rs: leftRow)
				if (rs.resultTerm.normalForm)
					normalform[0] = rs.resultTerm;
		}
		rightRow = TermReduction.reductionArrayInitialize(t2,0,false,trs);
		if (t2.normalForm && rightRow==null) {
			normalform[1] = t2;
		} else {
			for (Reductionstep rs: rightRow)
				if (rs.resultTerm.normalForm)
					normalform[1] = rs.resultTerm;
		}
		// if there were no steps taken, create the empty step and put it in the result array
		empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
		ArrayList<Integer> nul = new ArrayList<Integer>();
		nul.add(0);
		Position emptypos = new Position(nul);
		if (leftRow==null) {
			Reductionstep emptyrs = new Reductionstep(new Term(t1), new Term(t1),empty,emptypos);
			ArrayList<Reductionstep> emptyArray = new ArrayList<Reductionstep>();
			emptyArray.add(emptyrs);
			leftRow = emptyArray;
		}
		if (rightRow==null) {
			Reductionstep emptyrs = new Reductionstep(new Term(t2), new Term(t2),empty,emptypos);
			ArrayList<Reductionstep> emptyArray = new ArrayList<Reductionstep>();
			emptyArray.add(emptyrs);
			rightRow = emptyArray;
		}
		// create output
		ArrayList<ReductionRow> rrs = new ArrayList<ReductionRow>();
		rrs.add(new ReductionRow(leftRow,trs));
		rrs.add(new ReductionRow(rightRow,trs));
		ArrayList<ReductionRow> remunderlrrs = UnderlinedTRS.removeUnderlining(t1,t2,rrs,trs);
		createOutput(t1,t2,remunderlrrs.get(0),remunderlrrs.get(1),trs,normalform);
		return rrs;
	}
	
	/**
	 * This method creates the actual description of the convergence
	 * @param t1 The first term that is to be reduced to normal form
	 * @param t2 The second term that is to be reduced to normal form
	 * @param leftRow The left reduction row
	 * @param rightRow The right reduction row
	 * @param trs this is the trs in which the reductions take place
	 * @param normalform The two calculated normal forms
	 */
	private static void createOutput(Term t1, Term t2, ReductionRow leftRow, ReductionRow rightRow, GeneralTRS trs, Term ... normalform) {
		ArrayList<Reductionstep> left = new ArrayList<Reductionstep>();
		ArrayList<Reductionstep> right = new ArrayList<Reductionstep>();
		for (Reductionstep rs: leftRow.rr) {
			if (!rs.appliedRule.equals(empty)) {
				left.add(rs);
			}
		}
		for (Reductionstep rs: rightRow.rr) {
			if (!rs.appliedRule.equals(empty)) {
				right.add(rs);
			}
		}
		// create string array
		if (left.isEmpty()) {
			if (right.isEmpty())
				outputConfl = new String [6];
			else
				outputConfl = new String [6 + right.size()];
		} else {
			if (right.isEmpty())
				outputConfl = new String[6+left.size()];
			else 
				outputConfl = new String[6 + left.size() + right.size()];
		}
		// fill string array with output text
		outputConfl[0] = ("Starting closure of the reductiongraph using Newman's lemma. Reducing terms to normal forms: ");
		if (left.isEmpty()) {
			outputConfl[1] = ("The first reduction row is empty, because the term " + t1.termToString() + " is already in normal form.");
			outputConfl[2] = ("Continuing with the second reduction row.");
			if (right.isEmpty()) {
				outputConfl[3] = ("The second reduction row is empty, because the term " + UnderlinedTRS.removeUnderliningTerm(t2,trs).termToString() + " is already in normal form.");
				outputConfl[4] = ("Continuing with the rest of the proof.");
				// Check if the normalforms are equal.
				if (normalform[0] != null && normalform[1]!=null && normalform[0].equals(normalform[1])) {
					outputConfl[5] = ("Found a common reduct for the two terms: " + normalform[0].termToString() + ".");
				} else {
					outputConfl[5] = ("Error: The two terms do not have a common reduct!");
				}
			} else {
				outputConfl[3] = ("Reduction to normal form for term " + UnderlinedTRS.removeUnderliningTerm(t2,trs).termToString() + ":");
				int j=0;
				for (Reductionstep rs: right) {
					outputConfl[4 + j] = rs.reductionToString();
					j++;
				}
				outputConfl[4 + right.size()] = ("End of reductionRow.");
				// Check if the normalforms are equal.
				if (normalform[0] != null && normalform[1]!=null && normalform[0].equals(normalform[1])) {
					outputConfl[5 + right.size()] = ("Found a common reduct for the two terms: " + normalform[0].termToString() + ".");
				} else {
					outputConfl[5 + right.size()] = ("Error: The two terms do not have a common reduct!");
				}
			}
		} else {
			outputConfl[1] = ("Reduction to normal form for term " + UnderlinedTRS.removeUnderliningTerm(t1,trs).termToString() + ":");
			int i=0;
			for (Reductionstep rs: left) {
				outputConfl[2+i] = rs.reductionToString();
				i++;
			}
			outputConfl[2 + left.size()] = ("End of reductionRow.");
			if (rightRow.rr==null) {
				outputConfl[3 + left.size()] = ("The second reduction row is empty, because the term " + UnderlinedTRS.removeUnderliningTerm(t2,trs).termToString() + " is already in normal form.");
				outputConfl[4 + left.size()] = ("Continuing with the rest of the proof.");
				// Check if the normalforms are equal.
				if (normalform[0] != null && normalform[1]!=null && normalform[0].equals(normalform[1])) {
					outputConfl[5 + left.size()] = ("Found a common reduct for the two terms: " + normalform[0].termToString() + ".");
				} else {
					outputConfl[5 + left.size()] = ("Error: The two terms do not have a common reduct!");
				}
			} else {
				outputConfl[3 + left.size()] = ("Reduction to normal form for term " + UnderlinedTRS.removeUnderliningTerm(t2,trs).termToString() + ":");
				int j=0;
				for (Reductionstep rs: right) {
					outputConfl[4 + left.size() + j] = rs.reductionToString();
					j++;
				}
				outputConfl[4 + left.size() + right.size()] = ("End of reductionRow.");
				// Check if the normalforms are equal.
				if (normalform[0] != null && normalform[1]!=null && normalform[0].equals(normalform[1])) {
					outputConfl[5 + left.size() + right.size()] = ("Found a common reduct for the two terms: " + normalform[0].termToString() + ".");
				} else {
					outputConfl[5 + left.size() + right.size()] = ("Error: The two terms do not have a common reduct!");
				}
			}
		}
	}	
}
