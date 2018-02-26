package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.util.*;
import javax.swing.*;

import java.awt.event.*;

/**
 * This class creates the user interface. It extends JFrame and it contains a couple of
 * subclasses, each of which contains a couple of methods to take the user to a new view.
 * 
 * @author Marieke Peeters
 */
public class GUI {
	
	public static JFrame gui = new JFrame();
	
	static String name;
	static Set<String> variables = new HashSet<String>();
	static Set<String[]> functions = new HashSet<String[]>();
	static Set<String[][]> rules = new HashSet<String[][]>();
	static Set<String> assumptions = new HashSet<String>();
	public static Set<GeneralTRS> trsset = new HashSet<GeneralTRS>();
	static Set <GeneralTRS> selectedTrssConfluence = new HashSet<GeneralTRS>();
	static Set <GeneralTRS> selectedTrssEditOrErase = new HashSet<GeneralTRS>();
	static Set <String> selectedFeatures = new HashSet<String>();
	static Set <String> selectedVariables = new HashSet<String>();
	static Set <String> selectedFunctions = new HashSet<String>();
	static Set <String[]> selectedRules = new HashSet<String[]>();
	static int lTotal, rTotal;
	static int stepsLeft, stepsRight;
	static ArrayList<Reductionstep> redRowL = new ArrayList<Reductionstep>();
	static ArrayList<Reductionstep> redRowR = new ArrayList<Reductionstep>();
	static Reductionstep[] rss;
	static Term term;
	static GeneralTRS newTRS;
	
	/**
	 * Constructor GUI
	 */
	public GUI() {
		gui.setSize(500,300);
		gui.setVisible(true);
		init();
    }
    
 	/**
 	 * initialize first panel
 	 */
	private void init() {
		// set the title of the frame
        gui.setTitle("Confluence Checker");
        gui.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
        OpeningPanel.openingPanel();
	}
	
	/**
	 * Close GUI
	 */
	public static void exitForm(WindowEvent evt) {
        System.exit(0);
    }
	
	/**
	 * Close GUI
	 */
	public static void exitForm(ActionEvent evt) {
        System.exit(0);
    }
	
	/**
	 * This method creates a 'stop editing' button, that takes the user
	 * back to the main editing panel.
	 * @param button This is the button that needs to be turned into a 'stop editing button'.
	 */
	public static void createStopEditingButton (JButton button) {
		button.setText("Stop editing");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainEditingPanel.goToMainEditingPanel(evt);
			}
		});
	}
	
	/**
	 * Convert a string into an array of string symbols.
	 * This method ignores parentheses and comma's.
	 * @param s This is the string that is converted to an array.
	 */
	public static String[] stringtoArray (String s) {
		StringBuffer buf = new StringBuffer(s);
		String[] elements  = new String[s.length()];
		int z=0;
		while (buf.length() > 0) {
			char element = buf.charAt(0);
			if (!(Character.toString(element).equals("(") ||
					Character.toString(element).equals(",") ||
					Character.toString(element).equals(")"))) {
				elements[z] = Character.toString(element);
				z++;
			}
			buf.deleteCharAt(0);
		}
		String[] newElements = null;
		boolean useNew = false;
		for (int i=0;i<elements.length;i++)
			if (elements[i] == null) {
				newElements = new String[i];
				useNew = true;
				break;
			}
		if (useNew) {
			for (int i=0;i<newElements.length;i++)
				newElements[i] = elements[i];
			return newElements;
		}
		return elements;
	}
	
	/**
	 * This method is used to create glue all elements of 
	 * an array of strings together into one String.
	 * @param s The string array
	 * @return The string of the glued pieces
	 */
	public static String arrayToString (String[] s) {
		String string = new String("");
		for (String s1: s)
			string = (string + s1);
		return string;
	}
	
	/**
	 * This method is used to redirect the panel towards the appropriate
	 * panel, according to the features of the trs that are still selected
	 * to be edited.
	 * @param evt An ActionEvent, probably clicking a button.
	 */
	public static void continueEditing2 (ActionEvent evt) {
		if (selectedFeatures.isEmpty()) {
    		MainEditingPanel.goToMainEditingPanel (evt);
		}
		for (String s: selectedFeatures) {
			if (s.equals("name"))
				EditTrsNamePanel.editTrsName(evt);
			else if (s.equals("variables"))
				EditTrsVarsPanel.editTrsVariables(evt);
			else if (s.equals("functions"))
				EditTrsFunctionsPanel.editTrsFunctions(evt);
			else if (s.equals("rules"))
				EditTrsRulesPanel.editTrsRules(evt);
			else if (s.equals("assumptions"))
				EditTrsAssumpPanel.editTrsAssumptions(evt);
		}
	}
}
