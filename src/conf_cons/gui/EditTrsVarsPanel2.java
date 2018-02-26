package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to edit the selected variables
 * of the selected trs.
 * 
 * @author Marieke Peeters
 */
public class EditTrsVarsPanel2 extends GUI {
	
	private static JPanel newName, buttons;
	private static JLabel expLabel;
	private static JTextField inputNewVarName;
	private static JButton continueButt, stopEditing, backButt;	
	private static String variable = null;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * edit the selected variables.
	 */
	public static void editTrsVariables2(ActionEvent evt) {
		// select a variable to modify
		if (variable == null)
			for (String s: selectedVariables)
				variable = s;
		
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Change variable " + variable + " of trs '" + trs.name +"'");
		
		// initialize panels
		initnewName();
		initButt();
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(newName, BorderLayout.WEST);
		gui.getContentPane().add(buttons, BorderLayout.WEST);
		
		gui.pack();	
	}
	
	/**
	 * This method creates a panel containing a label and a textfield to
	 * enter the new name of the variable.
	 */
	private static void initnewName() {
		// initialize components
		newName = new JPanel();
		expLabel = new JLabel();
		inputNewVarName = new JTextField();
		
		// preferences panel
		newName.setLayout(new GridLayout(2,1));
		newName.setBorder(new EmptyBorder(new Insets(10, 10, 90, 250)));
		newName.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		expLabel.setText("Modify name of variable " + variable + ": ");
		newName.add(expLabel);

		inputNewVarName.setText("Enter new name");
		newName.add(inputNewVarName);
	}
	
	/**
	 * This method creates a panel containing three buttons:
	 * 	- one button to change the name of the variable and continue editing
	 *  - one button to go back to the panel where the features to be edited can be selected
	 * 	- one button to stop editing
	 */
	private static void initButt() {
		// initialize components
		buttons = new JPanel();
		continueButt = new JButton();
		backButt = new JButton();
		stopEditing = new JButton();
		
		// preferences panel
		buttons.setLayout(new GridLayout(3,1));
		buttons.setBorder(new EmptyBorder(new Insets(70, 150, 10, 10)));
		buttons.setPreferredSize(new Dimension(500, 150));
		
		// create 'continue' button
		if (selectedVariables.size()>1)
			continueButt.setText("Change name and continue editing variables");
		else
			continueButt.setText("Change name and continue editing trs");
		continueButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user entered a new name
				// if not: ask if that is correct
				if (inputNewVarName.getText().length()<1 || inputNewVarName.getText().equals("Enter new name")) {
					String[] message = new String[2];
					message[0] = ("You did not enter a new name!");
					if (selectedVariables.size()>1)
						message[1] = ("Are you sure you want to continue editing other variables of the trs?");
					else
						message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(continueButt, message, "Continue without editing name of variable?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply == JOptionPane.YES_OPTION) {
				    	continueEditing(evt);
				    // if user did not intend that, return.
				    } else
				    	return;
				// if user did enter a name, edit the name and continue
				} else {
					if (variable.length()>1) {
						JOptionPane.showMessageDialog (buttons, "You must enter exactly one symbol!");
						return;
					}
					for (GeneralTRS trs: selectedTrssEditOrErase) {
						for (Variable v: trs.variables)
							if (v.name.equals(inputNewVarName.getText())) {
								JOptionPane.showMessageDialog (buttons, "This name is already taken!");
								return;
							}
						for (Function f: trs.functions)
							if(f.functionsymbol.equals(inputNewVarName.getText())) {
								JOptionPane.showMessageDialog (buttons, "This name is already taken!");
								return;
							}
					}
					for (GeneralTRS trs: selectedTrssEditOrErase) {
						for (Variable v: trs.variables)
							if (v.name.equals(variable))
								v.name = inputNewVarName.getText();
					}
					continueEditing(evt);
				}
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
	 * This method redirects the panel to the appropriate 
	 * follow-up panel.
	 * @param evt Some ActionEvent like clicking a button
	 */
	private static void continueEditing(ActionEvent evt) {
		selectedVariables.remove(variable);
		variable = null;
    	if (selectedVariables.isEmpty()) {
    		continueEditing2(evt);
    	} else {
    		editTrsVariables2(evt);
    	}
	}
}
