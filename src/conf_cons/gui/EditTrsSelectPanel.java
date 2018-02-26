package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to select features of the trs
 * to edit.
 * 
 * @author Marieke Peeters
 */
public class EditTrsSelectPanel extends GUI {
	
	private static JPanel editPanel, editButtons;
	private static JLabel labelEdit;
	private static String[] featuresString;
	private static JCheckBox[] checkboxEdit;
	private static JButton buttonEdit, buttonStopEdit;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * edit the features of an already created trs
	 */
	public static void editTrs (ActionEvent evt) {
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Select features of trs '" + trs.name + "' to edit");
		
		// initialize panels
		initTrsFeatCheckb();
		initTrsEditButtons();
		
		// remove old panels from frame, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().add(editPanel, BorderLayout.WEST);
		gui.getContentPane().add(editButtons, BorderLayout.WEST);
		
		gui.pack();
	}
	
	/**
	 * This method creates a panel that contains checkboxes
	 * for all the features of the trs that can be edited.
	 * The user can select certain features for editing.
	 * The selected features are stored in a set in GUI.
	 */
	private static void initTrsFeatCheckb() {
		// initialize components
		editPanel = new JPanel();
		labelEdit = new JLabel();
		checkboxEdit = new JCheckBox[5];
		for (int i=0;i<checkboxEdit.length;i++)
			checkboxEdit[i] = new JCheckBox();
		featuresString = new String[5];
		featuresString[0] = "name";
		featuresString[1] = "variables";
		featuresString[2] = "functions";
		featuresString[3] = "rules";
		featuresString[4] = "assumptions";
		
		// preferences for panel
		editPanel.setLayout(new BoxLayout(editPanel,BoxLayout.Y_AXIS));
		editPanel.setBorder(new EmptyBorder(new Insets(10, 10, 0, 10)));
		editPanel.setPreferredSize(new Dimension(500, 150));
		
		// settings for label, add label to panel
		for (GeneralTRS trs: selectedTrssEditOrErase)
			labelEdit.setText("Editing trs '" + trs.name + "': What do you want to edit?");
		labelEdit.setBorder(new EmptyBorder(new Insets(0,0,5,0)));
		editPanel.add(labelEdit);
		
		// prepare checkBoxes and add to panel
		checkboxEdit[0].setText("name");
		checkboxEdit[1].setText("variables");
		checkboxEdit[2].setText("functions");
		checkboxEdit[3].setText("rules");
		checkboxEdit[4].setText("assumptions");
		
		for (int i=0; i<checkboxEdit.length;i++) {
			editPanel.add(checkboxEdit[i]);
			for (String s: selectedFeatures) {
				if (s.equals(checkboxEdit[i].getText())) {
					checkboxEdit[i].setSelected(true);
				}
			}
			checkboxEdit[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: checkboxEdit) {
						if (evt.getItemSelectable() == checkbox) {
							for (String s: featuresString) {
								if (checkbox.getText() == s) {
									selectedFeatures.add(s);
									if (evt.getStateChange() == ItemEvent.DESELECTED)
										selectedFeatures.remove(s);
								}
							}
						}
					}
				}
			});
		}
	}
	
	/**
	 * This method creates a new panel that contains two buttons:
	 * - an edit button which takes the user to a couple of panels for
	 * editing selected features
	 * - a cancel button which takes the user back to the main editing panel
	 */
	private static void initTrsEditButtons() {
		// initialize components
		editButtons = new JPanel();
		buttonEdit = new JButton();
		buttonStopEdit = new JButton();
		
		// set preferences for panel
		editButtons.setLayout(new GridLayout(2,1));
		editButtons.setBorder(new EmptyBorder(new Insets(90, 350, 10, 10)));
		editButtons.setPreferredSize(new Dimension(500, 150));
		
		// settings for 'edit' button
		buttonEdit.setText("Edit");
		buttonEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user selected any features to edit
				if (selectedFeatures.isEmpty()) {
					JOptionPane.showMessageDialog (editButtons, "You did not select any features!");
					return;
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
		});
		editButtons.add(buttonEdit);
		
		// settings for 'cancel' button
		createStopEditingButton(buttonStopEdit);
		editButtons.add(buttonStopEdit);
	}
}
