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
 * to take the user to a new view: a frame to change the assumptions
 * of the selected trs.
 * 
 * @author Marieke Peeters
 */
public class EditTrsAssumpPanel extends GUI {

	private static JPanel editAssump, editAssumpCheck, editAssumpButt;
	private static JLabel editAssumpLabel;
	private static JCheckBox[] editAssumpCheckboxes;
	private static JButton continueButton, stopEditAssump, backButt;

	private static Set<String[]> assumptionsTrs = new HashSet<String[]>();

	/**
	 * This method changes the panel into a panel where the user can
	 * edit the assumptions of an already created trs.
	 */
	public static void editTrsAssumptions (ActionEvent evt) {
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) {
			gui.setTitle("Edit assumptions of trs '" + trs.name +"'");
			for (String[] s: trs.assumptions)
				assumptionsTrs.add(s);
		}
		
		// initialize panels
		initAssumpLab();
		initAssumpCheck();
		initAssumpButt(evt);
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(editAssump, BorderLayout.WEST);
		gui.getContentPane().add(editAssumpButt, BorderLayout.WEST);
		
		gui.pack();
	}
		
	/**
	 * This method creates a panel containing a
	 * label that tells the user to select rules
	 * to erase or edit
	 */
	private static void initAssumpLab() {
		// initialize components
		editAssump = new JPanel();
		editAssumpLabel = new JLabel();
		
		// preferences panel
		editAssump.setLayout(new GridLayout(2,1));
		editAssump.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		editAssump.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		editAssumpLabel.setText("Select the assumptions of this trs:");
		editAssump.add(editAssumpLabel);
	}
	
	/**
	 * This method creates a panel that contains
	 * checkboxes for all the possible assumptions in the trs.
	 * The user can (de)select one or more assumptions to add
	 * or erase from the trs.
	 */
	private static void initAssumpCheck() {
		// initialize components
		editAssumpCheck = new JPanel();
		editAssumpCheckboxes = new JCheckBox[2];
		for (int i=0; i<editAssumpCheckboxes.length;i++)
			editAssumpCheckboxes[i] = new JCheckBox();
		
		// set preferences panel
		editAssumpCheck.setLayout(new GridLayout(3,2));
		editAssumpCheck.setPreferredSize(new Dimension(500, 150));
		
		editAssumpCheckboxes[0].setText("WCR&SN");
		editAssumpCheckboxes[1].setText("Orthogonal");
		
		for (int i=0; i<editAssumpCheckboxes.length;i++) {
			editAssumpCheck.add(editAssumpCheckboxes[i]);
			for (String[] s: assumptionsTrs) {
				if (s[1].equals(editAssumpCheckboxes[i].getText())) {
					editAssumpCheckboxes[i].setSelected(true);
				}
			}
			editAssumpCheckboxes[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: editAssumpCheckboxes) {
						if (evt.getStateChange() == ItemEvent.DESELECTED) {
							Set <String[]> newAssumptions = new HashSet<String[]>();
							for (String[] s : assumptionsTrs)
								newAssumptions.add(s);
							for (String[] s: assumptionsTrs)
								if (s[1].equals(checkbox.getText())) {
									newAssumptions.remove(s);
								}
						} else if (evt.getItemSelectable() == checkbox){
							String[] s = new String[2];
							for (GeneralTRS trs: selectedTrssEditOrErase)
								s[0] = trs.name;
							s[1] = checkbox.getText();
							assumptionsTrs.add(s);
						}
					}
				}
			});
		}
		editAssump.add(editAssumpCheck);
	}
	
	/**
	 * This method creates a panel containing three buttons:
	 * - a button to continue editing other features
	 * - a button to stop editing and return to main editing panel
	 * - a button that redirects the user to the panel where the to be edited
	 *   features of the trs can be selected
	 */
	private static void initAssumpButt(ActionEvent evt) {
		// initialize components
		editAssumpButt = new JPanel();
		continueButton = new JButton();
		stopEditAssump = new JButton();
		backButt = new JButton();
		
		// preferences panel
		editAssumpButt.setLayout(new GridLayout(3,1));
		editAssumpButt.setBorder(new EmptyBorder(new Insets(70, 200, 10, 10)));
		editAssumpButt.setPreferredSize(new Dimension(500, 150));
		
		// create 'continue' button
		continueButton.setText("Continue editing trs");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectedFeatures.remove("assumptions");
				// check if user selected any features to edit
				// if not: ask if that is correct
				if (assumptionsTrs.isEmpty()) {
					String[] message = new String[2];
					message[0] = ("You did not select any assumptions!");
					message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(editAssumpButt, message, "Continue without adding assumptions to the trs?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply == JOptionPane.YES_OPTION) {
				    	continueEditing(evt);
				    // if user did not intend that, return.
				    } else
				    	return;
				// if user did select assumptions, continue to next panel
				} else 
					continueEditing(evt);
			}
		});
		editAssumpButt.add(continueButton);
		
		backButt.setText("Back to 'Select features to edit'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		editAssumpButt.add(backButt);
		
		// create cancel button, add button to panel
		createStopEditingButton(stopEditAssump);
		editAssumpButt.add(stopEditAssump);
	}	
	
	/**
	 * A method to continue editing, depending on the selected features 
	 * the method takes the user to a new editing panel.
	 * It also saves the new information about the assumptions into the trs.
	 */
	private static void continueEditing(ActionEvent evt) {
		for (GeneralTRS trs: selectedTrssEditOrErase) {
			trs.assumptions.clear();
			for (String[] s: assumptionsTrs) {
				trs.assumptions.add(s);
			}
		}
		continueEditing2(evt);
	}
}