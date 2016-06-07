package trs;

import java.util.ArrayList;

/**
 * 
 * ShortShort is used to find the common reduct of two diverging reductionrows of short steps 
 * in a combined TRS. It is part of the implementation of the algorithm presented in a 
 * paper by V. van Oostrom: Modularity of Confluence, constructed (2008).
 * 
 * @author Marieke Peeters
 *
 */
public class ShortShort extends ConfluenceModular {
	
	// this printing instruction prints all choices that are made for different reduction strategies
	static boolean print1 = false;
	// this printing instruction prints all in-between steps of the short-short solution
	static boolean print3 = false;
	
	/**
	 * This method computes the convergence of a short-short tile of the diverging sequences.
	 * @param stepsLeft this is the diverging reductionrow on the left side
	 * @param stepsRight this is the diverging reductionrow on the right side
	 * @param combinedtrs this is the combined trs from which the original problem came
	 * @return the method returns an arrayList of reductionrows. The first one is the left converging
	 * reductionrow, the second one is the right converging reductionrow.
	 */
	public static ArrayList<ArrayList<ReductionRow>> shortShort (ArrayList<ReductionRow> stepsLeft, 
											ArrayList<ReductionRow> stepsRight, GeneralTRS combinedtrs) {
		if (print1)
			System.out.println("Both rows consist of short steps: going for short-short solution.");
		// get decomposition, to be able to put aliens back in place once base steps are computed
		Term term = stepsLeft.get(0).rr.get(0).startTerm;
		int rank = CrankRterm.getRank(term, combinedtrs);
		Term[] tallAliens = CrankRterm.getAliens(rank, term, combinedtrs);
		Substitution decomposition = Substitution.getDecomposition(term,tallAliens);
		Term base = CrankRterm.getBase(decomposition,stepsLeft.get(0).rr.get(0).startTerm);
		// convert to base-steps (remove tall aliens by applying substitution)
		ArrayList<ArrayList<ReductionRow>> baseArrays = getBaseArrays(stepsLeft,stepsRight,decomposition,base);
		// get arrays of steps from confluenceModular
		ArrayList<ArrayList<ReductionRow>> resultArrays = ConfluenceModular.confluenceModular(baseArrays.get(0), 
				baseArrays.get(1),CrankRterm.getRank(base,combinedtrs),combinedtrs);
		// put the aliens back in place
		resultArrays = getOrigFromBase(resultArrays,decomposition,combinedtrs,rank);
		return resultArrays;
	}
	
	/**
	 * getBaseArrays is used to transform the reductionrows upon a term into the reductionsteps
	 * upon the base of the term.
	 * @param stepsLeft this the reductionrow on the left
	 * @param stepsRight this is the reductionrow on the right
	 * @param decomposition this is the decomposition of the term
	 * @param base this is the base of the startTerm
	 * @return the method returns the transformed reductionrows
	 */
	private static ArrayList<ArrayList<ReductionRow>> getBaseArrays(ArrayList<ReductionRow> stepsLeft, 
								ArrayList<ReductionRow> stepsRight, Substitution decomposition, Term base) {
		// get crucial information
		GeneralTRS ctrs = stepsLeft.get(0).trs;
		int newrank = CrankRterm.getRank(base,ctrs);
		// create new arrays of reductionsteps on base
		ArrayList<Reductionstep> stepsBaseLeft = getStepsBase(stepsLeft,decomposition,ctrs);
		ArrayList<Reductionstep> stepsBaseRight = getStepsBase(stepsRight,decomposition,ctrs);
		if (print3) {
			System.out.println("Got base steps from original steps:");
			System.out.println("Base steps on the left:");
			for (Reductionstep rs: stepsBaseLeft)
				rs.printReduction();
			System.out.println("Base steps on the right:");
			for (Reductionstep rs: stepsBaseRight)
				rs.printReduction();
		}
		// the reductionsteps need to be put in arraylists of reductionrows, 
		// because of the argument types of confluenceModular	
		return ConfluenceModular.createArrays(ReductionRow.createReductionRowArray(stepsBaseLeft,newrank,ctrs),
				ReductionRow.createReductionRowArray(stepsBaseRight,newrank,ctrs));
	}
	
	/**
	 * this method computes baseSteps from an arraylist of reductionrows.
	 * @param steps it needs the steps that are to be transformed
	 * @param decomposition this is the decomposition that gets you the base
	 * @param ctrs this is the combined trs in which the reductions took place
	 * @return the method returns the base steps
	 */
	public static ArrayList<Reductionstep> getStepsBase (ArrayList<ReductionRow> steps,
															Substitution decomposition,GeneralTRS ctrs) {
		ArrayList<Reductionstep> stepsBase = new ArrayList<Reductionstep>();
		// replace all terms by their base according to the decomposition
		for (ReductionRow rr: steps)
			for (Reductionstep rs: rr.rr) {
				Term startTerm = CrankRterm.getBase(decomposition,rs.startTerm);
				Term resultTerm = CrankRterm.getBase(decomposition,rs.resultTerm);
				stepsBase.add(new Reductionstep(new Term(startTerm), new Term(resultTerm),rs.appliedRule,rs.positionApplied));
			}
		return stepsBase;
	}
	
	/**
	 * getOrigFromBase is a method to reverse the transformation that took place in getBaseArrays. it puts the aliens back
	 * in place, in accordance with the substitution of the decomposition.
	 * @param resultArrays these are the base arrays
	 * @param decomposition this is the decomposition
	 * @param ctrs this is the combined trs in which the reductions took place
	 * @param rank this is the rank of the startTerm in the divergence
	 * @return these are the resulting arrays, after the aliens were put back in place
	 */
	private static ArrayList<ArrayList<ReductionRow>> getOrigFromBase (ArrayList<ArrayList<ReductionRow>> resultArrays, 
			Substitution decomposition,GeneralTRS ctrs, int rank) {
		// create reduction arrays that contain the original steps (put aliens back) and return them
		ArrayList<Reductionstep> resultLeft = getOrigBase(resultArrays.get(0),decomposition,ctrs);
		ArrayList<Reductionstep> resultRight = getOrigBase(resultArrays.get(1),decomposition,ctrs);
		if (print3) {
			System.out.println("Created complete steps from converging base steps:");
			System.out.println("Completed steps on the left:");
			for (Reductionstep rs: resultLeft)
				rs.printReduction();
			System.out.println("Completed steps on the right:");
			for (Reductionstep rs: resultRight)
				rs.printReduction();
		}
		// the reductionsteps need to be put in arraylists of reductionrows, 
		// because of the argument types of confluenceModular
		return ConfluenceModular.createArrays(ReductionRow.createReductionRowArray(resultLeft,rank,ctrs),
				ReductionRow.createReductionRowArray(resultRight,rank,ctrs));
	}
	
	/**
	 * this method is to be used when the original steps are to be retrieved from base steps
	 * @param array this is the array of base steps
	 * @param decomposition this is the decomposition that took the aliens out of the base
	 * @param ctrs this is the combined trs in which the reductions took place
	 * @return this method returns the original reductionsteps
	 */
	private static ArrayList<Reductionstep> getOrigBase (ArrayList<ReductionRow> array, 
			Substitution decomposition,GeneralTRS ctrs) {
		// put all the aliens back into the base steps and return that array of steps
		ArrayList<Reductionstep> result = new ArrayList<Reductionstep>();
		for (ReductionRow rr: array)
			for (Reductionstep rs: rr.rr) {
				Term startTerm = Substitution.applySubstitution(rs.startTerm,decomposition);
				Term resultTerm = Substitution.applySubstitution(rs.resultTerm,decomposition);
				result.add(new Reductionstep(new Term(startTerm),new Term(resultTerm),rs.appliedRule,rs.positionApplied));
			}
		return result;
	}
	
	
	
}
