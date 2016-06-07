package trs;

import java.util.ArrayList;

/**
 * 
 * Mix is used to find the common reduct of two diverging reductionrows of which one
 * contains only tall steps and the other contains only short steps in a combined TRS. 
 * It is part of the implementation of the algorithm presented in a 
 * paper by V. van Oostrom: Modularity of Confluence, constructed (2008).
 * 
 * @author Marieke Peeters
 *
 */
public class Mix extends ConfluenceModular {
	
	// this printing instruction prints all choices that are made for different reduction strategies
	static boolean print1 = false;
	// this printing instruction prints all in-between steps of the mix solution
	static boolean print5 = false;
	
	/**
	 * This method computes the convergence of a mixed tile of the diverging sequences
	 * @param stepsLeft this is the diverging reductionrow on the left side
	 * @param stepsRight this is the diverging reductionrow on the right side
	 * @param combinedtrs this is the combined trs from which the original problem came
	 * @return the method returns an arrayList of reductionrows. The first one is the left converging
	 * reductionrow, the second one is the right converging reductionrow.
	 */
	public static ArrayList<ArrayList<ReductionRow>> mix (ArrayList<ReductionRow> stepsLeft, 
			ArrayList<ReductionRow> stepsRight, GeneralTRS combinedtrs) {
		if (print1)
			System.out.println("The rows consist of different steps: going for tall-short solution.");
		// create result arrays
		ArrayList<ArrayList<Reductionstep>> result = new ArrayList<ArrayList<Reductionstep>>();
		result.add(new ArrayList<Reductionstep>());
		result.add(new ArrayList<Reductionstep>());
		// get information about term and rank
		Term term = new Term(stepsLeft.get(0).rr.get(0).startTerm);
		int rank = CrankRterm.getRank(term,combinedtrs);
		// get information about alien vector and positions for tall steps
		ArrayList<Term> alienVectorStart = TallTall.getAlienVectorStart(term,combinedtrs);
		Position[] positionsAliens = TallTall.getAlPos(term,alienVectorStart);
		// remember what steps were tall and what steps were short
		ArrayList<ReductionRow> shortSteps,tallSteps;
		boolean tallIsLeft;
		if (stepsLeft.get(0).stepType == ReductionRow.StepType.Tallstep) {
			shortSteps = stepsRight;
			tallSteps = stepsLeft;
			tallIsLeft = true;
			if (print5)
				System.out.println("Steps on the left are tall. Steps on the right are short.");
		} else {
			shortSteps = stepsLeft;
			tallSteps = stepsRight;
			tallIsLeft = false;
			if (print5)
				System.out.println("Steps on the left are short. Steps on the right are tall.");
		}
		if (print5)
			System.out.println("Get alien diverging steps for tall-short method.");
		// get diverging alien steps
		ArrayList<ArrayList<Reductionstep>> alienDivergingSteps = TallTall.getAlienReductions1(tallSteps,positionsAliens);
		if (print5){
			System.out.println("The diverging alien steps are:");
			for (int i=0;i<alienDivergingSteps.size();i++) {
				System.out.println("Alien " + (i+1) + ":");
				for (Reductionstep rs: alienDivergingSteps.get(i)) {
					rs.printReduction();
				}
			}
		}
		ArrayList<ArrayList<Reductionstep>> alienConvergingSteps = new ArrayList<ArrayList<Reductionstep>>();
		for (int i=0;i<alienDivergingSteps.size();i++) 
			alienConvergingSteps.add(new ArrayList<Reductionstep>());
		if (print5)
			System.out.println("Get balancing converging alien steps for tall-short method.");
		// balance tall alien steps
		alienConvergingSteps = TallTall.recursiveBalancing(positionsAliens,alienVectorStart,alienDivergingSteps,combinedtrs,rank,alienConvergingSteps);
		if (print5){
			System.out.println("The converging balancing alien steps are:");
			for (int i=0;i<alienConvergingSteps.size();i++) {
				System.out.println("Alien " + (i+1) + ":");
				for (Reductionstep rs: alienConvergingSteps.get(i)) {
					rs.printReduction();
				}
			}
		}
		if (print5)
			System.out.println("Put balancing alien steps in context for tall-short method.");
		// save all alien steps (diverging&converging) in 1 arrayList
		ArrayList<ArrayList<Reductionstep>> alienSteps = combineAlienSteps(alienDivergingSteps,alienConvergingSteps);
		// put results in result arrays (all tall steps first), compute correct context first
		Term t = new Term(tallSteps.get(tallSteps.size()-1).rr.get(tallSteps.get(tallSteps.size()-1).rr.size()-1).resultTerm);
		result = saveBalancedAliens(positionsAliens, t, alienConvergingSteps, result, tallIsLeft, rank, true, combinedtrs);
		// get new context and put results in result arrays again (now all short steps)
		if (tallIsLeft)
			if (!result.get(0).isEmpty())
				t = result.get(0).get(result.get(0).size()-1).resultTerm;
		else
			if (!result.get(1).isEmpty())
				t = result.get(1).get(result.get(1).size()-1).resultTerm;
		result = saveBalancedAliens(positionsAliens, t, alienConvergingSteps, result, tallIsLeft, rank, false, combinedtrs);
		if (print5) {
			System.out.println("Created complete steps from computed alien steps:");
			if(tallIsLeft)
				for (Reductionstep rs: result.get(0))
					rs.printReduction();
			else
				for (Reductionstep rs: result.get(1))
					rs.printReduction();
		}
		// get information about substitution and base for short steps
		Term[] tallAliens = CrankRterm.getAliens(rank, term, combinedtrs);
		Substitution decomposition = Substitution.getDecomposition(term,tallAliens);
		if (print5)
			System.out.println("Get base steps for tall-short method.");
		// get base steps
		ArrayList<Reductionstep> baseSteps = ShortShort.getStepsBase(shortSteps,decomposition,combinedtrs);
		if (print5)
			System.out.println("Project base steps around reduced aliens for tall-short method.");
		// project base steps around reduced aliens to find converging steps on tall side
		result = projectBaseSteps(baseSteps,alienSteps,combinedtrs,alienVectorStart,result,tallIsLeft,decomposition);
		if (print5)
			System.out.println("Project alien steps into base context for tall-short method.");
		// project alien steps into base context to get converging array on short side
		result = projectAlienSteps(result,tallIsLeft,tallSteps,shortSteps,alienSteps,combinedtrs,rank);
		return ConfluenceModular.createArrays(ReductionRow.createReductionRowArray(result.get(0), rank, combinedtrs),
		ReductionRow.createReductionRowArray(result.get(1), rank, combinedtrs));
	}
	
	/**
	 * saveBalancedAliens is used to put all balancing steps upon the aliens back into their context.
	 * @param positionsAliens These are the positions of the aliens.
	 * @param term This is the term that is used as the context for the alien steps (the latest computed term on the tall side).
	 * @param alienConvergingSteps These are the converging alien steps (balancing steps).
	 * @param result This is the array of converging steps for the entire mix method.
	 * @param tallIsLeft This is a boolean that remembers whether the left array was tall or not (in that case, the right side is tall).
	 * @param rank This is the rank of the startTerm in the divergence that is to be converged in the mix method.
	 * @param tall This is a boolean that is used to make sure all tall steps are put in the converging array first, all short steps come last.
	 * @param combinedtrs This is the trs in which the computations take place.
	 * @return The method returns a double array of reductionsteps (it actually is 'result' with as an addition the computed steps on the tall side.
	 */
	private static ArrayList<ArrayList<Reductionstep>> saveBalancedAliens (Position[] positionsAliens, Term term, 
			ArrayList<ArrayList<Reductionstep>> alienConvergingSteps, ArrayList<ArrayList<Reductionstep>> result, boolean tallIsLeft,
			int rank, boolean tall, GeneralTRS combinedtrs) {
		// create a context term keeping track of the latest computed context
		Term t = new Term(term);
		// put back alien converging steps into startTerm and add to converging array on tall side
		for (int i=0;i<alienConvergingSteps.size();i++) {
			for (Reductionstep rs: alienConvergingSteps.get(i)) {
				if ((tall && CrankRterm.getRank(rs.startTerm,combinedtrs)==rank-1 )||
						(!tall && CrankRterm.getRank(rs.startTerm,combinedtrs)<rank-1)) {
					Reductionstep newrs = TallTall.aliensInContext2(rs,positionsAliens[i],t);
					if (tallIsLeft)
						result.get(0).add(newrs);
					else
						result.get(1).add(newrs);
					t = new Term(newrs.resultTerm);
				}
			}
		}
		return result;
	}
	
	/**
	 * combineAlienSteps combines the diverging and converging alien steps into one object (a double arraylist of reductionsteps)
	 * @param alienDivergingSteps These are the diverging steps taken upon the alien vector.
	 * @param alienConvergingSteps These are the converging steps taken upon the alien vector.
	 * @return The method returns all steps taken upon the alien vector.
	 */
	private static ArrayList<ArrayList<Reductionstep>> combineAlienSteps(ArrayList<ArrayList<Reductionstep>> alienDivergingSteps,
			ArrayList<ArrayList<Reductionstep>> alienConvergingSteps) {
		// create a new arraylist of alien steps (one list of reductions for each alien)
		ArrayList<ArrayList<Reductionstep>> alienSteps = new ArrayList<ArrayList<Reductionstep>>();
		// put all diverging and all converging steps into the arraylists
		for (int k=0;k<alienDivergingSteps.size();k++) {
			alienSteps.add(new ArrayList<Reductionstep>());
			for (Reductionstep rs: alienDivergingSteps.get(k))
				alienSteps.get(k).add(new Reductionstep (rs));
			for (Reductionstep rs: alienConvergingSteps.get(k))
				alienSteps.get(k).add(new Reductionstep (rs));
		}
		return alienSteps;
	}
	
	/**
	 * getAlienVectorsBaseSteps is a method used to compute all steps taken upon 
	 * @param baseSteps These are the steps taken upon the base (derived from the short steps).
	 * @return The method returns a vector of the aliens (variables) found in the term for each base step.
	 */
	private static ArrayList<ArrayList<Term>> getAlienVectorsBaseSteps (ArrayList<Reductionstep> baseSteps) {
		ArrayList<ArrayList<Term>> alienVectorBase = new ArrayList<ArrayList<Term>>();
		// First get the vector of the startTerm
		ArrayList<Term> alienVectorStart = getAlienVectorBaseStep(baseSteps.get(0).startTerm);
		alienVectorBase.add(alienVectorStart);
		// Then, for each base step, get the vector of the resultTerm
		for (Reductionstep rs: baseSteps) {
			Term resultTerm = new Term(rs.resultTerm);
			ArrayList<Term> alienVectorNew = getAlienVectorBaseStep(resultTerm);
			alienVectorBase.add(alienVectorNew);
		}
		// return all these vectors
		return alienVectorBase;
	}
	
	/**
	 * getAlienVectorBaseStep is a method that finds all variables in a term and adds them to an arrayList
	 * @param term This is the term that needs to be sorted out.
	 * @return The method returns a vector of variable terms.
	 */
	private static ArrayList<Term> getAlienVectorBaseStep(Term term) {
		ArrayList<Term> varTerms = new ArrayList<Term>();
		// if a term is a variable, it is a former alien
		if (term.x!=null)
			varTerms.add(new Term(term));
		// otherwise check if one of its subterms is an alien (variable)
		else if (term.subterms!=null)
			for (Term t: term.subterms)
				for (Term t1: getAlienVectorBaseStep(t))
					varTerms.add(new Term(t1));
		return varTerms;
	}
	
	/**
	 * projectBaseSteps is used to project base steps containing the computed resulting aliens (after balancing)
	 * @param baseSteps These are the (diverging) base steps taken on the short side.
	 * @param alienSteps These are the (converging) alien steps taken on the tall side.
	 * @param combinedtrs This is the trs in which all steps took place.
	 * @param alienVectorStart This is the vector of aliens in the original start term.
	 * @param result These are the resulting arrays of converging steps for the whole mix method.
	 * @param tallIsLeft This boolean tells whether the tall steps were on the left (true) or right (false) side.
	 * @param decomposition This is a substitution that is used to decompose terms into their aliens and base.
	 * @return The method returns the array of base steps projected containing the new aliens.
	 */
	private static ArrayList<ArrayList<Reductionstep>> projectBaseSteps(ArrayList<Reductionstep> baseSteps, 
			ArrayList<ArrayList<Reductionstep>> alienSteps, GeneralTRS combinedtrs, ArrayList<Term> alienVectorStart,
			ArrayList<ArrayList<Reductionstep>> result, boolean tallIsLeft, Substitution decomposition) {
		// create multiple vectors of aliens (for each base step one vector of variables)
		ArrayList<ArrayList<Term>> alienVectorsBase = getAlienVectorsBaseSteps(baseSteps);
		Substitution newSubst = new Substitution();
		if (print5)
			System.out.println("Found projected base steps for tall side:");
		for (int i=0;i<baseSteps.size();i++) {
			Rule emptyRule = new Rule (new Term(new Variable("x")),new Term(new Variable("x")));
			// if it is an empty step, ignore it
			if (!baseSteps.get(i).appliedRule.equals(emptyRule)) {
				// otherwise, check for each update in the decomposition if it occurs in the alien vector
				for (Update u: decomposition.substitution)
					for (Term t: alienVectorsBase.get(i))
						if (t.x.equals(u.variable)) {
							// if it does, replace the old alien/term in the update by the new alien
							int index = alienVectorsBase.get(0).indexOf(t);
							Update newu = null;
							// if there is no new alien, just take the old one
							if (alienSteps.get(index).isEmpty()) 
								newu = new Update(t.x,new Term(alienVectorStart.get(index)));
							// if there is, take the new one
							else
								newu = new Update(t.x,new Term(alienSteps.get(index).get(alienSteps.get(index).size()-1).resultTerm));
							// put the new update into a new substitution
							newSubst.substitution.add(newu);
						}
				// apply the new substitution to the base step
				Term startTerm = Substitution.applySubstitution(baseSteps.get(i).startTerm,newSubst);
				Term resultTerm = Substitution.applySubstitution(baseSteps.get(i).resultTerm,newSubst);
				Reductionstep rs = new Reductionstep(new Term(startTerm),new Term(resultTerm),baseSteps.get(i).appliedRule,baseSteps.get(i).positionApplied);
				// add the resulting reductionstep to the converging reduction array on the tall side
				if (print5)
					rs.printReduction();
				if (!startTerm.equals(resultTerm)) {
					if (tallIsLeft)
						result.get(0).add(rs);
					else
						result.get(1).add(rs);
				}
			}
		}
		return result;
	}
	
	/**
	 * projectAlienSteps is used to project all computed alien steps (on tall side) into reduced base context
	 * @param result These are the resulting arrays of converging steps for the whole mix method.
	 * @param tallIsLeft This boolean tells whether the tall steps were on the left (true) or right (false) side.
	 * @param tallSteps These are all the diverging tall steps taken on the tall side.
	 * @param shortSteps These are all the diverging short steps taken on the short side.
	 * @param alienSteps These are all the steps performed on the tall alien vector.
	 * @param combinedtrs This is the trs in which the computations take place.
	 * @param rank This is the rank of the startTerm in the divergence that is to be converged in the mix method.
	 * @return The method returns all alien steps put inside the new context of the base resulting from the divergence on the short side.
	 */
	private static ArrayList<ArrayList<Reductionstep>> projectAlienSteps(ArrayList<ArrayList<Reductionstep>> result,
			boolean tallIsLeft, ArrayList<ReductionRow> tallSteps, ArrayList<ReductionRow> shortSteps,
			ArrayList<ArrayList<Reductionstep>> alienSteps, GeneralTRS combinedtrs, int rank) {
		if (print5)
			System.out.println("Computing all projected alien steps for short side:");
		// get the last term on the short side
		Term lastShortTerm = new Term(shortSteps.get(shortSteps.size()-1).rr.get(shortSteps.get(shortSteps.size()-1).rr.size()-1).resultTerm);
		// create an arraylist of changed tall steps
		ArrayList<Reductionstep> changedTallSteps = new ArrayList<Reductionstep>();
		for (ArrayList<Reductionstep> als: alienSteps)
			// check which alien was the one that was left and copy all steps taken upon that alien into the array of changed tall steps
			if (!als.isEmpty() && lastShortTerm.equals(als.get(0).startTerm)) {
				for (Reductionstep rs: als) {
					changedTallSteps.add(new Reductionstep(rs));
				}
				break;
			}
		// if there is no collapsing step, just copy all alien steps into the right positions
		if (lastShortTerm.subterms!=null) {
			changedTallSteps = projectAlienSteps1(lastShortTerm, alienSteps, changedTallSteps, true, combinedtrs, rank);
			changedTallSteps = projectAlienSteps1(lastShortTerm, alienSteps, changedTallSteps, false, combinedtrs, rank);
		}
		for (Reductionstep rs: changedTallSteps) {
			if (tallIsLeft)
				result.get(1).add(rs);
			else
				result.get(0).add(rs);
			if (print5)
				rs.printReduction();
		}
		return result;
	}
	
	/**
	 * projectAlienSteps is used to project all alien steps into the (non-collapsed) context of the base on the short side.
	 * @param lastShortTerm This is the last short term on the short side.
	 * @param alienSteps These are all the steps taken upon the aliens.
	 * @param changedTallSteps These are the tall steps that have already been altered in the projectAlienSteps method.
	 * @param tall This boolean is a switch that is to be used to put only tall or only short steps into 'changedTallSteps'
	 * @param combinedtrs This is the trs in which the computations take place.
	 * @param rank This is the rank of the startTerm in the divergence that is to be converged in the mix method.
	 * @return The method returns all steps taken upon the aliens within the reduced base context coming from the short side.
	 */
	private static ArrayList<Reductionstep> projectAlienSteps1 (Term lastShortTerm, ArrayList<ArrayList<Reductionstep>> alienSteps,
			ArrayList<Reductionstep> changedTallSteps, boolean tall, GeneralTRS combinedtrs, int rank) {
		// loop through all proper subterms of the last short term
		for (Term st: lastShortTerm.subterms) {
			for (ArrayList<Reductionstep> als: alienSteps)
				// if the subterm equals a reduced alien
				if (!als.isEmpty() && st.equals(als.get(0).startTerm)) {
					// get the position of this particular subterm
					Position pos = null;
					ArrayList<Position> positions = Position.findPosition(lastShortTerm, st);
					for (Position p: positions)
						if (lastShortTerm.giveSubterm(p)==st)
							pos = new Position(p.position);
					// copy the steps taken upon that particular alien
					for (Reductionstep rs: als)
						if ((tall && CrankRterm.getRank(rs.startTerm, combinedtrs)==rank-1) ||
								(!tall && CrankRterm.getRank(rs.startTerm, combinedtrs)<rank-1)) {
							Rule empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
							if (!rs.appliedRule.equals(empty)) {
								Term resultTerm = Term.replaceSubterm(lastShortTerm,pos,rs.resultTerm);
								Position positionApplied = new Position(rs.positionApplied.position);
								for (Integer i: pos.position)
									positionApplied = positionApplied.nestPositions(i);
								if (positionApplied!=null)
									positionApplied.position.remove(pos.position.size());
								Reductionstep newrs = new Reductionstep(new Term(lastShortTerm),new Term(resultTerm),rs.appliedRule,positionApplied);
								changedTallSteps.add(newrs);
								lastShortTerm = new Term(resultTerm);
							}
						}
					break;
				}
		}
		return changedTallSteps;
	}
}
