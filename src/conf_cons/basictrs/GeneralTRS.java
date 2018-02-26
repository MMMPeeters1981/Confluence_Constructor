package conf_cons.basictrs;

import java.util.*;

import conf_cons.gui.*;
import conf_cons.conf.*;

/**
 * GeneralTRS is a general model of a TRS
 * It contains a name, a set of variables, a set of functions, a set of rules, a set of assumptions,
 * and an array of original trss. 
 * 
 * @author Marieke Peeters
 *
 */
public class GeneralTRS {
	
	// a trs will have a name
	public String name;
	// there will exist four collections within a trs: a collection of rules, 
	// functions, variables, assumptions
	public Set<Function> functions = new HashSet<Function>();
	public Set<Variable> variables = new HashSet<Variable>();
	public Set<Rule> rules = new HashSet<Rule>();
	public Set<String[]> assumptions = new HashSet<String[]>();
	// also a trs will contain all its original trss in an array
	public GeneralTRS[] originalTrss;
	
	/**
	 * The constructor GeneralTRS creates a new TRS from arrays of Strings
	 * @param x It needs to have a name
	 * @param varia It needs an array of variables
	 * @param funct It needs a two-dimensional array that contains 
	 * functionsymbols and their arities
	 * @param rul It needs a three-dimensional array that contains 
	 * the rules: [rulenumber][left=0, right=1][symbols] 
	 * 				example for clarity: 	rules f(x) -> g(x) and
	 *  									a -> b will become the following array:
	 * 										{ {  {f,x},{g,x}  } {  {a},{b}  } }
	 * @param assump It also needs a set of assumptions given by the user, such as
	 * orthogonal, WCR or SN
	 */
	public GeneralTRS (String x, String[] varia, String[][] funct, String[][][] rul, String[] assump) {
		// Check whether the functionsymbols in the function-string-array all contain arities
		// and if the arities are all integers
		checkFunct(funct);
		// Check if all the rules in the rule-string array are complete
		checkRulCompl(rul);
		// Now we start building the TRS.
		// give the trs its name
		name = x;
		// remember that this trs is an original trs
		originalTrss = new GeneralTRS[1];
		originalTrss[0] = this;
		// Create variable-set
		for (String s: varia) {
			Variable newvar = new Variable (s);
			variables.add(newvar);
		}
		// Create function-set
		for (int i=0;i<funct.length;i++) {
			Function newfunct = new Function (funct[i][0],Integer.parseInt(funct[i][1]));
			functions.add(newfunct);
		}
		// Create rule-set
		createRules(rul);
		// Create assumption-set
		for (String s: assump) {
			String[] stringarray = new String[2];
			stringarray[0] = name;
			stringarray[1] = s;
			assumptions.add(stringarray);
		}
	}
	
	/**
	 * The constructor GeneralTRS creates a new TRS from other trss.
	 * @param x This is the name of the new trs.
	 * @param trss These are the trss that are combined into a new trs.
	 */
	public GeneralTRS(String x, GeneralTRS ... trss) {
		// give the trs its name
		name = x;
		// create array of originaltrss
		int nrTrs = 0;
		for (GeneralTRS trs1: trss)
			nrTrs=nrTrs+trs1.originalTrss.length;
		originalTrss = new GeneralTRS[nrTrs];
		int i=0;
		for (GeneralTRS trs: trss) {
			for (Variable v: trs.variables)
				this.variables.add(v);
			for (Function f: trs.functions)
				this.functions.add(f);
			for (Rule r: trs.rules)
				this.rules.add(r);
			for (String[] assumption: trs.assumptions)
				this.assumptions.add(assumption);
			for (GeneralTRS trsorig: trs.originalTrss) {
				originalTrss[i] = trsorig;
				i++;
			}
		}
		// remove duplicates from originalTrss
		ArrayList<GeneralTRS> newOrigTrs = new ArrayList<GeneralTRS>();
		for (GeneralTRS trs: originalTrss){
			if (!newOrigTrs.contains(trs))
				newOrigTrs.add(trs);
		}
		originalTrss = new GeneralTRS[newOrigTrs.size()];
		int j=0;
		for (GeneralTRS trs: newOrigTrs) {
			originalTrss[j] = trs;
			j++;
		}
	}
	
	/**
	 * This method checks whether two objects are equal.
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
		// if the term does not contain a function nor a variable, something is wrong
		else if (name==null)
			equal = false;
		// if all the elements of the objects are equal, so are the objects
		else {
			GeneralTRS trs = (GeneralTRS)obj;
			if (trs.name==null)
				equal=false;
			else if ( !(functions.containsAll(trs.functions) &&
						trs.functions.containsAll(functions)) ||
					!(variables.containsAll(trs.variables) &&
						trs.variables.containsAll(variables)) ||
					!(rules.containsAll(trs.rules) &&
						trs.rules.containsAll(rules)) ||
					!(assumptions.containsAll(trs.assumptions) && 
						trs.assumptions.containsAll(assumptions))) {
				equal = false;
			} else if (originalTrss!=null && trs.originalTrss!=null){
				if (originalTrss.length!=trs.originalTrss.length)
					equal = false;
				else {
					for (int i=0; i<originalTrss.length; i++) {
						if (!originalTrss[i].equals(trs.originalTrss[i])){
							equal=false;
							break;
						}
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
			hash = 31 * hash + (null == name ? 0 : name.hashCode());
			hash = 31 * hash + (null == functions? 0 : functions.hashCode());
			hash = 31 * hash + (null == rules? 0 : rules.hashCode());
			hash = 31 * hash + (null == assumptions? 0 : assumptions.hashCode());
			hash = 31 * hash + (null == variables? 0 : variables.hashCode());
		return hash;
	}
	
	/**
	 * This method prints a description of a GeneralTRS.
	 */
	public void printTrs() {
		System.out.println("Description of trs named " + name + ":");
		for (Rule r: rules) {
			r.printRule();
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * This method creates an array of strings describing the trs
	 * @return the description of the trs (length 1 + number of rules)
	 */
	public String[] trsToString() {
		String[] trsString = new String[1 + rules.size()]; 
		trsString[0] = ("Description of trs named " + name + ":");
		int i=0;
		for (Rule r: rules) {
			trsString[i+1] = r.lefthandside.termToString() + "->" + r.righthandside.termToString();
			i++;
		}
		return trsString;
	}
	
	/**
	 * This method checks whether the functions are complete and whether the arities are
	 * all digits
	 * @param funct this is the two-dimensional array that contains the information to create the functions
	 */
	private void checkFunct (String[][] funct) {
		for (int i=0;i<funct.length;i++) {
			if (funct[i].length == 1 || funct[i][1]=="") { 
				System.out.println ("One of the functions does not contain an arity.");
				return;
			}
			try {
				Integer.parseInt(funct[i][1]);
			}
			catch (NumberFormatException nFE) {
				System.out.println("The arity of a function must be a digit!");
				return;
			}
		}
	}
	
	/**
	 * This method checks whether all the rules are complete
	 * @param rul this is the three-dimensional array that contains the information to create the rules
	 */
	private void checkRulCompl(String[][][] rul) {
		for (int i=0;i<rul.length;i++) {
			if (rul[i].length != 2 || rul[i][1][0]==""){
				System.out.println ("There are incomplete rules (rules without righthandsides).");
				return;
			}
		}
	}
	
	// a pointer will be used to keep track of the place we are at in the string-symbol-array.
	public int pointer;
	
	/**
	 * This method creates rules from a triple array of strings describing the rules. 
	 * The method needs a three-dimensional array of strings and creates rules, 
	 * consisting of a lefthandside term and a righthandside term
	 * @param rulestring This is the three dimensional string array.
	 */
	private void createRules (String[][][] rulestring){
		// Here the pointer gets its use: it keeps track of the position in the array of string-symbols
		// that is being parsed. Her it is set to zero. It is the start of the parsing.
		for (int i=0;i<rulestring.length;i++){
			Term lefthandside = createTerm(rulestring[i][0]);
			Term righthandside = createTerm(rulestring[i][1]);
			Rule r = new Rule (lefthandside, righthandside);
			rules.add(r);
		}
	}
	
	/**
	 * This method resets the pointer for the createTermsfromRules and the 
	 * createTermfromString method.
	*/
	public void resetPointer(){
		pointer=0;
	}
	
	/**
	 *  This method will create a term out of an array of string-symbols.
	 * @param stringtoparse This method needs the string of symbols that is used to build the term.
	 * @return In the end this method must return a term instead of the array of Strings. 
	 */
	public Term createTerm(String[] stringtoparse) {
		//send the string array to the recursive createTerm2 method.
		resetPointer();
		return createTerm2(stringtoparse,1)[0];
	}
	
	/**
	 * This method will create terms out of an array of string-symbols.
	 * @param stringtoparse This method needs the string of symbols that is used to build the term.
	 * @param ar It also keeps track of the arity of the functionsymbol that was read in the last loop.
	 * @return In the end this method must return a Term instead of the array of Strings. The term can 
	 * be called upon by asking for createTerm(stringtoparse,1)[0]. Because the return is an array of terms,
	 * but never exceeds length 1.
	 */
	private Term[] createTerm2(String[] stringtoparse, int ar){
		// because a term can contain subterms, the term will be
		// represented as an array. The term itself of course is an
		// array of size 1, but the subterms can be arrays of terms
		// bigger than 1.
		Term[] t;
		boolean found;
		// Create array of terms, length = arity of headsymbol.
		t = new Term[ar];
		// Now fill all positions of the subterms with terms.
		for (int i=0;i<ar;i++) { 
			// the pointer keeps track of the position in the stringtoparse, so the first 
			// time this method is called, the first position in the string is parsed
			found = false;
			for (Function f: functions) {
			// it might be a function
				if (pointer < stringtoparse.length && f.functionsymbol.equals(stringtoparse[pointer])) {
		  			found = true;
		    		if (f.arity == 0) {
		    		// and it might be a constant 
	  					pointer ++;
		    			t[i] = new Term(f);
		    			break;
		    		} else {
		    			// or it might have subterms as arguments: 
		    			// recursion over the rest of the string
		    			pointer ++;
	    				t[i] = new Term(f,createTerm2(stringtoparse, f.arity));
	    				break;
		    		}
		    	} 
		   	}
		    // if a subterm is a variable, create a subterm from the variable
	   		for (Variable v: variables) {
				if (found == false && pointer < stringtoparse.length && v.name.equals(stringtoparse[pointer])) {
					pointer ++;
					found = true;
	    			t[i] = new Term(v);
	    		}
	   		}
		}
		return t;
	}
	
}