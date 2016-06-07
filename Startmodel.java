package trs;

/**
 *
 * Startmodel is the start of the program. It requests the following information:
 * - a string-array with all the variables of the given TRS
 * - a double string-array with one or more function-symbols and their arities.
 * - a three-double string-array with one or more rules that consist of a
 *   lefthandside consisting of 1 or more symbols and a righthandside 
 *   consisting of 1 or more symbols.
 * - a string-array with assumptions about the TRS, such as orthogonality, SN or WCR.
 *   
 * At this point the arrays are programmed in the class, but it is possible 
 * to implement a way for the user to give the program a file with the 
 * information it needs, or to give the user the possibility of giving the 
 * information via the screen.
 * 
 * Also, in this class calls will be made to the confluence classes.
 *
 * @author Marieke Peeters
 * 
 */

public class Startmodel {
	
	/**
	 *  The method main initiates two TRSes: E and CL.
	 */
	public static void main (String[] args) {
	    new GUI();
	    
	 // Create a new TRS called e.
		GeneralTRS e;
		String namee = "e";
		// The TRS e uses one variable: x.
		String[] variablese = {"x"};
		// The TRS e uses 3 functions: * (wants two arguments, a (constant) and b (constant).
		String[][] functionse = {{"*","2"},{"a","0"},{"b","0"}};
		// The TRS e uses 2 rules: 
		// *(x,x) -> x 
		// a -> b
		String[][][] rulese = {{{"*","x","x"},{"x"}},{{"a"},{"b"}}};
		// The TRS e is Strongly Normalizing and Weak Church Rosser.
		String[] assumptionse = {"WCR&SN"};
		e = new GeneralTRS (namee,variablese,functionse,rulese,assumptionse);
		// Add the trs to the set of trss known in the GUI
		GUI.trsset.add(e);
		
		// Create a new TRS called cl.
		GeneralTRS cl;
		String namecl = "cl";
		// The TRS cl uses 3 variables: x, y and z.
		String[] variablescl = {"x","y","z"};
		// The TRS cl uses 4 functions: @ (wants two arguments), I, K and S (all constants).
		String[][] functionscl = {{"@","2"},{"I","0"},{"K","0"},{"S","0"}};
		// The TRS cl uses three rules:
		// @(I,x) -> x
		// @(@(K,x),y) -> x
		// @(@(@(S,x),y),z) -> (o(o(x,z),o(y,z))
		String[][][] rulescl = {{{"@","I","x"},{"x"}},{{"@","@","K","x","y"},{"x"}},
							{{"@","@","@","S","x","y","z"},{"@","@","x","z","@","y","z"}}};
		// The TRS cl is orthogonal.
		String[] assumptionscl = {"Orthogonal"};
		cl = new GeneralTRS (namecl, variablescl,functionscl,rulescl,assumptionscl);
		// Add trs to the set of trss known in the GUI
		GUI.trsset.add(cl);
		
		GeneralTRS ortho;
		String nameortho = "ortho";
		String[] variablesortho = {};
		String[][] functionsortho = {{"f","2"},{"a","0"},{"b","0"}};
		String[][][] rulesortho = {{{"a"},{"b"}},{{"b"},{"a"}}};
		String[] assumptionsortho = {"Orthogonal"};
		ortho = new GeneralTRS (nameortho, variablesortho,functionsortho,rulesortho,assumptionsortho);
		GUI.trsset.add(ortho);
		
		// Create a new TRS called t1.
		GeneralTRS t1;
		String namet1 = "t1";
		// The TRS t1 uses 1 variable: x.
		String[] variablest1 = {"x"};
		// The TRS t1 uses 2 functions: f (wants two arguments) and a (constant).
		String[][] functionst1 = {{"f","2"},{"a","0"}};
		// The TRS t1 uses one rule:
		// f(x,x) -> x
		String[][][] rulest1 = {{{"f","x","x"},{"x"}}};
		// The TRS t1 is WCR and SN.
		String[] assumptionst1 = {"WCR&SN"};
		t1 = new GeneralTRS (namet1, variablest1,functionst1,rulest1,assumptionst1);
		// Add trs to the set of trss known in the GUI
		GUI.trsset.add(t1);
		
		// Create a new TRS called t2.
		GeneralTRS t2;
		String namet2 = "t2";
		// The TRS t2 uses 1 variable: x.
		String[] variablest2 = {"x"};
		// The TRS t2 uses 5 functions: G (wants one argument), H (wants one argument)
		// I, K and J (all constants).
		String[][] functionst2 = {{"H","1"},{"G","1"},{"I","0"},{"K","0"},{"J","0"}};
		// The TRS t2 uses five rules:
		// G(x) -> I
		// H(x) -> J
		// G(x) -> H(x)
		// I -> K
		// J -> K
		String[][][] rulest2 = {{{"G","x"},{"I"}},{{"I"},{"K"}},{{"H","x"},{"J"}},
				{{"J"},{"K"}},{{"G","x"},{"H","x"}}};
		// The TRS t2 is WCR and SN.
		String[] assumptionst2 = {"WCR&SN"};
		t2 = new GeneralTRS (namet2, variablest2,functionst2,rulest2,assumptionst2);
		// Add trs to the set of trss known in the GUI
		GUI.trsset.add(t2);
		
		// Create a new TRS called trs1.
		GeneralTRS trs1;
		String name1 = "trs1";
		// The TRS t1 uses 1 variable: x.
		String[] variables1 = {"x","y","z"};
		// The TRS t1 uses 5 functions: 
		// 3 arguments: f 
		// 1 argument: m
		// constants a, b and i.
		String[][] functions1 = {{"f","3"},{"a","0"},{"b","0"},{"i","0"},{"m","1"}};
		// The TRS t1 uses 3 rules:
		// f(x,x,y) -> i
		// a -> b
		// m(x) -> b
		String[][][] rules1 = {{{"f","x","x","y"},{"i"}},{{"a"},{"b"}},{{"m","x"},{"b"}}};
		// The TRS t1 is WCR and SN
		String[] assumptions1 = {"WCR&SN"};
		trs1 = new GeneralTRS (name1,variables1,functions1,rules1,assumptions1);
		// Add trs to the set of trss known in the GUI
		GUI.trsset.add(trs1);
		
		// Create a new TRS called trs2.
		GeneralTRS trs2;
		String name2 = "trs2";
		// The TRS t2 uses 1 variable: x.
		String[] variables2 = {"x"};
		// The TRS t2 uses 5 functions: 
		// 1 argument: G and H
		// I, K and J (all constants).
		String[][] functions2 = {{"H","1"},{"G","1"},{"K","0"},{"J","0"}};
		// The TRS t2 uses 4 rules:
		// G(x) -> J
		// H(x) -> J
		// H(x) -> G(x)
		// J -> K
		String[][][] rules2 = {{{"G","x"},{"J"}},{{"H","x"},{"J"}},
				{{"J"},{"K"}},{{"H","x"},{"G","x"}}};
		// The TRS t2 is orthogonal.
		String[] assumptions2 = {"WCR&SN"};
		trs2 = new GeneralTRS (name2, variables2,functions2,rules2,assumptions2);
		// Add trs to the set of trss known in the GUI
		GUI.trsset.add(trs2);
	}
}