package conf_cons.conf;

import java.util.ArrayList;

import conf_cons.gui.*;
import conf_cons.basictrs.*;

/**
 * ConfluenceOrthogonal is used to find the common reduct of two diverging steps for 
 * a TRS that is orthogonal. It uses the method of underlined TRS's and complete
 * developments.
 * 
 * @author Marieke Peeters
 * 
 */
public class ConfluenceOrthogonal {
	
	public static ArrayList<String> completeOutput;
	private static GeneralTRS underlinedTrs;
	private static Rule empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
	
	/**
	 * The method confluenceOrthogonal computes a common reduct for two terms, using Newman's lemma.
	 * It computes the two reductionrows to their normal form and checks if the 
	 * two normal forms are the same.
	 * @param stepsLeft These are the reductionsteps taken on the left side of the divergence.
	 * @param stepsRight These are the reductionsteps taken on the right side of the divergence.
	 * @param trs This is the trs that is used to produce the reductionrow.
	 * @return the method returns an arraylist of arraylists of reductionsteps, the proof can be found in
	 * a static array of strings in the class: completeOutput.
	 */
	public static ArrayList<ReductionRow> confluenceOrthogonal (ArrayList<Reductionstep> stepsLeft, 
			ArrayList<Reductionstep> stepsRight, GeneralTRS trs) {
		// Create new underlined trs from given trs
		underlinedTrs = UnderlinedTRS.underlineTRS(trs);
		// underline startTerm and remember startTerm
		Term startul = UnderlinedTRS.underlineTerm(trs,new Term(stepsLeft.get(0).startTerm));
		ArrayList<ReductionRow> result = new ArrayList<ReductionRow>();
		// Get resulting arrays of reductionsteps from recursion on steps
		result = confluenceOrthogonal1(stepsLeft,stepsRight,trs,startul);
		return getOutput(stepsLeft,stepsRight,result,trs);
	}
	
	/**
	 * confluenceOrthogonal1 initializes the reductiongraph for the recursion
	 * @param stepsLeft  these are the steps taken on the left side of the divergence
	 * @param stepsRight these are the steps taken on the right side of the divergence
	 * @param trs this is the trs in which the steps take place
	 * @param startul this is the underlined term to start the recursion with
	 * @return the method returns the resulting arrays of reductionsteps
	 */
	private static ArrayList<ReductionRow> confluenceOrthogonal1 (ArrayList<Reductionstep> stepsLeft, 
			ArrayList<Reductionstep> stepsRight, GeneralTRS trs, Term startul) {
		Term startulLeft = new Term (startul);
		Term startulRight = new Term (startul);
		// create reductiongraph to be able to get orthogonal confluence recursively
		ArrayList<ArrayList<ArrayList<ArrayList<Reductionstep>>>> graph = new ArrayList<ArrayList<ArrayList<ArrayList<Reductionstep>>>>();
		for (int i=0;i<stepsLeft.size()+1;i++) {
			graph.add(new ArrayList<ArrayList<ArrayList<Reductionstep>>>());
			for (int j=0;j<stepsRight.size()+1;j++) {
				graph.get(i).add(new ArrayList<ArrayList<Reductionstep>>());
				graph.get(i).get(j).add(new ArrayList<Reductionstep>());
				graph.get(i).get(j).add(new ArrayList<Reductionstep>());
				// Put divergence in reductiongraph
				if (i==0 && j<stepsRight.size()) {
					// if the step is a step upon a redex that was not a redex in the beginning of the divergence (and not underlined)
					if (startulRight.giveSubterm(stepsRight.get(j).positionApplied).function.functionsymbol.length()!=3)
						// underline it now
						startulRight.giveSubterm(stepsRight.get(j).positionApplied).function =
							new Function ("ul" + startulRight.giveSubterm(stepsRight.get(j).positionApplied).function.functionsymbol,
									startulRight.giveSubterm(stepsRight.get(j).positionApplied).function.arity);
					// the new (underlined) reductionstep is created using the original step, the underlined term and the underlined trs
					Reductionstep newrs = UnderlinedTRS.findRSunderl(stepsRight.get(j), new Term(startulRight), underlinedTrs);
					graph.get(0).get(j).get(1).add(newrs);
					// keep track of the new resultTerm
					startulRight = new Term(newrs.resultTerm);
				}
			}
			if (i<stepsLeft.size()) {
				// if the step is a step upon a redex that was not a redex in the beginning of the divergence (and not underlined)
				if (startulLeft.giveSubterm(stepsLeft.get(i).positionApplied).function.functionsymbol.length()!=3)
					// underline it now
					startulLeft.giveSubterm(stepsLeft.get(i).positionApplied).function =
						new Function("ul" + startulLeft.giveSubterm(stepsLeft.get(i).positionApplied).function.functionsymbol,
								startulLeft.giveSubterm(stepsLeft.get(i).positionApplied).function.arity);
				// the new (underlined) reductionstep is added to the graph
				Reductionstep newrs = UnderlinedTRS.findRSunderl(stepsLeft.get(i), new Term(startulLeft), underlinedTrs);
				graph.get(i).get(0).get(0).add(newrs);
				// keep track of the new resultTerm
				startulLeft = new Term(newrs.resultTerm);

			}
		}
		// return recursive confluence algorithm results
		return recursion(graph,stepsLeft.size(),stepsRight.size(),trs);
	}
	
	/**
	 * recursion fills in all steps in the graph
	 * @param graph this is the reductiongraph containing all the steps taken and computed
	 * index 1: row index
	 * index 2: column index
	 * index 3: 0 is left side reductionarray in tile, 1 is right side reductionarray in tile
	 * index 4: index of reductionstep in particular reductionarray
	 * @param left this is the number of steps taken on the left side
	 * @param right this is the number of steps taken on the right side
	 * @param trs this is the trs in which the steps take place
	 * @return the method returns the resulting arrays of reductionsteps
	 */
	private static ArrayList<ReductionRow> recursion (
			ArrayList<ArrayList<ArrayList<ArrayList<Reductionstep>>>> graph, int left, int right, GeneralTRS trs) {
		// pointers to indicate tiles of the reductiongraph
		int pointerLeft=0;
		int pointerRight=0;
		// find convergence for all tiles
		while (pointerLeft < left) {
			// get steps and resultTerms of current 'tile'
			ArrayList<Reductionstep> stepsLeftTile = graph.get(pointerLeft).get(pointerRight).get(0);
			ArrayList<Reductionstep> stepsRightTile = graph.get(pointerLeft).get(pointerRight).get(1);
			// create resulting reduction-rows
			ArrayList<ReductionRow> result = new ArrayList<ReductionRow>();
			// compute results
			result = getResult(stepsLeftTile,stepsRightTile,trs,stepsLeftTile.get(0).startTerm);
			// put found results into reductiongraph
			for (Reductionstep rs: result.get(0).rr)
				graph.get(pointerLeft+1).get(pointerRight).get(1).add
					(new Reductionstep(rs));
			for (Reductionstep rs: result.get(1).rr)
				graph.get(pointerLeft).get(pointerRight+1).get(0).add
					(new Reductionstep(rs));
			// move on to next tile
			pointerRight++;
			if (right == pointerRight) {
				pointerRight = 0;
				pointerLeft++;
			}
		}
		pointerLeft = 0;
		// when done with all tiles, return the results as two ArrayLists of ReductionRows
		ArrayList<Reductionstep> resultArrayLeft = new ArrayList<Reductionstep>();
		ArrayList<Reductionstep> resultArrayRight = new ArrayList<Reductionstep>();
		for (int i=0;i<left;i++)
			for (Reductionstep rs: graph.get(i).get(right).get(0)) 
				resultArrayRight.add(rs);
		for (int i=0;i<right;i++)
			for (Reductionstep rs: graph.get(left).get(i).get(1)) 
				resultArrayLeft.add(rs);
		ArrayList<ReductionRow> newresult = new ArrayList<ReductionRow>();
		newresult.add(new ReductionRow(resultArrayLeft,trs));
		newresult.add(new ReductionRow(resultArrayRight,trs));
		return newresult;
	}
	
	/**
	 * getResult will return the resulting converging steps for the divergence of this tile.
	 * @param stepsLeftTile the steps performed on the startterm on the left of the tile
	 * @param stepsRightTile the steps performed on the startterm on the right of the tile
	 * @param trs the trs in which the reductions took place
	 * @param startul this is the underlined term the recursion started with
	 * @return the method returns the convergence of the tile
	 */
	private static ArrayList<ReductionRow> getResult (ArrayList<Reductionstep> stepsLeftTile, 
			ArrayList<Reductionstep> stepsRightTile, GeneralTRS trs, Term startul) {
		// create resulting arrays
		ArrayList<ReductionRow> result = new ArrayList<ReductionRow>();
		// if there is one step to the left and one step to the right in this tile, we are good to go.
		if (stepsLeftTile.size()==1 && stepsRightTile.size()==1) {
			// copy extra redexes from other side of divergence
			Reductionstep[] rss = UnderlinedTRS.updateUnderl(stepsLeftTile.get(0),stepsRightTile.get(0),underlinedTrs);
			Term result1 = startul;
			Term result2 = startul;
			if (rss[0]!=null)	
				result1 = new Term(rss[0].resultTerm);
			if (rss[1]!=null)
				result2 = new Term(rss[1].resultTerm);
			// result is confluence WCRSN on the two resulting underlined terms
			result = ConfluenceWCRSN.confluenceWCRSN(result1, result2, underlinedTrs);
		} else
			// result is recursion upon reductionarrays in the tile
			result = confluenceOrthogonal1(stepsLeftTile,stepsRightTile,trs,startul);
		return result;
	}
	
	/**
	 * compute output for this convergence and send back resulting arrays
	 * @param stepsLeft these were the original steps on the left side of the divergence
	 * @param stepsRight these were the original steps on the right side of the divergence
	 * @param result these were the resulting converging steps
	 * @param trs this was the original (non-underlined) trs
	 * @return the method returns all non-empty non-underlined converging steps
	 */
	private static ArrayList<ReductionRow> getOutput (ArrayList<Reductionstep> stepsLeft, ArrayList<Reductionstep> stepsRight,
			ArrayList<ReductionRow> result, GeneralTRS trs) {
		// remove underlining from steps
		result = UnderlinedTRS.removeUnderlining(stepsLeft.get(stepsLeft.size()-1).resultTerm, stepsRight.get(stepsRight.size()-1).resultTerm, result, trs);
		// create string output from computed convergence (reinitialize!)
		completeOutput = new ArrayList<String>();
		completeOutput.add("Starting confluence proof for an orthogonal system using Newman's Lemma on underlined trs (recursively).");
		completeOutput.add("Created convergent reductions for diverging steps in orthogonal confluence problem:");
		completeOutput.add("Reduction array for term " + stepsLeft.get(stepsLeft.size()-1).resultTerm.termToString() + ":");
		ArrayList<ArrayList<Reductionstep>> resultNoEmpty = new ArrayList<ArrayList<Reductionstep>>();
		for (int i=0;i<2;i++) {
			resultNoEmpty.add(new ArrayList<Reductionstep>());
			if (!result.isEmpty())
				for (Reductionstep rs: result.get(i).rr) 
					if (!rs.appliedRule.equals(empty))
						resultNoEmpty.get(i).add(rs);
		}
		if (resultNoEmpty.isEmpty() || resultNoEmpty.get(0).isEmpty())
			completeOutput.add("It was empty.");
		else
			for (Reductionstep rs: resultNoEmpty.get(0))
				completeOutput.add(rs.reductionToString());
		completeOutput.add("Reduction array for term " + stepsRight.get(stepsRight.size()-1).resultTerm.termToString() + ":");
		if (resultNoEmpty.isEmpty() || resultNoEmpty.get(1).isEmpty())
			completeOutput.add("It was empty.");
		else
			for (Reductionstep rs: resultNoEmpty.get(1))
				if (!rs.appliedRule.equals(empty))
					completeOutput.add(rs.reductionToString());
		if (!resultNoEmpty.isEmpty() && !resultNoEmpty.get(0).isEmpty()) {
			completeOutput.add("Found a common reduct for the two terms: " + 
					result.get(0).rr.get(result.get(0).rr.size()-1).resultTerm.termToString());
		} else if (!resultNoEmpty.isEmpty() && !resultNoEmpty.get(1).isEmpty()) {
			completeOutput.add("Found a common reduct for the two terms: " + 
					result.get(1).rr.get(result.get(1).rr.size()-1).resultTerm.termToString());
		} else
			completeOutput.add("Found a common reduct for the two terms: " + 
					stepsLeft.get(stepsLeft.size()-1).resultTerm.termToString());
		// return computed reductionrows
		result.clear();
		result.add(new ReductionRow(resultNoEmpty.get(0),trs));
		result.add(new ReductionRow(resultNoEmpty.get(1),trs));
		return result;
	}
}