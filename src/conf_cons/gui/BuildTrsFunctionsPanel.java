package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * This class is a subclass of GUI. This frame contains textfields 
 * in which the user can enter symbol and arity of functions, 
 * that will be added as functions to the new trs he or she is building.
 * After adding a function, it will be displayed next to a checkbox
 * The user can decide to remove it, by checking the checkbox.
 * 
 * @author Marieke Peeters
 * 
 */
public class BuildTrsFunctionsPanel extends GUI {

	private static JPanel functText, labPan, textPan, functOut, buttPan, contPan, addPan, backPan;
	private static JLabel giveSymb, giveArity, knownFuncts;
	private static JTextField enterSymbol, enterArity;
	private static JScrollPane checkScrollPan;
	private static JButton contButt, addButt, backButt;
	private static JCheckBox[] checkFuncts;
	private static String[][] string;
	
	/**
	 * This method changes the panel into a panel where the 
	 * user can enter the functions of the trs.
	 */
	public static void goToBuildTRSfunctions (ActionEvent evt) {
		// Set the title of the frame
		gui.setTitle("Create new TRS: enter functions");
		
		// initialize panels
		initText();
		initCheck();
		initButtons();
		
		// remove old panels and add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().add(functText);
		gui.getContentPane().add(checkScrollPan);
		gui.getContentPane().add(buttPan);
		
		gui.pack();
	}
	
	/**
	 * This method creates a new panel containing two labels
	 * that tell the user to add a function to the new TRS,
	 * and two Textfields to type the new function and its arity.
	 */
	private static void initText() {
		// initialize components
		functText= new JPanel(); 
		labPan = new JPanel();
		textPan = new JPanel();
		giveSymb = new JLabel();
		giveArity = new JLabel();
		enterSymbol = new JTextField();
		enterArity = new JTextField();
		
		labPan.setLayout(new GridLayout(2,1));
		labPan.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		labPan.setPreferredSize(new Dimension(250, 100));
		
		textPan.setLayout(new GridLayout(2,1));
		textPan.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		textPan.setPreferredSize(new Dimension(250, 100));
		
		giveSymb.setText("Enter the name of the function: ");
		labPan.add(giveSymb);
		
		enterSymbol.setColumns(1);
		textPan.add(enterSymbol);
		
		giveArity.setText("Enter the arity of the function: ");
		labPan.add(giveArity);

		enterArity.setColumns(1);
		textPan.add(enterArity);
		
		functText.setLayout(new GridLayout(1,2));
		functText.setBorder(new EmptyBorder(new Insets(20, 10, 25, 80)));
		functText.setPreferredSize(new Dimension(500, 100));
		
		functText.add(labPan);
		functText.add(textPan);
	}
	
	/**
	 * This method creates a new panel containing a label
	 * and a checkbox for each added function
	 * to show the user what functions he added to the trs.
	 */
	private static void initCheck() {
		// initialize components
		functOut = new JPanel();
		checkScrollPan = new JScrollPane(functOut);
		knownFuncts = new JLabel();
		checkFuncts = new JCheckBox[functions.size()];
		
		// Set preferences for panel
		functOut.setLayout(new GridLayout(functions.size()+1,1));
		functOut.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		
		if (functions.isEmpty())
			knownFuncts.setText("So far, you have not entered any functions.");
		else
			knownFuncts.setText("Entered functions, check to remove:");
		functOut.add(knownFuncts);
		
		string = new String[functions.size()][];
		int i=0;
		for (String[] s: functions) {
			string[i] = s;
			i++;
		}
		
		for (int j=0;j<string.length;j++) {
			checkFuncts[j] = new JCheckBox();
			checkFuncts[j].setText(string[j][0] + " (arity = " + string[j][1]+")");
			functOut.add(checkFuncts[j]);
			checkFuncts[j].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					int k=0;
					for (JCheckBox checkbox: checkFuncts) {
						if (evt.getStateChange() == ItemEvent.DESELECTED)
							functions.add(string[k]);
						else if (evt.getItemSelectable() == checkbox)
							functions.remove(string[k]);
						k++;
					}
				}
			});
		}
	}
	
	/**
	 * This method creates a new panel containing a button to 
	 * add a function to the trs, a button to continue
	 * building the trs and a button to go back to the panel 
	 * where the trs is given its variables.
	 */
	private static void initButtons() {
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
				addFunction(evt,enterSymbol.getText(),enterArity.getText());
			}
		});
		addPan.add(addButt);
		buttPan.add(addPan);

		contPan.setLayout(new GridLayout(1,1));
		contPan.setBorder(new EmptyBorder(new Insets(0, 150, 0, 10)));
		contButt.setText("Continue (and remove checked functions)");
		contButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsRulesPanel.goToBuildTRSrules(evt);
			}
		});
		contPan.add(contButt);
		buttPan.add(contPan);

		backPan.setLayout(new GridLayout(1,1));
		backPan.setBorder(new EmptyBorder(new Insets(0, 240, 0, 10)));
		backButt.setText("Back to 'Enter variables'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsVarsPanel.goToBuildTRSvariables(evt);
			}
		});
		backPan.add(backButt);
		buttPan.add(backPan);
	}
	
	/**
	 * This method adds the new function information to the
	 * array of functions that is given to GeneralTRS
	 */
	private static void addFunction (ActionEvent evt,String functionsymbol,String arity) {
		if (functionsymbol.length()>1 || functionsymbol.length()<1) {
			JOptionPane.showMessageDialog (buttPan, "Functionsymbol must be exactly one symbol!");
			return;
		}
		boolean add = true;
		int ar = -1;
		try {
			ar = Integer.parseInt(arity);
		}
		catch(NumberFormatException nFE) {
		    JOptionPane.showMessageDialog (buttPan, "Arity must be a digit!");
		    return;
		}
		if (!(ar==-1)) {
			String[] function = new String[2];
			function[0] = functionsymbol;
			function[1] = arity;
			for (String[] f: functions)
				if (f[0].equals(functionsymbol)) {
					JOptionPane.showMessageDialog (buttPan, "You already entered that function!");
					add=false;
				}
			for (String v: variables)
				if (v.equals(functionsymbol)) {
					JOptionPane.showMessageDialog (buttPan, "You already entered that symbol as a variable!");
					add=false;
				}
			if (add) {
				functions.add(function);
				BuildTrsFunctionsPanel.goToBuildTRSfunctions(evt);
			}
		}
	}
}