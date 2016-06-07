package trs;

import java.util.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to select variables
 * of the trs for editing.
 * 
 * @author Marieke Peeters
 */
public class EditTrsVarsPanel extends GUI {

	private static JPanel editVarLabCheck, editVarCheck, editVarButt;
	private static JScrollPane editScroll;
	private static JLabel editVariablesLabel;
	private static JCheckBox[] editVariablesCheckboxes;
	private static JButton eraseVariablesButton, continueButton, backButt, stopEditVar;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * edit or remove the variables of an already created trs.
	 */
	public static void editTrsVariables (ActionEvent evt){
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Edit variables of trs '" + trs.name +"'");
		
		// reset the set of selected variables
		selectedVariables.clear();
		
		// initialize panels
		initVarLab();
		initVarCheck();
		initVarButt(evt);
		initCancel();
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(editVarLabCheck, BorderLayout.WEST);
		gui.getContentPane().add(editVarButt, BorderLayout.WEST);
		
		gui.pack();
	}
	
	/**
	 * This method creates a panel containing a
	 * label that tells the user to select variables
	 * to erase or edit
	 */
	private static void initVarLab() {
		// initialize components
		editVarLabCheck = new JPanel();
		editVariablesLabel = new JLabel();
		
		// preferences panel
		editVarLabCheck.setLayout(new GridLayout(2,1));
		editVarLabCheck.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		editVarLabCheck.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		editVariablesLabel.setText("Select variables to edit or erase:");
		editVarLabCheck.add(editVariablesLabel);
	}
	
	/**
	 * This method creates a panel that contains
	 * checkboxes for all the variables in the trs.
	 * The user can select one or more variables to edit
	 * or erase from the trs.
	 */
	private static void initVarCheck() {
		// initialize components
		editVarCheck = new JPanel();
		editScroll = new JScrollPane(editVarCheck);
		for (GeneralTRS trs: selectedTrssEditOrErase)
			editVariablesCheckboxes = new JCheckBox[trs.variables.size()];
		for (int i=0; i<editVariablesCheckboxes.length;i++)
			editVariablesCheckboxes[i] = new JCheckBox();
		
		// set preferences panel
		editVarCheck.setLayout(new GridLayout(editVariablesCheckboxes.length,1));
		
		// create checkboxes and checkbox listeners, add to panel
		int j=0;
		for (GeneralTRS trs: selectedTrssEditOrErase) {
			for (Variable v: trs.variables) {
				editVariablesCheckboxes[j].setText(v.name);
				editVarCheck.add(editVariablesCheckboxes[j]);
				editVariablesCheckboxes[j].addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent evt) {
						for (JCheckBox checkbox: editVariablesCheckboxes) {
							if (evt.getStateChange() == ItemEvent.DESELECTED)
								selectedVariables.remove(checkbox.getText());
							else if (evt.getItemSelectable() == checkbox)
								selectedVariables.add(checkbox.getText());
						}
					}
				});
				j++;
			}
		}
		editVarLabCheck.add(editScroll);
	}
	
	/**
	 * This method creates a panel containing two buttons:
	 * - a button to erase variables from the trs
	 * - a button to continue editing selected variables
	 */
	private static void initVarButt(ActionEvent evt) {
		// initialize components
		editVarButt = new JPanel();
		eraseVariablesButton = new JButton();
		continueButton = new JButton();
		
		// preferences panel
		editVarButt.setLayout(new GridLayout(4,1));
		editVarButt.setBorder(new EmptyBorder(new Insets(45, 200, 10, 10)));
		editVarButt.setPreferredSize(new Dimension(500, 150));
		
		// create 'erase' button
		eraseVariablesButton.setText("Erase variable(s)");
		eraseVariablesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user selected any features to erase
				if (selectedVariables.isEmpty()) {
					JOptionPane.showMessageDialog (editVarButt, "You did not select any variables to erase!");
					return;
				}
				Set<Variable> newVariables = new HashSet<Variable>();
				for (GeneralTRS trs: selectedTrssEditOrErase) {
					for (Variable v: trs.variables) {
						newVariables.add(v);
						for (String s: selectedVariables)
							if (s.equals(v.name))
								newVariables.remove(v);
					}
					trs.variables.clear();
					for (Variable v: newVariables)
						trs.variables.add(v);
				}
				selectedFeatures.remove("variables");
				editTrsVariables(evt);
			}
		});
		editVarButt.add(eraseVariablesButton);
		
		// create 'continue' button
		continueButton.setText("Continue editing trs");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectedFeatures.remove("variables");
				// check if user selected any features to edit
				// if not: ask if that is correct
				if (selectedVariables.isEmpty()) {
					String[] message = new String[2];
					message[0] = ("You did not select any variables to edit!");
					message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(editVarButt, message, "Continue editing other features?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply == JOptionPane.YES_OPTION) {
				    	continueEditing2(evt);
				    // if user did not intend that, return.
				    } else
				    	return;
				// if user did select variables to edit, continue to next panel
				} else 
					EditTrsVarsPanel2.editTrsVariables2(evt);
			}
		});
		editVarButt.add(continueButton);
	}
	
	/**
	 * This method is used to create a panel containing two buttons:
	 * - 	a button that takes the user back to the panel where the features
	 * 		that are to be edited can be selected
	 * -	a button that will stop the editing sequence of panels and takes
	 * 		the user back to the main editing panel
	 */
	private static void initCancel() {
		// initialize components
		stopEditVar = new JButton();
		backButt = new JButton();
		
		backButt.setText("Back to 'Select features to edit'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		editVarButt.add(backButt);
		
		// create cancel button, add button to panel
		createStopEditingButton(stopEditVar);
		editVarButt.add(stopEditVar);
	}
}

