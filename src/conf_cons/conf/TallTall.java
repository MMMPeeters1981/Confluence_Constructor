package conf_cons.conf;

import java.util.ArrayList;

import conf_cons.gui.*;
import conf_cons.basictrs.*;

/**
 * 
 * TallTall is used to find the common reduct of two diverging reductionrows of tall steps 
 * in a combined TRS. It is part of the implementation of the algorithm presented in a 
 * paper by V. van Oostrom: Modularity of Confluence, constructed (2008).
 * 
 * @author Marieke Peeters
 *
 */
public class TallTall extends ConfluenceModular {
	// this printing instruction prints all choices that are made for different reduction strategies
	static boolean print1 = false;
	// this printing instruction prints all in-between steps of the tall-tall solution
	static boolean print4 = false;
	
	/**
	 * This method computes the convergence of a tall-tall tile of the diverging sequences
	 * @param stepsLeft this is the diverging reductionrow on the left side
	 * @param stepsRight this is the diverging reductionrow on the right side
	 * @param combinedtrs this is the combined trs from which the original problem came
	 * @return the method returns an arrayList of reductionrows. The first one is the left converging
	 * reductionrow, the second one is the right converging reductionrow.
	 */
	public static ArrayList<ArrayList<ReductionRow>> tallTall(ArrayList<ReductionRow> stepsLeft, 
			ArrayList<ReductionRow> stepsRight, GeneralTRS combinedtrs) {
		if (print1)
			System.out.println("Both rows consist of tall steps: going for tall-tall solution.");
		Term startTerm = stepsLeft.get(0).rr.get(0).startTerm;
		int rank = CrankRterm.getRank(startTerm, combinedtrs);
		// get alien vector in startTerm
		ArrayList<Term> alienVectorStart = getAlienVectorStart(startTerm,combinedtrs);
		Position[] positionsAliens = getAlPos(startTerm,alienVectorStart);
		Term resultTermLeft = stepsLeft.get(stepsLeft.size()-1).rr.get(stepsLeft.get(stepsLeft.size()-1).rr.size()-1).resultTerm;
		Term resultTermRight = stepsRight.get(stepsRight.size()-1).rr.get(stepsRight.get(stepsRight.size()-1).rr.size()-1).resultTerm;
		// divide reductionsteps into alien reduction-arrays
		ArrayList<ArrayList<Reductionstep>> alienReductionsDivergence = getAlienReductions(stepsLeft,stepsRight,positionsAliens);
		// balance recursively, until all equalities among aliens have been restored
		ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence = balance(alienVectorStart,positionsAliens,combinedtrs,rank-1,alienReductionsDivergence);
		// sort steps and put aliens back into context
		ArrayList<ArrayList<Reductionstep>> result = aliensInContext(alienReductionsConvergence,resultTermLeft,resultTermRight,positionsAliens,rank,combinedtrs);
		// the reductionsteps need to be put in arraylists of reductionrows, 
		// because of the argument types of confluenceModular
		return createArrays(ReductionRow.createReductionRowArray(result.get(0),rank,combinedtrs),
				ReductionRow.createReductionRowArray(result.get(1),rank,combinedtrs));
	}
	
	/**
	 * getAlienVector is used to retrieve an arrayList of all aliens in the startTerm
	 * @param term this is the startterm that contains the aliens
	 * @param trs this is the trs the term came from
	 * @return the method returns all aliens in the reduced term
	 */
	public static ArrayList<Term> getAlienVectorStart (Term term, GeneralTRS trs) {
		// get the rank of the term
		int rank = CrankRterm.getRank(term,trs);
		// get all subterms of rank-1 and add them to the arraylist (do this twice!)
		ArrayList<Term> alienVector = new ArrayList<Term>();
		if (term.subterms!=null)
			for (int i=0;i<2;i++)
				for (Term st: term.subterms) {
					int stRank = CrankRterm.getRank(st,trs);
					if (stRank == rank-1)
						alienVector.add(st);
				}
		return alienVector;
	}
	
	/**
	 * getAlPos is a method used to compute the positions of the tall aliens in the startTerm.
	 * @param startTerm this is the startTerm
	 * @param alienVectorStart these are the aliens in the startTerm ordered from left to right
	 * @return the positions of the tall aliens in the startTerm (same order as found in term)
	 */
	public static Position[] getAlPos (Term startTerm, ArrayList<Term> alienVectorStart) {
		// get the position of each alien and add it to the array of positions
		Position[] positionsAliens = new Position[alienVectorStart.size()];
		for (int i=0;i<alienVectorStart.size();i++) {
			ArrayList<Position> positions = Position.findPosition(startTerm, alienVectorStart.get(i));
			for (Position pos: positions)
				if (alienVectorStart.get(i)==startTerm.giveSubterm(pos))
					positionsAliens[i]=new Position(pos.position);
		}
		return positionsAliens;
	}

	/**
	 * this method is used to divide the reductions upon the alien vector into reduction-arrays per alien
	 * for left side and right side of diverging steps
	 * @param stepsLeft these are the steps taken on the left side of the divergence
	 * @param stepsRight these are the steps taken on the right side of the divergence
	 * @param positionsAliens these are the positions of the aliens in the startTerm
	 * @return the method returns twice as many arraylists as there are aliens, 
	 * for each alien it produces an array of reductionsteps taken upon particular alien on the
	 * left and on the right side
	 */
	public static ArrayList<ArrayList<Reductionstep>> getAlienReductions (ArrayList<ReductionRow> stepsLeft, 
			ArrayList<ReductionRow> stepsRight, Position[] positionsAliens) {
		ArrayList<ArrayList<Reductionstep>> alienReductions = new ArrayList<ArrayList<Reductionstep>>();
		// create arrays of reductionsteps upon the aliens for each alien (of left and right term) there is 1 array of steps
		ArrayList<ArrayList<Reductionstep>> alienReductionsLeft = getAlienReductions1(stepsLeft,positionsAliens);
		ArrayList<ArrayList<Reductionstep>> alienReductionsRight = getAlienReductions1(stepsRight,positionsAliens);
		for (ArrayList<Reductionstep> ar: alienReductionsLeft)
			alienReductions.add(ar);
		for (ArrayList<Reductionstep> ar: alienReductionsRight)
			alienReductions.add(ar);
		if (print4) {
			System.out.println("Got alien steps from original steps:");
			System.out.println("Alien steps on the left:");
			int i=0;
			for (ArrayList<Reductionstep> aliensteps: alienReductionsLeft) {
				System.out.println("Alien " + (i+1) + ":");
				for (Reductionstep rs: aliensteps)
					rs.printReduction();
				i++;
			}
			System.out.println("Alien steps on the right:");
			i=0;
			for (ArrayList<Reductionstep> aliensteps: alienReductionsRight) {
				System.out.println("Alien " + (i+1) + ":");
				for (Reductionstep rs: aliensteps)
					rs.printReduction();
				i++;
			}
		}
		return alienReductions;
	}
	
	/**
	 * this method is used to divide the reductions upon the alien vector into reduction-arrays per alien for only
	 * one side of the divergence
	 * @param steps these are the steps taken upon the startTerm
	 * @param positionsAliens these are the positions of the aliens in the startTerm
	 * @return the method returns as many arraylists as there are aliens, 
	 * for each alien it produces an array of reductionsteps taken upon particular alien
	 */
	public static ArrayList<ArrayList<Reductionstep>> getAlienReductions1 (ArrayList<ReductionRow> steps, Position[] positionsAliens) {
		ArrayList<ArrayList<Reductionstep>> alienReductions = new ArrayList<ArrayList<Reductionstep>>();
		// for each alienPosition
		for (int i=0;i<positionsAliens.length/2;i++) {
			ArrayList<Reductionstep> alienReduction = new ArrayList<Reductionstep>();
			// check all steps, whether this alien was reduced, by looking at positionApplied
			for (ReductionRow rr: steps)
				for (Reductionstep rs: rr.rr) {
					if (positionsAliens[i].isPrefix(rs.positionApplied)) {
						// if so, transform the step into an alien step and add it to the list
						Term startTerm = rs.startTerm.giveSubterm(positionsAliens[i]);
						Term resultTerm = rs.resultTerm.giveSubterm(positionsAliens[i]);
						Position position = rs.positionApplied.subtractPrefix(positionsAliens[i]);
						alienReduction.add(new Reductionstep(new Term(startTerm), new Term(resultTerm),rs.appliedRule,position));
					}
				}
			alienReductions.add(alienReduction);
		}
		return alienReductions;
	}
	
	/**
	 * balance is a method that restores all equalities among aliens
	 * @param alienVectorStart this is the vector of aliens in the startTerm
	 * @param positionsAliens these are the positions of the aliens in the startTerm
	 * @param trs this is the trs the terms and rules come from
	 * @param rank this is the rank of the startTerm
	 * @param alienReductionsDivergence These are the diverging steps for every alien in the startTerm.
	 * @return the method returns the converging steps
	 */
	private static ArrayList<ArrayList<Reductionstep>> balance (ArrayList<Term> alienVectorStart,
			Position[] positionsAliens, GeneralTRS trs, int rank, 
			ArrayList<ArrayList<Reductionstep>> alienReductionsDivergence) {
		// create resulting arrays (empty still)
		ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence = new ArrayList<ArrayList<Reductionstep>>();
		for (int i=0;i<alienReductionsDivergence.size();i++) 
			alienReductionsConvergence.add(new ArrayList<Reductionstep>());
		// while there are still some unresolved balancing issues, start balancing
		alienReductionsConvergence = recursiveBalancing
			(positionsAliens,alienVectorStart,alienReductionsDivergence,trs,rank,alienReductionsConvergence);
		if (print4) {
			System.out.println("Calculated balancing alien steps:");
			for (int i=0;i<alienReductionsConvergence.size();i++) {
				if (i==0)
					System.out.println("Alien steps on the left:");
				if (i==alienReductionsConvergence.size()/2)
					System.out.println("Alien steps on the right:");
				int j=i;
				if (j>=alienReductionsConvergence.size()/2)
					j=j-alienReductionsConvergence.size()/2;
				System.out.println("Alien " + (j+1) + ":");
				for (Reductionstep rs: alienReductionsConvergence.get(i)) {
					rs.printReduction();
				}
			}
		}
		// save results into result arrays
		return alienReductionsConvergence;
	}
	
	/**
	 * this is the recursive balancing of alien terms
	 * @param alienPositions the positions of the aliens in the startTerm
	 * @param alienVectorStart the aliens in the startTerm
	 * @param alienReductionsDivergence These are the diverging steps for every alien in the startTerm.
	 * @param trs the trs the terms and rules come from
	 * @param rank this is the rank of the startTerm
	 * @param alienReductionsConvergence These are the converging steps for every alien in the startTerm.
	 * @return the converging steps found through balancing in this run
	 */
	public static ArrayList<ArrayList<Reductionstep>> recursiveBalancing (Position[] alienPositions, 
			ArrayList<Term> alienVectorStart, ArrayList<ArrayList<Reductionstep>> alienReductionsDivergence, 
			GeneralTRS trs, int rank, ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence) {
		ArrayList<ArrayList<ReductionRow>>  resultAliens = null;
		boolean go = true;
		// start looking for a reduced alien
		while (go) {
			go = false;
			for (int i=0;i<alienReductionsDivergence.size();i++)
				for (int j=0; j<alienReductionsDivergence.size();j++)
					// start looking for an equality
					if ( alienVectorStart.get(i).equals(alienVectorStart.get(j)) && i!=j) {
						// get reductions from alien in startTerm up until last reduction of alien
						ArrayList<ReductionRow> leftRow = getReductionRow(alienReductionsConvergence,rank,trs,alienReductionsDivergence,alienVectorStart,i);
						ArrayList<ReductionRow> rightRow = getReductionRow(alienReductionsConvergence,rank,trs,alienReductionsDivergence,alienVectorStart,j);
						// find convergence for two reductionrows, but only if their last resultTerms are not equal
						if (!leftRow.get(leftRow.size()-1).rr.get(leftRow.get(leftRow.size()-1).rr.size()-1).resultTerm.equals
								(rightRow.get(rightRow.size()-1).rr.get(rightRow.get(rightRow.size()-1).rr.size()-1).resultTerm)) {
							resultAliens = ConfluenceModular.confluenceModular (leftRow,rightRow,rank-1,trs);
							go = true;
							// look for equal aliens and copy steps into their converging steps array
							alienReductionsConvergence = getEqualities(alienReductionsDivergence,alienReductionsConvergence,alienVectorStart,i,j,resultAliens);
							// put the converging steps into the arrays of converging steps for the two aliens themselves
							for (ReductionRow rr: resultAliens.get(0))
								for (Reductionstep rs: rr.rr)
									alienReductionsConvergence.get(i).add(new Reductionstep(rs));
							for (ReductionRow rr: resultAliens.get(1))
								for (Reductionstep rs: rr.rr)
									alienReductionsConvergence.get(j).add(new Reductionstep(rs));
						}
					}
		}
		return alienReductionsConvergence;
	}
	
	/**
	 * this method will find all reductions (diverging and converging) upon an alien, to feed into a recursion
	 * @param alienReductionsConvergence these are all converging steps taken upon the aliens
	 * @param rank this is the rank of the aliens (rank-1 considering the original startTerm)
	 * @param trs the modular trs
	 * @param alienReductionsDivergence all diverging reductions taken upon the aliens
	 * @param alienVectorStart this is the vector of the aliens in the startTerm
	 * @param i this is the index of the alien that is used in this comparison
	 * @return the method returns a reductionrow containing all steps taken upon the alien up until now
	 */
	private static ArrayList<ReductionRow> getReductionRow (ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence, int rank,
			GeneralTRS trs, ArrayList<ArrayList<Reductionstep>> alienReductionsDivergence, ArrayList<Term> alienVectorStart, int i) {
		ArrayList<ReductionRow> row = new ArrayList<ReductionRow>();
		ArrayList<Reductionstep> redsDivPlusCon = new ArrayList<Reductionstep>();
		// if the alien has been reduced during divergence, get those diverging steps
		if (!alienReductionsDivergence.get(i).isEmpty())
			for (Reductionstep rs: alienReductionsDivergence.get(i))
				redsDivPlusCon.add(new Reductionstep(rs));
		// if there have been made converging steps, add them to diverging steps
		if (!alienReductionsConvergence.get(i).isEmpty())
			for (Reductionstep rs: alienReductionsConvergence.get(i))
				redsDivPlusCon.add(new Reductionstep(rs));
		if (!redsDivPlusCon.isEmpty())
			row = ReductionRow.createReductionRowArray(redsDivPlusCon,rank,trs);
		// otherwise just create the empty row on alien from startterm 
		else
			row = createEmptyArrayRow(alienVectorStart.get(i),trs);
		return row;
	}
	
	/**
	 * getEqualities is used to find equalaliens in the vector and copy the found steps to the reduction-rows of the equal aliens
	 * @param alienReductionsDivergence these are the arrays of diverging steps taken upon the aliens
	 * @param alienReductionsConvergence these are the arrays of diverging steps taken upon the aliens
	 * @param alienVectorStart this is the vector of aliens in the startTerm
	 * @param i this is the index of the first reduced alien
	 * @param j this is the index of the second reduced alien
	 * @param resultAliens these are the resulting reductionRows upon the aliens coming from the recursion upon alien i and j.
	 * @return the method returns all converging reduction arrays upon the aliens thus far
	 */
	private static ArrayList<ArrayList<Reductionstep>> getEqualities(ArrayList<ArrayList<Reductionstep>> alienReductionsDivergence,
			ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence, ArrayList<Term> alienVectorStart, int i, int j, 
			ArrayList<ArrayList<ReductionRow>>  resultAliens) {
		// if there are aliens equal to one of the aliens, put the found reductions in the array of those equal aliens
		for (int k=0;k<2;k++) {
			for (int l=0;l<alienReductionsConvergence.size();l++) {
				int p=0;
				if (k==0)
					p=i;
				else
					p=j;
				// get latest version of particular alien
				Term term1 = getTerm(alienReductionsConvergence,alienReductionsDivergence,alienVectorStart,l);
				Term term2 = getTerm(alienReductionsConvergence,alienReductionsDivergence,alienVectorStart,p);
				// check whether latest versions are equal, if so: copy reductions
				if (term1.equals(term2) && p!=l && i!=j)
					for (ReductionRow rr: resultAliens.get(k))
						for (Reductionstep rs: rr.rr)
							alienReductionsConvergence.get(l).add(new Reductionstep(rs));
			}
		}
		return alienReductionsConvergence;
	}
	
	/**
	 * getTerm is used to get the last result of reductions upon this particular alien
	 * @param alienReductionsConvergence these are the arrays of converging reductions upon all aliens
	 * @param alienReductionsDivergence these are the arrays of diverging reductions upon al aliens
	 * @param alienVectorStart this is the vector of aliens in the startTerm
	 * @param p this is the index of the alien
	 * @return this method returns the final version of the particular alien
	 */
	private static Term getTerm (ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence, 
			ArrayList<ArrayList<Reductionstep>> alienReductionsDivergence, ArrayList<Term> alienVectorStart, int p) {
		Term term = null;
		// if there are converging steps for this alien, look in that array for latest version
		if (!alienReductionsConvergence.get(p).isEmpty())
			term = new Term(alienReductionsConvergence.get(p).get(alienReductionsConvergence.get(p).size()-1).resultTerm);
		// otherwise if there were diverging steps, look into that array
		else if (!alienReductionsDivergence.get(p).isEmpty())
			term = new Term(alienReductionsDivergence.get(p).get(alienReductionsDivergence.get(p).size()-1).resultTerm);
		// otherwise, just get the startTerm
		else 
			term = new Term(alienVectorStart.get(p));
		return term;
	}
	
	/**
	 * this method puts all the alien-reductions back into the context of the entire term
	 * @param alienReductionsConvergence these are the reductions of all aliens
	 * @param resultTermLeft this was the original resultTerm on the left side of the divergence
	 * @param resultTermRight this was the original resultTerm on the right side of the divergence
	 * @param positionsAliens these are the positions of all the aliens in the startTerm
	 * @param rank this is the rank of the original startTerm
	 * @param combinedtrs this is the trs in which the reductions took place
	 * @return the method returns all reductions in their (reduced) context
	 */
	private static ArrayList<ArrayList<Reductionstep>> aliensInContext(ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence,
			Term resultTermLeft, Term resultTermRight, Position[] positionsAliens, int rank, GeneralTRS combinedtrs) {
		ArrayList<ArrayList<Reductionstep>> result = new ArrayList<ArrayList<Reductionstep>>();
		result.add(new ArrayList<Reductionstep>());
		result.add(new ArrayList<Reductionstep>());
		// put aliens into context of resultTerms (first for all tall steps, then for all short steps)
		result = aliensInContext1(result,alienReductionsConvergence,resultTermLeft,resultTermRight,positionsAliens,rank,combinedtrs,true);
		if (!result.get(0).isEmpty())
			resultTermLeft = new Term(result.get(0).get(result.get(0).size()-1).resultTerm);
		if (!result.get(1).isEmpty())
			resultTermRight = new Term(result.get(1).get(result.get(1).size()-1).resultTerm);
		result = aliensInContext1(result,alienReductionsConvergence,resultTermLeft,resultTermRight,positionsAliens,rank,combinedtrs,false);
		return result;
	}
	
	/**
	 * aliensInContext1 is a method that puts back aliens in context. It can be called to put back all
	 * tall steps upon aliens back into context, or all short steps (use boolean tall).
	 * @param result these are the result arrays thus far
	 * @param alienReductionsConvergence these are the converging steps upon the aliens
	 * @param resultTermLeft this is the resultterm on the left
	 * @param resultTermRight this is the resultterm on the right
	 * @param positionsAliens these are the positions of the aliens in the startterm
	 * @param rank this is the rank of the original startterm
	 * @param combinedtrs this is the modular trs
	 * @param tall this is a boolean that tells whether the tall steps are to be added, or the short steps
	 * @return the method returns the list of converging steps embedded in their context
	 */
	private static ArrayList<ArrayList<Reductionstep>> aliensInContext1 (ArrayList<ArrayList<Reductionstep>> result, 
			ArrayList<ArrayList<Reductionstep>> alienReductionsConvergence,
			Term resultTermLeft, Term resultTermRight, Position[] positionsAliens, int rank, GeneralTRS combinedtrs,
			boolean tall) {
		int i=0;
		for (ArrayList<Reductionstep> ar: alienReductionsConvergence) {
			for (Reductionstep rs: ar) {
				if ((tall && CrankRterm.getRank(rs.startTerm,combinedtrs)==rank-1 )||
						(!tall && CrankRterm.getRank(rs.startTerm,combinedtrs)<rank-1)) {
					// if alien is from resultTermLeft
					if (i<positionsAliens.length/2) {
						Reductionstep newrs = aliensInContext2(rs,positionsAliens[i],resultTermLeft);
						result.get(0).add(newrs);
						resultTermLeft = new Term(newrs.resultTerm);
					// if alien is from resultTermRight
					} else if (i>=positionsAliens.length/2){
						Reductionstep newrs = aliensInContext2(rs,positionsAliens[i],resultTermRight);
						result.get(1).add(newrs);
						resultTermRight = new Term(newrs.resultTerm);
					}
				}
			}
			i++;
		}
		return result;
	}

	/**
	 * alienInContext puts back a reduction of an alien into its context
	 * @param rs this is the reductionstep upon the alien
	 * @param p this is the position of the alien
	 * @param t this is the context-term
	 * @return this is the reductionstep when the context has been put back around the alien
	 */
	public static Reductionstep aliensInContext2 (Reductionstep rs, Position p, Term t) {
		Position newpos = new Position(rs.positionApplied.position);
		for (int l: p.position)
			newpos = newpos.nestPositions(l);
		newpos.position.remove(p.position.size());
		Term startTerm = new Term(Term.replaceSubterm(t, p, rs.startTerm));
		Term resultTerm = new Term(Term.replaceSubterm(t, p, rs.resultTerm));
		Reductionstep newrs = new Reductionstep(new Term(startTerm),new Term(resultTerm),rs.appliedRule,newpos);
		return newrs;
	}
}
