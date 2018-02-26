package conf_cons.conf;

import java.util.ArrayList;

import conf_cons.gui.*;
import conf_cons.basictrs.*;

/**
 * 
 * UnderlinedTRS is a class that contains all methods that have to do with
 * the creation of the underlined trs, when performing a confluence
 * proof construction for an orthogonal trs.
 * 
 * @author Marieke Peeters
 *
 */
public class UnderlinedTRS {

	private static Rule empty = new Rule(new Term(new Variable("x")),new Term(new Variable("x")));
	
	/**
	 * This method is used to underline a TRS, by underlining its rules
	 * @param trs the method needs a trs as an argument to be able to underline it
	 * @return The method returns the underlined TRS
	 */
	public static GeneralTRS underlineTRS (GeneralTRS trs) {
		// give the new trs a name: underlined plus the original name
		// and let it contain all the things the original trs contained
		GeneralTRS newtrs = new GeneralTRS("underlined" + trs.name, trs);
		// but all rules are replaced by their underlined version
		newtrs.rules.clear();
		for (Rule r: trs.rules) {
			Rule rnew = underlineRule(r);
			newtrs.rules.add(rnew);
		}
		// and all functionsymbols are underlined and added to the original functions
		for (Function f: trs.functions) {
			Function fnew = new Function("ul" + f.functionsymbol,f.arity);
			newtrs.functions.add(fnew);
		}
		return newtrs;
	}
	
	/**
	 * This method is used to underline a rule.
	 * It turns the headsymbol of the lefthandside into an uppercase symbol.
	 * @param r the method needs the rule to be able to underline it
	 * @return The new rule is returned.
	 */
	public static Rule underlineRule(Rule r) {
		// create a new rule
		Rule newLhs;
		// if lefthandside is variable, nothing changes (but this is actually 
		// illegal in term rewriting)
		if (r.lefthandside.x!=null) 
			return new Rule(r.lefthandside,r.righthandside);
		else {
			// if term is function, change headsymbol to uppercase functionsymbol
			Function newf = new Function
				("ul" + r.lefthandside.function.functionsymbol,
						r.lefthandside.function.arity);
			if (r.lefthandside.subterms!=null)
				newLhs = new Rule(new Term(newf,r.lefthandside.subterms),r.righthandside);
			else
				newLhs = new Rule (new Term(newf),r.righthandside);
		}
		// return the new rule
		return newLhs;
	}
	
	/**
	 * This method is used to underline a term. 
	 * It looks for all the redexes in the term and changes their 
	 * headsymbols into upper case symbols.
	 * @param trs this is the original trs
	 * @param t this is the term that is to be underlined
	 * @return The underlined term is returned.
	 */
	public static Term underlineTerm(GeneralTRS trs, Term t) {
		Term newt = new Term(t);
		if (t.x!=null)
			return newt;
		// get all possible contractions
		TermReduction.reductionArrayInitialize(t,1,true,trs);
		for (Reductionstep rs: t.possibleContractions) {
			// underline the headsymbol of the subterm that was contracted (if it was a function)
			Term newsubterm = newt.giveSubterm(rs.positionApplied);
			if (newsubterm.x==null) {
				Function newFunction = new Function
						("ul" + newsubterm.function.functionsymbol,newsubterm.function.arity);
				if (newsubterm.subterms!=null)
					newsubterm = new Term(newFunction,newsubterm.subterms);
				else 
					newsubterm = new Term(newFunction);
			}
			// put the underlined subterm back in place
			newt = Term.replaceSubterm(newt,rs.positionApplied,newsubterm);
		}
		return newt;
	}
	
	/**
	 * This method transforms all reductionsteps from their underlined versions back into their original versions.
	 * @param resTerm1 this is the resultTerm on the left side of the reductiondiagram in case empty steps are taken on the left side
	 * @param resTerm2 this is the resultTerm on the right side of the reductiondiagram in case empty steps are taken on the right side
	 * @param rrs these are the reductionsteps
	 * @param trs this is the original trs
	 * @return the transformed reductionsteps are returned
	 */
	public static ArrayList<ReductionRow> removeUnderlining (Term resTerm1, Term resTerm2, ArrayList<ReductionRow> rrs, GeneralTRS trs) {
		ArrayList<ArrayList<Reductionstep>> newars = new ArrayList<ArrayList<Reductionstep>>();
		ArrayList<ReductionRow> newrrs = new ArrayList<ReductionRow>();
		// for each found reductionstep
		int i=0;
		for (ReductionRow rr: rrs) {
			newars.add(new ArrayList<Reductionstep>());
			if (rr.rr!=null)
				for (Reductionstep rs: rr.rr) {
					// remove underlining of all terms: startTerm, resultTerm and of course the lefthandside of the appliedrule
					Term startTerm = removeUnderliningTerm(rs.startTerm,trs);
					Term resultTerm = removeUnderliningTerm(rs.resultTerm,trs);
					Rule appliedRule = new Rule(removeUnderliningTerm(rs.appliedRule.lefthandside,trs),rs.appliedRule.righthandside);
					newars.get(i).add(new Reductionstep(startTerm,resultTerm,appliedRule,rs.positionApplied));
				}
			if (!newars.get(i).isEmpty())
				newrrs.add(new ReductionRow(newars.get(i),trs));
			i++;
		}
		return newrrs;
	}
	
	/**
	 * This method transforms an underlined term back into the original term
	 * @param t this is the underlined term
	 * @param trs this is the original trs
	 * @return the method returns the original term
	 */
	public static Term removeUnderliningTerm(Term t, GeneralTRS trs) {
		Term newt = new Term(t);
		// check if the headsymbol of the term is a functionsymbol
		if (newt.function!=null)
			for (Function f: trs.functions)
				// if so, find the original version of the function and 
				if (sameButUnderlined(f,newt.function))
					// replace the underlined function
					if (t.subterms==null)
						newt = new Term(f);
					else
						newt = new Term(f,newt.subterms);
		// do the same for all subterms
		if (newt.subterms!=null)
			for (int i=0;i<newt.subterms.length;i++)
				newt.subterms[i]=removeUnderliningTerm(newt.subterms[i],trs);
		return newt;
	}
	
	/**
	 * This method checks if two functions are the same, except one of them, which is
	 * underlined.
	 * @param f this is the original function
	 * @param funderlined this is the underlined function
	 * @return the method returns true if these functions are equal, 
	 * when underlining is ignored, otherwise it returns false.
	 */
	private static boolean sameButUnderlined(Function f, Function funderlined) {
		// check if the functionsymbol is equal to the underlined functionsymbol
		if (funderlined.functionsymbol.length()==3) {
			if (f.functionsymbol.charAt(0)==(funderlined.functionsymbol.charAt(2)))
				return true;
		}
		return false;
	}
	
	/**
	 * equalTermUnderl will check if two terms are equivalent in the underlined and original trs.
	 * @param t This is the term in the original trs
	 * @param ult This is the term in the underlined trs
	 * @return The method returns true if the terms are equivalent
	 */
	public static boolean equalTermUnderl (Term t, Term ult) {
		boolean equal=true;
		if (t.equals(ult))
			return equal;
		// if one of the terms does not contain a function or a variable, something is wrong
		if ((t.x==null && t.function==null) ||
				(ult.x==null && ult.function==null) )
			equal=false;
		// if the terms do not both contain a variable or both contain a function or 
		// contain different functions, or different variables, they are not equal
		else if ( (t.x!=null && ult.x==null) ||
					(ult.x!=null && t.x==null) ||
					(t.x!=null && ult.x!=null && !(t.x.equals(ult.x)))||
				(t.function!=null && ult.function!=null &&
					(ult.function.functionsymbol.length() != 3 || 
						(t.function.functionsymbol.length()==1 && !(sameButUnderlined(t.function,ult.function)) ||
						(t.function.functionsymbol.length()==3 && !t.function.equals(ult.function)))))) {
			equal = false;
		} 
		// if the terms contain subterms that are not equivalent in underlined and 
		// original trs, the terms themselves are not equivalent
		if (t.subterms!=null && ult.subterms!=null) {
			if (t.subterms.length!=ult.subterms.length)
				equal = false;
			else {
				for (int i=0; i<t.subterms.length; i++) {
					if (!equalTermUnderl(t.subterms[i],ult.subterms[i])){
						equal=false;
						break;
					}
				}
			}
		}
		return equal;
	}
	
	/**
	 * This method will find the underlined version of a reductionstep for an underlined term,
	 * of which the chosen reductionsteps were taken upon the non-underlined version.
	 * @param rs this is the non-onderlined reductionstep
	 * @param t this is the underlined term
	 * @param underlinedTrs this is the underlined trs
	 * @return the method returns the underlined version of the reductionstep
	 */
	public static Reductionstep findRSunderl (Reductionstep rs, Term t, GeneralTRS underlinedTrs) {
		// if the step was the empty step, just create empty step for underlined term
		if (rs.appliedRule.equals(empty)) {
			ArrayList<Integer> nul = new ArrayList<Integer>();
			nul.add(0);
			return new Reductionstep(t,t,empty,new Position(nul));
		// if step was not empty
		} else {
			// reduce the underlined term
			TermReduction.reductionArrayInitialize(t,1,true,underlinedTrs);
			// compare the reductions of the underlined term, to the original reductionstep
			for (Reductionstep rsunderl: t.possibleContractions) {
				// if the rules are equal (but underlined) and the position is equal, the reductionsteps are equal
				if (UnderlinedTRS.equalTermUnderl(rs.appliedRule.lefthandside, rsunderl.appliedRule.lefthandside) &&
						rs.positionApplied.equals(rsunderl.positionApplied))
					return new Reductionstep(rsunderl);
			}
		}
		return null;
	}
	
	/**
	 * The method updateUnderl is used to update the underlined redexes in a reductionstep.
	 * @param rs1 this is the first reductionstep
	 * @param rs2 this is the second reductionstep
	 * @param underlinedTrs this is the underlined trs
	 * @return the method returns the two reductionsteps, but now they contain the 
	 * union of the redexes in the two reductionsteps
	 */
	public static Reductionstep[] updateUnderl (Reductionstep rs1,Reductionstep rs2,GeneralTRS underlinedTrs) {
		Reductionstep[] rss = new Reductionstep[2];
		rss[0] = new Reductionstep(rs1);
		rss[1] = new Reductionstep(rs2);
		// if the two startTerms are not equal
		if (!rss[0].startTerm.equals(rss[1].startTerm)) {
			// check which redex needs to be copied to the other startTerm and find the new startTerm 
			// and the appropriate underlined steps
			if (rss[0].startTerm.giveSubterm(rss[1].positionApplied).function.functionsymbol.length() != 3)
				// get new startTerm containing redex
				rss[0].startTerm.giveSubterm(rss[1].positionApplied).function =
					new Function("ul" + rs1.startTerm.giveSubterm(rss[1].positionApplied).function.functionsymbol,
							rs1.startTerm.giveSubterm(rss[1].positionApplied).function.arity);
			// find underlined step for new startterm
			rss[0] = findRSunderl(rs1,new Term(rss[0].startTerm),underlinedTrs);
			if (rss[1].startTerm.giveSubterm(rss[0].positionApplied).function.functionsymbol.length() != 3)
				// get new startTerm containing redex
				rss[1].startTerm.giveSubterm(rss[0].positionApplied).function =
					new Function("ul" + rs2.startTerm.giveSubterm(rss[0].positionApplied).function.functionsymbol,
							rs2.startTerm.giveSubterm(rss[0].positionApplied).function.arity);
			// find underlined step for new startterm
			rss[1] = findRSunderl(rs2,new Term(rss[1].startTerm),underlinedTrs);
		}
		return rss;
	}
}
