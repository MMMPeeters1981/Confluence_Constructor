package conf_cons.conf;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import conf_cons.gui.*;
import conf_cons.basictrs.*;

/**
 * 
 * This class contains methods to compute certain features of a c-rank-r-term. 
 * One can calculate the rank, the base, the tall aliens, 
 * the short steps and the tall steps.
 * @author Marieke Peeters
 *
 */
public class CrankRterm {
	
	/**
	 * getRank computes the rank of a term. The rank is the number of layers,
	 * coming from different original trss, in the term.
	 * @param t this is the term that is given the rank
	 * @param combinedtrs this is the combined trs from which the term is built.
	 * @return the method returns the rank of the term.
	 */
	public static int getRank (Term t, GeneralTRS combinedtrs) {
		GeneralTRS currenttrs=null;
		boolean sametrs = false;
		currenttrs = getTRS (t, combinedtrs);
		// set all the ranks at 0
		int rankst = 0;
		int rankMax = 0;
		int rank = 0;
		boolean function = false;
		boolean def = false;
		// if the term contains subterms, check whether the subterms are from the same trs
		if (t.subterms!=null) {
			for (Term st: t.subterms) {
				boolean tempsametrs = false;
				if (st.function!=null) {
					function = true;
					for (Function f: currenttrs.functions) {
						if (st.function.equals(f)) {
							tempsametrs = true;
							break;
						} else {
							tempsametrs = false;
						}
					}
				} else {
					tempsametrs = true;
				}
				// remember whether one of the subterms is from a different trs
				if (!tempsametrs && function) {
					sametrs = false;
					def = true;
				} else if (!def)
					sametrs = true;
				// get the rank of this particular subterm
				rankst = getRank (st, combinedtrs);
				// compare new rank of subterm to maxrank and replace if new rank is higher
				if (rankst > rankMax) 
					rankMax = rankst;
			}
		}
		// increase rank by one if one of the subterms is from a different trs
		rank = rank + (sametrs? 0:1);
		// and add maximum rank of subterms to rank of the term
		rank = rank + rankMax;
		return rank;
	}
	
	/**
	 * getTRS can be used to trace from which original trs
	 * the headsymbol of a term is coming
	 * @param t this is the term that for which the headsymbol is traced
	 * @param combinedtrs this is the combined trs that contains the original trss
	 * @return the method returns the trs the term comes from
	 */
	public static GeneralTRS getTRS (Term t, GeneralTRS combinedtrs) {
		GeneralTRS returnTRS = null;
		// get and return trs of headsymbol
		one: {	for (GeneralTRS thistrs: combinedtrs.originalTrss) {
					for (Function f: thistrs.functions) {
						if (t.function!=null&&t.function.equals(f)) {
							returnTRS = thistrs;
							break one;
						}
					}
			  }	}
		return returnTRS;
	}
	
	/**
	 * getBase can be used to get the base of a term for a given rank
	 * @param decomposition decomposition is the substitution that decomposes the term into
	 * a base and tall aliens
	 * @param term this is the term for which the base is computed
	 * @return the method returns the base of the term
	 */
	static public Term getBase (Substitution decomposition, Term term) {
		Term base = new Term(term);
		for (Update u: decomposition.substitution) {
			// find the positions of each tall alien
			ArrayList<Position> positionsAlien = Position.findPosition(term,u.term);
			// create a variable term
			Term variableTerm = new Term(u.variable);
			// and replace all tall aliens with that variable
			for (Position p: positionsAlien)
				base = Term.replaceSubterm(base,p,variableTerm);
		}
		return base;
	}
	
	/**
	 * This is a method that computes all the different tall aliens in a term for a given rank
	 * @param rank this is the given rank
	 * @param term this is the term that should contain the tall aliens
	 * @param combinedtrs this is the trs from which the term is built
	 * @return the method returns an array of different tall alien subterms
	 */
	static public Term[] getAliens (int rank, Term term, GeneralTRS combinedtrs) {
		Term [] tallAliens = null;
		Set<Term> aliens = new HashSet<Term>();
		GeneralTRS trs = getTRS(term,combinedtrs);
		// aliens are of rank-1
		if (term.subterms!=null)
			for (Term st: term.subterms) {
				boolean inSet = false;
				int stRank = getRank(st,combinedtrs);
				if (stRank == rank-1) {
					if (st.x==null && !getTRS(st,combinedtrs).equals(trs)) {
						for (Term alien: aliens)
							if (alien.equals(st))
								inSet=true;
						if(!inSet)
							aliens.add(new Term(st));
					}
				}
			}
		// create an array instead of a set
		tallAliens = new Term[aliens.size()];
		int i=0;
		for (Term st: aliens) {
			tallAliens[i] = new Term(st);
			i++;
		}
		return tallAliens;
	}
	
	/**
	 * This method computes all the short steps one can perform upon a term for a given rank
	 * @param base this is the term
	 * @param combinedtrs this is the trs the term and the rules comes from
	 * @param decomposition this is the substitution that describes the decomposition of the term into base and tall aliens
	 * @return the method returns a set of short reductionsteps
	 */
	static public Set<Reductionstep> getShortSteps(Term base, GeneralTRS combinedtrs, Substitution decomposition) {
		Set<Reductionstep> shortSteps = new HashSet<Reductionstep>();
		// find all contractions of the base
		TermReduction.reductionArrayInitialize(base, 1, true, combinedtrs);
		for (Reductionstep rs: base.possibleContractions) {
			// apply substitution of tall aliens into base and
			// create c-rank-r-reductionsteps
			Term startTerm = Substitution.applySubstitution(rs.startTerm,decomposition);
			Term resultTerm = Substitution.applySubstitution(rs.resultTerm,decomposition);
			shortSteps.add(new Reductionstep(new Term(startTerm),new Term(resultTerm),rs.appliedRule,rs.positionApplied));
		}
		return shortSteps;
	}
	
	/**
	 * This method computes all the tall steps one can perform upon a term for a given rank 
	 * @param combinedtrs this is the combined trs from which the term comes
	 * @param rank this is the given rank
	 * @param term this is the term
	 * @return the method returns a set of tall reductionsteps
	 */
	static public Set<Reductionstep> getTallSteps(GeneralTRS combinedtrs, int rank, Term term) {
		Term[] tallAliens = getAliens(rank, term, combinedtrs);
		Set<Reductionstep> tallSteps = new HashSet<Reductionstep>();
		// find all contractions of aliens
		for (Term alien: tallAliens) {
			TermReduction.reductionArrayInitialize(alien, 1, true, combinedtrs);
			for (Reductionstep rs: alien.possibleContractions) {
				Set<Term> newTA = new HashSet<Term>();
				// if the term is still a tall alien, add it to new set of tall aliens
				if (getRank(rs.resultTerm,combinedtrs) == rank-1) {
					newTA.add(new Term(rs.resultTerm));
				}
				// nest steps into base and create c-rank-r-reductionstep
				ArrayList<Position> alienPos = Position.findPosition(term,alien);
				// if the alien occurs more than once, there will be occurrences 
				// of the alien in the term, after contracting the term.
				if (alienPos.size()>1)
					newTA.add(new Term(alien));
				// calculate positionApplied and tall steps for all aliens equal to this one
				for (Position pos: alienPos) {
					Term resultTerm = Term.replaceSubterm(term,pos,rs.resultTerm);
					ArrayList<Integer> position = new ArrayList<Integer>();
					for (int i: rs.positionApplied.position)
						position.add(i);
					Position positionApplied = new Position(position);
					positionApplied.position.remove(0);
					if (positionApplied.position.isEmpty())
						positionApplied = pos;
					else {
						ArrayList<Integer> nestedPosition = new ArrayList<Integer>();
						for (int i: pos.position)
							nestedPosition.add(i);
						for (int i: positionApplied.position)
							nestedPosition.add(i);
						positionApplied = new Position(nestedPosition);
					}
					Reductionstep newrs = new Reductionstep(new Term(term),new Term(resultTerm),rs.appliedRule,positionApplied);
					tallSteps.add(newrs);
				}
			}
		}
		return tallSteps;
	}
}