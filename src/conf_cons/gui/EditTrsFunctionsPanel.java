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
 * to take the user to a new view: a frame to select functions
 * of the trs to edit.
 * 
 * @author Marieke Peeters
 */
public class EditTrsFunctionsPanel extends GUI {
	
	private static JPanel editFunct, editFunctCheck, editFunctButt;
	private static JScrollPane functScroll;
	private static JLabel editFunctionsLabel;
	private static JCheckBox[] editFunctionsCheckboxes;
	private static JButton eraseFunctionsButton, continueButton, backButt, stopEditFunct;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * edit the functions of an already created trs.
	 */
	public static void editTrsFunctions (ActionEvent evt) {
		
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Edit functions of trs '" + trs.name +"'");
		
		// reset the set of selected functions
		selectedFunctions.clear();
		
		// initialize panels
		initFunctLab();
		initFunctCheck();
		initFunctButt(evt);
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(editFunct);
		gui.getContentPane().add(editFunctButt);
		
		gui.pack();
	}
		
	/**
	 * This method creates a panel containing a
	 * label that tells the user to select functions
	 * to erase or edit
	 */
	private static void initFunctLab() {
		// initialize components
		editFunct = new JPanel();
		editFunctionsLabel = new JLabel();
		
		// preferences panel
		editFunct.setLayout(new GridLayout(2,1));
		editFunct.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		editFunct.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		editFunctionsLabel.setText("Select functions to edit or erase:");
		editFunct.add(editFunctionsLabel);
	}
	
	/**
	 * This method creates a panel that contains
	 * checkboxes for all the functions in the trs.
	 * The user can select one or more functions to edit
	 * or erase from the trs.
	 */
	private static void initFunctCheck() {
		// initialize components
		editFunctCheck = new JPanel();
		functScroll = new JScrollPane(editFunctCheck);
		for (GeneralTRS trs: selectedTrssEditOrErase)
			editFunctionsCheckboxes = new JCheckBox[trs.functions.size()];
		for (int i=0; i<editFunctionsCheckboxes.length;i++)
			editFunctionsCheckboxes[i] = new JCheckBox();
		
		// set preferences panel
		editFunctCheck.setLayout(new GridLayout(editFunctionsCheckboxes.length,1));
		
		// create checkboxes and checkbox listeners, add to panel
		int j=0;
		for (GeneralTRS trs: selectedTrssEditOrErase) {
			for (Function f: trs.functions) {
				editFunctionsCheckboxes[j].setText(f.functionsymbol);
				editFunctCheck.add(editFunctionsCheckboxes[j]);
				editFunctionsCheckboxes[j].addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent evt) {
						for (JCheckBox checkbox: editFunctionsCheckboxes) {
							if (evt.getStateChange() == ItemEvent.DESELECTED)
								selectedFunctions.remove(checkbox.getText());
							else if (evt.getItemSelectable() == checkbox)
								selectedFunctions.add(checkbox.getText());
						}
					}
				});
				j++;
			}
		}
		editFunct.add(functScroll);
	}
	
	/**
	 * This method creates a panel containing four buttons:
	 * - a button to erase functions from the trs
	 * - a button to continue editing selected functions
	 * - a button that redirects the user to the panel where the to be edited
	 *   features of the trs can be selected
	 * - a button that redirects the user to the main editing panel
	 */
	private static void initFunctButt(ActionEvent evt) {
		// initialize components
		editFunctButt = new JPanel();
		eraseFunctionsButton = new JButton();
		backButt = new JButton();
		continueButton = new JButton();
		stopEditFunct = new JButton();
		
		// preferences panel
		editFunctButt.setLayout(new GridLayout(4,1));
		editFunctButt.setBorder(new EmptyBorder(new Insets(40, 210, 10, 10)));
		editFunctButt.setPreferredSize(new Dimension(500, 150));
		
		// create 'erase' button
		eraseFunctionsButton.setText("erase function(s)");
		eraseFunctionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user selected any features to erase
				if (selectedFunctions.isEmpty()) {
					JOptionPane.showMessageDialog (editFunctButt, "You did not select any functions to erase!");
					return;
				}
				Set<Function> newFunctions = new HashSet<Function>();
				for (GeneralTRS trs: selectedTrssEditOrErase) {
					for (Function f: trs.functions) {
						newFunctions.add(f);
						for (String s: selectedFunctions)
							if (s.equals(f.functionsymbol))
								newFunctions.remove(f);
					}
					trs.functions.clear();
					for (Function f: newFunctions)
						trs.functions.add(f);
				}
				selectedFeatures.remove("functions");
				editTrsFunctions(evt);
			}
		});
		editFunctButt.add(eraseFunctionsButton);
		
		// create 'continue' button
		continueButton.setText("Continue editing trs");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectedFeatures.remove("functions");
				// check if user selected any features to edit
				// if not: ask if that is correct
				if (selectedFunctions.isEmpty()) {
					String[] message = new String[2];
					message[0] = ("You did not select any functions to edit!");
					message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(editFunctButt, message, "Continue without editing functions?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply == JOptionPane.YES_OPTION) {
				    	continueEditing2(evt);
				    // if user did not intend that, return.
				    } else
				    	return;
				// if user did select functions to edit, continue to next panel
				} else 
					EditTrsFunctionsPanel2.editTrsFunctions2(evt);
			}
		});
		editFunctButt.add(continueButton);
		
		backButt.setText("Back to 'Select features to edit'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		editFunctButt.add(backButt);
		
		// create cancel button, add button to panel
		createStopEditingButton(stopEditFunct);
		editFunctButt.add(stopEditFunct);
	}
}