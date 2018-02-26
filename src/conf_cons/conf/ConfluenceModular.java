package conf_cons.conf;

import java.util.ArrayList;

import conf_cons.gui.*;
import conf_cons.basictrs.*;

/**
 * 
 * ConfluenceModular is used to find the common reduct of two diverging steps for 
 * a combined TRS. It implements the algorithm presented in a paper by V. van Oostrom:
 * Modularity of Confluence, constructed (2008).
 * 
 * @author Marieke Peeters
 *
 */
public class ConfluenceModular {
	
	// this printing instruction prints all choices that are made for different reduction strategies
	static boolean print1 = false;
	// this printing instruction prints all reductionrows that are calculated in a tile
	static boolean print1a = false;
	// this printing instruction prints all reductionrows that are calculated
	static boolean print2 = false;
	
	/**
	 * confluenceModularStart initiates confluenceModular and returns an array of strings
	 * describing the confluence proof for an modular confluence problem.
	 * @param leftRow This is the reductionrow of the diverging steps to the left.
	 * @param rightRow This the reductionrow of the diverging steps to the right.
	 * @param ctrs This is the combined trs from which the reduction comes.
	 * @return The method returns a description of the confluence proof.
	 */
	public static ArrayList<String> confluenceModularStart (ArrayList<ReductionRow> leftRow, 
											ArrayList<ReductionRow> rightRow, GeneralTRS ctrs) {
		int rank = CrankRterm.getRank(leftRow.get(0).rr.get(0).startTerm, ctrs);
		// compute convergence
		ArrayList<ArrayList<ReductionRow>> result = confluenceModular(leftRow,rightRow,rank,ctrs);
		// create string output from computed convergence
		ArrayList<String> finalOutput = new ArrayList<String>();
		finalOutput.add("Created convergent reductions for diverging steps in modular confluence problem:");
		finalOutput.add("Reduction array for term " + leftRow.get(leftRow.size()-1).rr.get(leftRow.get(leftRow.size()-1).rr.size()-1).
				resultTerm.termToString() + ":");
		ArrayList<Reductionstep> newResultLeft = new ArrayList<Reductionstep>();
		if (!result.get(0).isEmpty())
			for (ReductionRow rr: result.get(0))
				if (rr.stepType != ReductionRow.StepType.Emptystep)
					for (Reductionstep rs: rr.rr) {
						newResultLeft.add(rs);
						finalOutput.add(rs.reductionToString());
					}
		if (newResultLeft.isEmpty())
			finalOutput.add("It was empty.");
		finalOutput.add("Reduction array for term " + rightRow.get(rightRow.size()-1).rr.get(rightRow.get(rightRow.size()-1).rr.size()-1).
				resultTerm.termToString() + ":");
		ArrayList<Reductionstep> newResultRight = new ArrayList<Reductionstep>();
		if (!result.get(1).isEmpty())
			for (ReductionRow rr: result.get(1))
				if (rr.stepType != ReductionRow.StepType.Emptystep)
					for (Reductionstep rs: rr.rr) {
						newResultRight.add(rs);
						finalOutput.add(rs.reductionToString());
					}
		if (newResultRight.isEmpty())
			finalOutput.add("It was empty.");
		if (!result.get(0).isEmpty()&&!result.get(0).get(0).rr.isEmpty()) {
			finalOutput.add("Found a common reduct for the two terms: " + 
					result.get(0).get(result.get(0).size()-1).rr.get(result.get(0).get(result.get(0).size()-1).
							rr.size()-1).resultTerm.termToString());
		} else {
			finalOutput.add("Found a common reduct for the two terms: " + 
					result.get(1).get(result.get(1).size()-1).rr.get(result.get(1).get(result.get(1).size()-1).
							rr.size()-1).resultTerm.termToString());
		}
		
		// return string output
		return finalOutput;
	}
	
	/**
	 * This method computes the converging steps for a given divergence
	 * within a combined trs.
	 * @param leftRow This is the arraylist of reductionrows of the diverging steps to the left.
	 * @param rightRow This is the arraylist of reductionrows  of the diverging steps to the right.
	 * @param rank this is the rank of the startterm in the divergence
	 * @param ctrs this is the combined trs in which the reductions take place
	 * @return the method returns an arrayList of reductionrows. The first one is the left converging
	 * reductionrow, the second one is the right converging reductionrow.
	 */
	public static ArrayList<ArrayList<ReductionRow>> confluenceModular (ArrayList<ReductionRow> leftRow, 
														ArrayList<ReductionRow> rightRow, int rank, GeneralTRS ctrs) {
		// reductiongraph: first two indices are to point at the tile, third index is either 0: leftrow, or 1: rightrow
		// last index is always only 0, because each row consists of only 1 reductionrow
		ArrayList<ArrayList<ArrayList<ArrayList<ReductionRow>>>> reductiongraph = 
			new ArrayList<ArrayList<ArrayList<ArrayList<ReductionRow>>>>();
		// initialize all arrayLists.....
		for (int i=0;i<leftRow.size()+1;i++) {
			reductiongraph.add(new ArrayList<ArrayList<ArrayList<ReductionRow>>>());
			for (int j=0;j<rightRow.size()+1;j++) {
				reductiongraph.get(i).add(new ArrayList<ArrayList<ReductionRow>>());
				reductiongraph.get(i).get(j).add(new ArrayList<ReductionRow>());
				reductiongraph.get(i).get(j).add(new ArrayList<ReductionRow>());
				if (i==0 && j<rightRow.size())
					reductiongraph.get(0).get(j).get(1).add(rightRow.get(j));
			}
			if (i<leftRow.size())
				reductiongraph.get(i).get(0).get(0).add(leftRow.get(i));
		}
		return findConvergence(reductiongraph,leftRow.size(),rightRow.size(),rank,ctrs);
	}
	
	/**
	 * findConvergence will find a convergence for two diverging reduction-rows and returns the converging reductionrows
	 * @param reductiongraph this is the reductiongraph consisting of the reductionrows
	 * @param left this is the amount of reductionrows on the left (in the graph)
	 * @param right this is the amount of reductionrows on the right (in the graph)
	 * @param rank this is the rank of the startTerm in the divergence
	 * @param ctrs this is the combined trs
	 * @return this is the resulting array of converging reductions left and right
	 */
	private static ArrayList<ArrayList<ReductionRow>> findConvergence (
				ArrayList<ArrayList<ArrayList<ArrayList<ReductionRow>>>> reductiongraph,
				int left,int right,int rank,GeneralTRS ctrs) {
		// pointers to indicate tiles of the reductiongraph
		int pointerLeft=0;
		int pointerRight=0;
		// find convergence for all tiles
		while (pointerLeft < left) {
			// get steps and resultTerms of current 'tile'
			ArrayList<ReductionRow> stepsLeft = reductiongraph.get(pointerLeft).get(pointerRight).get(0);
			ArrayList<ReductionRow> stepsRight = reductiongraph.get(pointerLeft).get(pointerRight).get(1);
			// create resulting reduction-rows
			ArrayList<ArrayList<ReductionRow>> result = new ArrayList<ArrayList<ReductionRow>>();
			int newrank = CrankRterm.getRank(stepsLeft.get(0).rr.get(0).startTerm,ctrs);
			// create empty-step features
			Rule empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
			ArrayList<Integer> nul = new ArrayList<Integer>();
			nul.add(0);
			Position emptypos = new Position(nul);
			ArrayList<Reductionstep> newarl = new ArrayList<Reductionstep>();
			for (ReductionRow rr: stepsLeft)
				if (rr.stepType!=ReductionRow.StepType.Emptystep)
					for (Reductionstep rs: rr.rr)
						newarl.add(new Reductionstep(rs));
			if (newarl.isEmpty())
				newarl.add(new Reductionstep(stepsLeft.get(0).rr.get(0).startTerm,stepsLeft.get(0).rr.get(0).startTerm,empty,emptypos));
			ArrayList<Reductionstep> newarr = new ArrayList<Reductionstep>();
			for (ReductionRow rr: stepsRight)
				if (rr.stepType!=ReductionRow.StepType.Emptystep)
					for (Reductionstep rs: rr.rr)
						newarr.add(new Reductionstep(rs));
			if (newarr.isEmpty())
				newarr.add(new Reductionstep(stepsRight.get(0).rr.get(0).startTerm,stepsRight.get(0).rr.get(0).startTerm,empty,emptypos));
			ArrayList<ReductionRow> newLeft = ReductionRow.createReductionRowArray(newarl,newrank,ctrs);
			ArrayList<ReductionRow> newRight = ReductionRow.createReductionRowArray(newarr,newrank,ctrs);
			if (print1a) {
				System.out.print("The rank of the startTerm ");
				stepsLeft.get(0).rr.get(0).startTerm.printTerm();
				System.out.println(" is " + newrank + ".");
				System.out.println("Array of steps on the left of this tile:");
				for (ReductionRow rr: newLeft) {
					if (rr.stepType==ReductionRow.StepType.Shortstep)
						System.out.println("Array of short steps:");
					else if (rr.stepType==ReductionRow.StepType.Tallstep)
						System.out.println("Array of tall steps:");
					else if (rr.stepType==ReductionRow.StepType.Emptystep)
						System.out.println("The empty step:");
					for (Reductionstep rs: rr.rr)
						rs.printReduction();
				}
				System.out.println("Array of steps on the right of this tile:");
				for (ReductionRow rr: newRight) {
					if (rr.stepType==ReductionRow.StepType.Shortstep)
						System.out.println("Array of short steps:");
					else if (rr.stepType==ReductionRow.StepType.Tallstep)
						System.out.println("Array of tall steps:");
					else if (rr.stepType==ReductionRow.StepType.Emptystep)
						System.out.println("The empty step:");
					for (Reductionstep rs: rr.rr)
						rs.printReduction();
				}
			}
			// check the rank of the terms: send to according methods for convergence
			if (CrankRterm.getRank(stepsLeft.get(0).rr.get(0).startTerm,ctrs) == 1)
				result = confluenceOneTRS(newLeft,newRight,ctrs);
			else {
				result = confluenceTwoTRS(newLeft,newRight,ctrs);
			}
			// put results into reductiongraph
			for (ReductionRow rr: result.get(0))
				if(rr.rr!=null)
					reductiongraph.get(pointerLeft+1).get(pointerRight).get(1).add(new ReductionRow (rr.rr,
							rank,ctrs));
			for (ReductionRow rr: result.get(1))
				if (rr.rr!=null)
					reductiongraph.get(pointerLeft).get(pointerRight+1).get(0).add(new ReductionRow (rr.rr,
							rank,ctrs));
			if (result.get(0).isEmpty() || result.get(0).get(result.get(0).size()-1).rr==null) {
				Term oldResultLeft = stepsLeft.get(stepsLeft.size()-1).rr.get(stepsLeft.get(stepsLeft.size()-1).rr.size()-1).resultTerm;
				reductiongraph.get(pointerLeft+1).get(pointerRight).get(1).add(createEmptyArrayRow(oldResultLeft,ctrs).get(0));
			}
			if (result.get(1).isEmpty() || result.get(1).get(result.get(1).size()-1).rr==null) {
				Term oldResultRight = stepsRight.get(stepsRight.size()-1).rr.get(stepsRight.get(stepsRight.size()-1).rr.size()-1).resultTerm;
				reductiongraph.get(pointerLeft).get(pointerRight+1).get(0).add(createEmptyArrayRow(oldResultRight,ctrs).get(0));
			}
			if (print1a) {
				System.out.println("Left array of result for this tile:");
				for (ReductionRow rr: result.get(0))
					if (rr.rr!=null)
						for (Reductionstep rs: rr.rr)
							rs.printReduction();
				System.out.println("Right array of result for this tile:");
				for (ReductionRow rr: result.get(1))
					if (rr.rr!=null)
						for (Reductionstep rs: rr.rr)
							rs.printReduction();
			}
			// move on to next tile
			pointerRight++;
			if (right == pointerRight) {
				pointerRight = 0;
				pointerLeft++;
			}
		}
		pointerLeft = 0;
		// when done with all tiles, return the results as two ArrayLists of ReductionRows
		ArrayList<ReductionRow> resultArrayLeft = new ArrayList<ReductionRow>();
		ArrayList<ReductionRow> resultArrayRight = new ArrayList<ReductionRow>();
		for (int i=0;i<left;i++)
			for (ReductionRow rr: reductiongraph.get(i).get(right).get(0))
				resultArrayRight.add(rr);
		for (int i=0;i<right;i++)
			for (ReductionRow rr: reductiongraph.get(left).get(i).get(1))
				resultArrayLeft.add(rr);
		return createArrays(resultArrayLeft,resultArrayRight);
	}
	
	/**
	 * confluenceOneTRS computes the converging reductionrows for a startterm that is of rank 1.
	 * it sends the problem to the corresponding confluence method: confluenceOrthogonal or
	 * confluenceWCRSN, depending on the type of trs the term comes from.
	 * @param stepsLeft this is the diverging reductionrow on the left side
	 * @param stepsRight this is the diverging reductionrow on the right side
	 * @param ctrs this is the combined trs from which the original problem came
	 * @return the method returns an arrayList of reductionrows. The first one is the left converging
	 * reductionrow, the second one is the right converging reductionrow.
	 */
	private static ArrayList<ArrayList<ReductionRow>> confluenceOneTRS (ArrayList<ReductionRow> stepsLeft, 
														ArrayList<ReductionRow> stepsRight, GeneralTRS ctrs) {
		if (print1)
			System.out.println("Term is of rank 1: going for single trs solutions.");
		// find out what trs this layer is from
		Term startTerm = stepsLeft.get(0).rr.get(0).startTerm;
		GeneralTRS trs = CrankRterm.getTRS(startTerm, ctrs);
		// find out what assumptions are true in that trs
		boolean okWCRSN = false;
		boolean okOrtho = false;
		for (String[] s: trs.assumptions)
			if (s[1].equals("WCR&SN"))
				okWCRSN = true;
			else if (s[1].equals("Orthogonal"))
				okOrtho = true;
		// send the divergence to the according method
		ArrayList<ReductionRow> result = new ArrayList<ReductionRow>();
		ArrayList<Reductionstep> left = new ArrayList<Reductionstep>();
		ArrayList<Reductionstep> right = new ArrayList<Reductionstep>();
		for (ReductionRow rr: stepsLeft)
			for (Reductionstep rs: rr.rr)
				left.add(rs);
		for (ReductionRow rr: stepsRight)
			for (Reductionstep rs: rr.rr)
				right.add(rs);
		if (okWCRSN) {
			result = ConfluenceWCRSN.confluenceWCRSN(left.get(left.size()-1).resultTerm,right.get(right.size()-1).resultTerm,trs);
			if (print1)
				System.out.println("Found a solution through the ConfluenceWCRSN method.");
		} else if (okOrtho) {
			result = ConfluenceOrthogonal.confluenceOrthogonal(left,right,trs);
			if (print1)
				System.out.println("Found a solution through the ConfluenceOrthogonal method.");
		}
		ArrayList<ReductionRow> leftArray = new ArrayList<ReductionRow>();
		leftArray.add(result.get(0));
		ArrayList<ReductionRow> rightArray = new ArrayList<ReductionRow>();
		rightArray.add(result.get(1));
		// return the resulting convergence
		return createArrays(leftArray,rightArray);
	}
	
	/**
	 * confluenceOTwoTRS computes the converging reductionrows for a startterm that is of rank 2 or higher.
	 * it sends the problem to the corresponding confluence method: short-short, tall-tall, or
	 * mixed, depending on the type of steps in the diverging reductionrows.
	 * @param stepsLeft this is the diverging reductionrow on the left side
	 * @param stepsRight this is the diverging reductionrow on the right side
	 * @param ctrs this is the combined trs from which the original problem came
	 * @return the method returns an arrayList of reductionrows. The first one is the left converging
	 * reductionrow, the second one is the right converging reductionrow.
	 */
	private static ArrayList<ArrayList<ReductionRow>> confluenceTwoTRS (ArrayList<ReductionRow> stepsLeft, 
														ArrayList<ReductionRow> stepsRight, GeneralTRS ctrs) {
		if (print1)
			System.out.println("Term is of rank higher than 1: going for multiple trs solutions.");
		ArrayList<ArrayList<ReductionRow>> initialResult = new ArrayList<ArrayList<ReductionRow>>();
		// if there are still more then 1 reductionrows on the left or right side of the reduction, 
		// tiling is necessary: recursion.
		if (stepsLeft.size()>1 || stepsRight.size()>1) {
			if (print1) 
				System.out.println("There are multiple arrays of steps (tall and short arrays), so start recursion.");
			return confluenceModular(stepsLeft,stepsRight,CrankRterm.getRank(stepsLeft.get(0).rr.get(0).startTerm,ctrs),ctrs);
		}
		// check whether the tile is a 'short-short'-, 'tall-tall'- or a 'mixed'-tile
		if ((stepsLeft.get(0).stepType==ReductionRow.StepType.Shortstep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Emptystep) ||
				(stepsLeft.get(0).stepType==ReductionRow.StepType.Emptystep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Shortstep) ||
				(stepsLeft.get(0).stepType==ReductionRow.StepType.Shortstep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Shortstep) ||
				(stepsLeft.get(0).stepType==ReductionRow.StepType.Emptystep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Emptystep))
			initialResult = ShortShort.shortShort(stepsLeft,stepsRight,ctrs);
		else if ((stepsLeft.get(0).stepType==ReductionRow.StepType.Tallstep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Emptystep) ||
				(stepsLeft.get(0).stepType==ReductionRow.StepType.Emptystep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Tallstep) ||
				(stepsLeft.get(0).stepType==ReductionRow.StepType.Tallstep &&
					stepsRight.get(0).stepType==ReductionRow.StepType.Tallstep))
			initialResult = TallTall.tallTall(stepsLeft,stepsRight,ctrs);
		else
			initialResult = Mix.mix(stepsLeft,stepsRight,ctrs);
		// return the resulting converging arrays
		return initialResult;
	}
	
	/**
	 * createEmptyArrayRow creates an arrayList containing a reductionrow of one empty step
	 * @param startTerm this is the term that was at the beginning of the sequence of reductionsteps, 
	 * it is used to get the rank
	 * @param ctrs this is the combined trs in which the reductionsteps took place
	 * @return the method returns the empty array-row
	 */
	public static ArrayList<ReductionRow> createEmptyArrayRow (Term startTerm, GeneralTRS ctrs) {
		ArrayList<ReductionRow> emptyArrayRow = new ArrayList<ReductionRow>();
		// put it into an arraylist
		ArrayList<Reductionstep> emptyArray = new ArrayList<Reductionstep>();
		// create an empty arraylist of integers to create the empty position
		ArrayList<Integer> nul = new ArrayList<Integer>();
		nul.add(0);
		Position emptyPosition = new Position(nul);
		// create an empty rule: it says you can change this term into itself
		Variable emptyvar = new Variable("x");
		Term var = new Term(emptyvar);
		Rule emptyRule = new Rule (var,var);
		// create reductionstep
		Reductionstep empty = new Reductionstep (new Term(startTerm),new Term(startTerm),emptyRule,emptyPosition);
		emptyArray.add(empty);
		// create reductionrow from the arraylist
		ReductionRow emptyRow = new ReductionRow(emptyArray,CrankRterm.getRank(startTerm,ctrs),ctrs);
		emptyArrayRow.add(emptyRow);
		// return the empty reduction-row
		return emptyArrayRow;
	}
	
	/**
	 * This method is used to create an arraylist of arraylists of reductionrows.
	 * @param rrl this is the first list of reductionrows
	 * @param rrr this is the second list of reductionrows
	 * @return the method returns the arraylist of arraylists of reductionrows
	 */
	public static ArrayList<ArrayList<ReductionRow>> createArrays (ArrayList<ReductionRow> rrl, 
			ArrayList<ReductionRow> rrr) {
		ArrayList<ArrayList<ReductionRow>> result = new ArrayList<ArrayList<ReductionRow>>();
		result.add(rrl);
		result.add(rrr);
		return result;
	}
}