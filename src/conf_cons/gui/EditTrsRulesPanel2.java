package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to edit the selected
 * functions of the trs.
 * 
 * @author Marieke Peeters
 * 
 */

public class EditTrsRulesPanel2 extends GUI {
	
	private static JPanel head, labels, newTerms, buttons;
	private static JLabel expLabel, newLhsLabel, newRhsLabel;
	private static JTextField inputNewRuleLhs, inputNewRuleRhs;
	private static JButton continueButt, backButt, stopEditing;	
	private static String[] rule = null;

	/**
	 * This method changes the panel into a panel where the user can
	 * edit the selected rules.
	 */
	public static void editTrsRules2(ActionEvent evt) {
		// select a function to modify
		if (rule == null)
			for (String[] s: selectedRules) {
				rule = s;
				break;
			}
		
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Change rule " + rule[0] + " -> " + rule[1] + " of trs '" + trs.name +"'");
		
		// initialize panels
		initHead();
		initButt();
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(head);
		gui.getContentPane().add(buttons);
		
		gui.pack();	
	}
	
	/**
	 * This method creates a panel that explains to the user what kind of frame this is:
	 * a frame to modify a rule. It also contains textfields to enter the new
	 * left- and righthandside of the rule.
	 */
	private static void initHead() {
		// initialize components
		head = new JPanel();
		expLabel = new JLabel();
		labels = new JPanel();
		newTerms = new JPanel();
		newLhsLabel = new JLabel();
		newRhsLabel = new JLabel();
		inputNewRuleLhs = new JTextField();
		inputNewRuleRhs = new JTextField();
		
		head.setLayout(new GridLayout(3,1));
		head.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		head.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		expLabel.setText("Modify rule " + rule[0] + " -> " + rule[1] + ": ");
		head.add(expLabel);
		
		//preferences panel
		labels.setLayout(new GridLayout(1,2));
		labels.setPreferredSize(new Dimension(500,50));
		
		newLhsLabel.setText("New lefthandside");
		labels.add(newLhsLabel);
		
		newRhsLabel.setText("New righthandside");
		labels.add(newRhsLabel);
		
		head.add(labels);
		
		// preferences panel
		newTerms.setLayout(new GridLayout(1,2));
		newTerms.setBorder(new EmptyBorder(new Insets(0, 0, 20, 0)));
		newTerms.setPreferredSize(new Dimension(500,50));
		
		inputNewRuleLhs.setText("Enter new lefthandside");
		newTerms.add(inputNewRuleLhs);
		
		inputNewRuleRhs.setText("Enter new righthandside");
		newTerms.add(inputNewRuleRhs);
		
		head.add(newTerms);
	}
	
	/**
	 * This method creates a panel containing three buttons:
	 * 	- one button to continue editing
	 * 	- one button to stop editing
	 *  - one button to go back to the panel where the user can
	 *    select the features that are to be edited.
	 */
	private static void initButt() {
		// initialize components
		buttons = new JPanel();
		continueButt = new JButton();
		stopEditing = new JButton();
		backButt = new JButton();
		
		// preferences panel
		buttons.setLayout(new GridLayout(3,1));
		buttons.setBorder(new EmptyBorder(new Insets(70, 200, 10, 10)));
		buttons.setPreferredSize(new Dimension(500, 150));
		
		// create 'continue' button
		if (selectedRules.size()>1)
			continueButt.setText("Change rule and continue editing rules");
		else
			continueButt.setText("Change rule and continue editing trs");
		continueButt.addActionListener(new ActionListener() {
			/**
			 * This is a method that creates actions to be performed when the continue button is clicked.
			 */
			public void actionPerformed(ActionEvent evt) {
				boolean notchangeleft = false;
				boolean notchangeright = false;
				// check if user entered a new lefthandside
				// if not: ask if that is correct
				if (inputNewRuleLhs.getText().length()<1 || 
						inputNewRuleLhs.getText().equals("Enter new lefthandside")) {
					String[] message = new String[2];
					message[0] = ("You did not enter a new lefthandside!");
					if (selectedRules.size()>1)
						message[1] = ("Are you sure you want to continue editing " +
								"other rules of the trs?");
					else
						message[1] = ("Are you sure you want to continue editing " +
								"other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(continueButt, message, 
				    		"Continue without changing lefthandside of rule?", JOptionPane.YES_NO_OPTION);
				    // if user did not intend that, return.
				    if (!(reply == JOptionPane.YES_OPTION))
				    	return;
				    // if user did intend that, remember not to change lefthandside
				    else
				    	notchangeleft = true;
				}
				// check if user entered a new righthandside
				// if not: ask if that is correct
				if (inputNewRuleRhs.getText().length()<1 || 
						inputNewRuleRhs.getText().equals("Enter new righthandside")) {
					String[] message = new String[2];
					message[0] = ("You did not enter a new righthandside!");
					if (selectedRules.size()>1)
						message[1] = ("Are you sure you want to continue editing " +
								"other rules of the trs?");
					else
						message[1] = ("Are you sure you want to continue editing " +
								"other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(continueButt, message, 
				    		"Continue without changing righthandside of rule?", JOptionPane.YES_NO_OPTION);
				    // if user did not intend that, return.
				    if (!(reply == JOptionPane.YES_OPTION))
				    	return;
				    // if user did intend that, remember not to change righthandside
				    else
				    	notchangeright = true;
				}
				for (GeneralTRS trs: selectedTrssEditOrErase) {
					Rule currentRule = null;
					for (Rule r: trs.rules) {
						if (r.lefthandside.termToString().equals(rule[0]) && 
								r.righthandside.termToString().equals(rule[1])) {
							currentRule = r;
						}
					}
					Term left = currentRule.lefthandside;
					Term right = currentRule.righthandside;
					if (!(notchangeleft)) {
						trs.resetPointer();
						left = trs.createTerm(stringtoArray
								(inputNewRuleLhs.getText()));
						if (left == null || trs.pointer != stringtoArray
								(inputNewRuleLhs.getText()).length) {
							JOptionPane.showMessageDialog(continueButt, "The lefthandside that you entered is not a term in this trs!");
							return;
						}
					}
					if (!(notchangeright)) {
						trs.resetPointer();
						right = trs.createTerm(stringtoArray
								(inputNewRuleRhs.getText()));
						if (right == null || trs.pointer != stringtoArray
								(inputNewRuleRhs.getText()).length) {
							JOptionPane.showMessageDialog(continueButt, "The righthandside that you entered is not a term in this trs!");
							return;
						}
					}
					Rule newrule = new Rule(left,right);
					if (currentRule != null)
						trs.rules.remove(currentRule);
					if (newrule != null)
						trs.rules.add(newrule);
				}
				continueEditing(evt);
			}
		});
		buttons.add(continueButt);
		
		backButt.setText("Back to 'Select features to edit'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		buttons.add(backButt);

		createStopEditingButton(stopEditing);
		buttons.add(stopEditing);
	}
	
	/**
	 * This method redirects the panels to the appropriate panel
	 * according to the selected features that are still to be edited.
	 */
	private static void continueEditing(ActionEvent evt) {
		selectedRules.remove(rule);
		rule = null;
		if (selectedRules.isEmpty()) {
    		continueEditing2(evt);
    	} else {
    		editTrsRules2(evt);
    	}
	}
}
