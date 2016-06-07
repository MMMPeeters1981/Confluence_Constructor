package trs;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

/**
 * This class is a subclass of GUI. This frame contains textfields 
 * in which the user can enter left- and righthandsides of rules, 
 * that will be added as rules to the new trs he or she is building.
 * After adding a rule, it will be displayed next to a checkbox
 * The user can decide to remove it, by checking the checkbox.
 * 
 * @author Marieke Peeters
 * 
 */
public class BuildTrsRulesPanel extends GUI {

	private static JPanel explPan, labPan, entPan, outPan, buttPan, contPan, addPan, backPan;
	private static JScrollPane checkScroll;
	private static JLabel addRule, arrow, knownRules;
	private static JTextField left, right;
	private static JCheckBox[] checkRules;
	private static JButton addButt, contButt, backButt;
	private static String[][][] string;
	
	/**
	 * This method changes the panel into a panel where the user can enter
	 * the rules that are valid for the TRS.
	 */
	public static void goToBuildTRSrules (ActionEvent evt) {
		// set the title of the frame
		gui.setTitle("Create new TRS: enter rules");
		
		// initialize panels
		initEnter();
		initCheck();
		initButtons();
		
		// remove old panels and add new panels to frame		
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().add(explPan);
		gui.getContentPane().add(checkScroll);
		gui.getContentPane().add(buttPan);
		
		gui.pack();
	}
	
	/**
	 * This panel explains to the user he or she
	 * should enter the rules of the new trs.
	 */
	private static void initEnter(){
		// initialize components
		explPan = new JPanel();
		labPan = new JPanel();
		entPan = new JPanel();
		addRule = new JLabel();
		left = new JTextField();
		arrow = new JLabel();
		right = new JTextField();
		
		labPan.setLayout(new GridLayout(1,3));
		labPan.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		labPan.setPreferredSize(new Dimension(500, 50));

		entPan.setLayout(new GridLayout(1,3));
		entPan.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		entPan.setPreferredSize(new Dimension(500, 50));
		
		addRule.setText("Add a rule to TRS: ");
		labPan.add(addRule);
		
		left.setText("lefthandside");
		entPan.add(left);
		
		arrow.setText("          ->   		");
		entPan.add(arrow);
		
		right.setText("righthandside");
		entPan.add(right);

		explPan.setLayout(new GridLayout(2,1));
		explPan.setBorder(new EmptyBorder(new Insets(20, 10, 25, 175)));
		explPan.setPreferredSize(new Dimension(500, 100));
		explPan.add(labPan);
		explPan.add(entPan);
	}
	
	/**
	 * This method creates a new panel containing a label
	 * and a checkbox for each added rule
	 * to show the user what rules he added to the trs. The checkboxes 
	 * can be checked to remove false rules.
	 */
	private static void initCheck(){
		// initialize components
		outPan = new JPanel();
		checkScroll = new JScrollPane(outPan);
		knownRules = new JLabel();
		checkRules = new JCheckBox[rules.size()];
		
		// Set preferences for panel
		outPan.setLayout(new GridLayout(rules.size()+1,1));
		outPan.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		
		if (rules.isEmpty())
			knownRules.setText("So far, you have not entered any rules.");
		else
			knownRules.setText("Entered rules, check to remove:");
		outPan.add(knownRules);
		
		string = new String[rules.size()][][];
		int i=0;
		for (String[][] r: rules) {
			string[i] = r;
			i++;
		}
		
		for (int j=0;j<string.length;j++) {
			checkRules[j] = new JCheckBox();
			String left = arrayToString(string[j][0]);
			String right = arrayToString(string[j][1]);
			checkRules[j].setText( left + " -> " + right);
			outPan.add(checkRules[j]);
			checkRules[j].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					int k=0;
					for (JCheckBox checkbox: checkRules) {
						if (evt.getStateChange() == ItemEvent.DESELECTED)
							rules.add(string[k]);
						else if (evt.getItemSelectable() == checkbox)
							rules.remove(string[k]);
						k++;
					}
				}
			});
		}
	}
	
	/**
	 * This method creates a new panel containing a button to 
	 * add a rule to the trs, a button to continue
	 * building the trs and a button to go back to the panel 
	 * where the trs is given its functions.
	 */
	private static void initButtons(){
		// initialize components
		buttPan = new JPanel();
		addPan = new JPanel();
		contPan = new JPanel();
		backPan = new JPanel();
		addButt = new JButton();
		contButt = new JButton();
		backButt = new JButton();

		// Set preferences for panel
		buttPan.setLayout(new GridLayout(3,1));
		buttPan.setBorder(new EmptyBorder(new Insets(5, 10, 10, 10)));
		buttPan.setPreferredSize(new Dimension(500, 100));
		
		// settings of buttons and add buttons to panel
		addPan.setLayout(new GridLayout(1,1));
		addPan.setBorder(new EmptyBorder(new Insets(0, 375, 0, 10)));
		addButt.setText("Add");
		addButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addRule(evt,left.getText(),right.getText());
			}
		});
		addPan.add(addButt);
		buttPan.add(addPan);

		contPan.setLayout(new GridLayout(1,1));
		contPan.setBorder(new EmptyBorder(new Insets(0, 150, 0, 10)));
		contButt.setText("Continue (and remove checked rules)");
		contButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsAssumpPanel.goToBuildTRSassumptions(evt);
			}
		});
		contPan.add(contButt);
		buttPan.add(contPan);

		backPan.setLayout(new GridLayout(1,1));
		backPan.setBorder(new EmptyBorder(new Insets(0, 240, 0, 10)));
		backButt.setText("Back to 'Enter functions'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsFunctionsPanel.goToBuildTRSfunctions(evt);
			}
		});
		backPan.add(backButt);
		buttPan.add(backPan);
	}
	
	/**
	 * This method adds the new rule to the array of rules
	 * that will be sent to GeneralTRS
	 */
	private static void addRule (ActionEvent evt, String lefthandside, String righthandside) {
		if (lefthandside.length()<1 || righthandside.length()<1 || lefthandside.equals("lefthandside") ||
				righthandside.equals("righthandside")) {
			JOptionPane.showMessageDialog (buttPan, "Please enter a valid rule!");
			return;
		}
		
		// create a new rule from the two strings that were given by the user.
		String[][] newrule = new String[2][];
		String[] lefthandsideArray = stringtoArray(lefthandside);
		String[] righthandsideArray = stringtoArray(righthandside);
		newrule[0] = lefthandsideArray;
		newrule[1] = righthandsideArray;
		
		// Now check if the rule is new and
		// if the rule consists of known symbols.
		boolean newr = false;
		boolean knownSymbol = false;
		// loop over all rules
		for (String[][] rule: rules) {
			one: { for (int j=0; j<2; j++) {
					// if the left- or righthandsides of the rules
					// have different lengths, the rules are different
					if (rule[j].length != newrule[j].length) {
						newr = true;
						break one;
					}
					// otherwise check all symbols on equality
					else for (int i=0; i<rule[j].length; i++) {
						if (!rule[j][i].equals(newrule[j][i])) {
							newr = true;
							break one;
						}
					}
			} }
		}
		two: {for (int j=0; j<2;j++)
			// now loop through the symbols of the new rule
			for (String symbol: newrule[j]) {
				knownSymbol = false;
				if (variables.isEmpty() && functions.isEmpty()) {
					break two;
				}
				// all symbols need to be found
				// either as a variable or as a function
				for (String variable: variables)
					if (symbol.equals(variable)) {
						knownSymbol=true;
					}
				for (String[] function: functions) 
					if (symbol.equals(function[0])) {
						knownSymbol=true;
					}
				if (knownSymbol==false) {
					break;
				}
			} }
		if ((rules.isEmpty()||newr) && knownSymbol) {
			rules.add(newrule);
			BuildTrsRulesPanel.goToBuildTRSrules(evt);
		} else {
			JOptionPane.showMessageDialog (buttPan, "Please enter a valid rule!");
			return;
		}
	}
}