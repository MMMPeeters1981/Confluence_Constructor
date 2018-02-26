package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.util.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to select rules
 * of the trs to edit.
 * 
 * @author Marieke Peeters
 */
public class EditTrsRulesPanel extends GUI {
	
	private static JPanel editRules, editRulesCheck, editButt;
	private static JScrollPane rulesScroll;
	private static JLabel editRulesLabel;
	private static JCheckBox[] editRulesCheckboxes;
	private static String[][] checkboxtextarray;
	private static JButton eraseRulesButton, continueButton, stopEditRules, backButt;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * edit the rules of an already created trs.
	 */
	public static void editTrsRules (ActionEvent evt) {
		
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Edit rules of trs '" + trs.name + "'");
		
		// reset the set of selected Rules
		selectedRules.clear();
		
		// initialize panels
		initRulesLab();
		initRulesCheck();
		initRulesButt(evt);
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(editRules);
		gui.getContentPane().add(editButt);
		
		gui.pack();
	}
		
	/**
	 * This method creates a panel containing a
	 * label that tells the user to select rules
	 * to erase or edit
	 */
	private static void initRulesLab() {
		// initialize components
		editRules = new JPanel();
		editRulesLabel = new JLabel();
		
		// preferences panel
		editRules.setLayout(new GridLayout(2,1));
		editRules.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		editRules.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		editRulesLabel.setText("Select rules to edit or erase:");
		editRules.add(editRulesLabel);
	}
	
	/**
	 * This method creates a panel that contains
	 * checkboxes for all the rules in the trs.
	 * The user can select one or more rules to edit
	 * or erase from the trs.
	 */
	private static void initRulesCheck() {
		// initialize components
		editRulesCheck = new JPanel();
		rulesScroll = new JScrollPane(editRulesCheck);
		for (GeneralTRS trs: selectedTrssEditOrErase) {
			editRulesCheckboxes = new JCheckBox[trs.rules.size()];
		}
		for (int i=0; i<editRulesCheckboxes.length;i++)
			editRulesCheckboxes[i] = new JCheckBox();
		checkboxtextarray = new String[editRulesCheckboxes.length][2];
		for (int i=0; i<checkboxtextarray.length;i++) {
			checkboxtextarray[i] = new String[2];
			for (int j=0;j<2;j++)
				checkboxtextarray[i][j] = new String();
		}
			
		// set preferences panel
		editRulesCheck.setLayout(new GridLayout(editRulesCheckboxes.length,1));
		
		// create checkboxes and checkbox listeners, add to panel
		int j=0;
		for (GeneralTRS trs: selectedTrssEditOrErase) {
			for (Rule r: trs.rules) {
				editRulesCheckboxes[j].setText(r.lefthandside.termToString() + " -> " + r.righthandside.termToString());
				checkboxtextarray[j][0] = r.lefthandside.termToString();
				checkboxtextarray[j][1] = r.righthandside.termToString();
				editRulesCheck.add(editRulesCheckboxes[j]);
				
				editRulesCheckboxes[j].addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent evt) {
						for (JCheckBox checkbox: editRulesCheckboxes) {
							if (evt.getItemSelectable() == checkbox) {
								for (String[] s: checkboxtextarray)
									if (checkbox.getText().equals((s[0] + " -> " + s[1]))) {
									selectedRules.add(s);
										if (evt.getStateChange() == ItemEvent.DESELECTED)
											selectedRules.remove(s);
									}
							}
						}
					}
				});
				j++;
			}
		}
		editRules.add(rulesScroll);
	}
	
	/**
	 * This method creates a panel containing four buttons:
	 * - a button to erase rules from the trs
	 * - a button to continue editing selected rules
	 * - a button that redirects the user to the panel where the to be edited
	 *   features of the trs can be selected
	 * - a button that redirects the user to the main editing panel
	 */
	private static void initRulesButt(ActionEvent evt) {
		// initialize components
		editButt = new JPanel();
		eraseRulesButton = new JButton();
		continueButton = new JButton();
		backButt = new JButton();
		stopEditRules = new JButton();
		
		// preferences panel
		editButt.setLayout(new GridLayout(4,1));
		editButt.setBorder(new EmptyBorder(new Insets(40, 210, 10, 10)));
		editButt.setPreferredSize(new Dimension(500, 150));
		
		// create 'erase' button
		eraseRulesButton.setText("erase rule(s)");
		eraseRulesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user selected any features to erase
				if (selectedRules.isEmpty()) {
					JOptionPane.showMessageDialog (editButt, "You did not select any rules to erase!");
					return;
				}
				Set<Rule> newRules = new HashSet<Rule>();
				for (GeneralTRS trs: selectedTrssEditOrErase) {
					for (Rule r: trs.rules) {
						newRules.add(r);
						for (String[] s: selectedRules)
							if (s[0].equals(r.lefthandside.termToString2()) && s[1].equals(r.righthandside.termToString2()))
								newRules.remove(r);
					}
					trs.rules.clear();
					for (Rule r: newRules)
						trs.rules.add(r);
				}
				selectedFeatures.remove("rules");
				editTrsRules(evt);
			}
		});
		editButt.add(eraseRulesButton);
		
		// create 'continue' button
		continueButton.setText("Continue editing trs");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectedFeatures.remove("rules");
				// check if user selected any features to edit
				// if not: ask if that is correct
				if (selectedRules.isEmpty()) {
					String[] message = new String[2];
					message[0] = ("You did not select any Rules to edit!");
					message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(editButt, message, "Continue without editing rules?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply == JOptionPane.YES_OPTION) {
				    	continueEditing2(evt);
				    // if user did not intend that, return.
				    } else
				    	return;
				// if user did select Rules to edit, continue to next panel
				} else 
					EditTrsRulesPanel2.editTrsRules2(evt);
			}
		});
		editButt.add(continueButton);
		
		backButt.setText("Back to 'Select features to edit'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		editButt.add(backButt);
		
		// create cancel button, add button to panel
		createStopEditingButton(stopEditRules);
		editButt.add(stopEditRules);
	}
}
