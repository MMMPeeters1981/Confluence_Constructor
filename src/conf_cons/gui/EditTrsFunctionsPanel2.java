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
 */
public class EditTrsFunctionsPanel2 extends GUI {
	
	private static JPanel functionPanel, labels, textPanel, buttons;
	private static JLabel expLabel, newNameLabel, newArityLabel;
	private static JTextField inputNewFunctName, inputNewFunctArity;
	private static JButton continueButt, stopEditing, backButt;	
	private static String function = null;

	/**
	 * This method changes the panel into a panel where the user can
	 * edit the selected functions.
	 */
	public static void editTrsFunctions2(ActionEvent evt) {
		// select a function to modify
		if (function == null)
			for (String s: selectedFunctions)
				function = s;
		
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Change function " + function + " of trs '" + trs.name +"'");
		
		// initialize panels
		initHead();
		initnewNameArity();
		initButt();
		
		// remove old panels from, add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().add(functionPanel);
		gui.getContentPane().add(buttons);
		
		gui.pack();	
	}
	
	/**
	 * This method creates a panel containing a label
	 * that says which function is to be changed
	 */
	private static void initHead() {
		// initialize components
		functionPanel = new JPanel();
		expLabel = new JLabel();
		
		functionPanel.setLayout(new GridLayout(3,1));
		functionPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		functionPanel.setPreferredSize(new Dimension(500, 150));
		
		// settings label, add label to panel
		expLabel.setText("Modify function '" + function + "': ");
		functionPanel.add(expLabel);
	}
	
	/**
	 * This method creates a panel containing the textfields
	 * to change the name and/or arity of the function
	 */
	private static void initnewNameArity() {
		// initialize components
		textPanel = new JPanel();
		labels = new JPanel();
		newNameLabel = new JLabel();
		newArityLabel = new JLabel();
		inputNewFunctName = new JTextField();
		inputNewFunctArity = new JTextField();
		
		// preferences panel
		labels.setLayout(new GridLayout(1,2));
		labels.setPreferredSize(new Dimension(500,50));
		
		newNameLabel.setText("New name");
		labels.add(newNameLabel);
		
		newArityLabel.setText("New arity");
		labels.add(newArityLabel);
		
		functionPanel.add(labels);
		
		// preferences panel
		textPanel.setLayout(new GridLayout(1,2));
		textPanel.setBorder(new EmptyBorder(new Insets(0,0,20,0)));
		textPanel.setPreferredSize(new Dimension(500,50));
		
		inputNewFunctName.setText("Enter new name");
		textPanel.add(inputNewFunctName);
		
		inputNewFunctArity.setText("Enter new arity");
		textPanel.add(inputNewFunctArity);
		
		functionPanel.add(textPanel);
	}
	
	/**
	 * This method creates a panel containing three buttons:
	 * 	- one button to continue editing
	 * 	- one button to stop editing
	 *  - one button to go back to the panel where the features
	 *    of the trs that are to be edited can be selected
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
		if (selectedFunctions.size()>1)
			continueButt.setText("Change function and continue editing functions");
		else
			continueButt.setText("Change function and continue editing trs");
		
		continueButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean checkDigit = true;
				// check if user entered a new name and an arity
				// if not: ask if that is correct
				if (inputNewFunctName.getText().length()<1 || inputNewFunctName.getText().equals("Enter new name")) {
					String[] message = new String[2];
					message[0] = ("You did not enter a new name!");
					if (selectedFunctions.size()>1)
						message[1] = ("Are you sure you want to continue editing other functions of the trs?");
					else
						message[1] = ("Are you sure you want to continue editing other features of the trs?");
					int reply = JOptionPane.showConfirmDialog(continueButt, message, "Continue without changing name of function?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply != JOptionPane.YES_OPTION)
				    	return;
				} else {
					if (inputNewFunctName.getText().length()>1) {
						JOptionPane.showMessageDialog (continueButt, "Functionsymbol must be one symbol!");
						return;
					}
					for (GeneralTRS trs: selectedTrssEditOrErase) {
						for (Variable v: trs.variables)
							if (v.name.equals(inputNewFunctName.getText())) {
								JOptionPane.showMessageDialog (buttons, "This name is already taken!");
								return;
							}
						for (Function f: trs.functions)
							if(f.functionsymbol.equals(inputNewFunctName.getText())) {
								JOptionPane.showMessageDialog (buttons, "This name is already taken!");
								return;
							}
					}
					for (GeneralTRS trs: selectedTrssEditOrErase) {
						for (Function f: trs.functions) {
							if (f.functionsymbol.equals(function))
								f.functionsymbol = inputNewFunctName.getText();
						}
					}
				}
					
				if (inputNewFunctArity.getText().length()<1 || inputNewFunctArity.getText().equals("Enter new arity")) {
					String[] message = new String[2];
					message[0] = ("You did not enter a new arity!");
					if (selectedFunctions.size()>1)
						message[1] = ("Are you sure you want to continue editing other functions of the trs?");
					else
						message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(continueButt, message, "Continue without changing arity of function?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply == JOptionPane.YES_OPTION) {
				    	checkDigit = false;
				    // if user did not intend that, return.
				    } else
				    	return;
				// if user did enter a name, edit the name and continue
				} else if (checkDigit) {
					int arity;
					boolean arityint = true;
					try {
					    arity = Integer.parseInt(inputNewFunctArity.getText());
					}
					catch(NumberFormatException nFE) {
					    arityint = false;
					    JOptionPane.showMessageDialog (continueButt, "Arity must be a digit!");
					    return;
					}
					String message = new String("Are you sure you want to change the arity? You need to change the rules accordingly!");
					int reply = JOptionPane.showConfirmDialog(continueButt, message, "Continue changing arity of function?", JOptionPane.YES_NO_OPTION);
				    // if user intended to continue editing other features, continue
				    if (reply != JOptionPane.YES_OPTION)
				    	return;
					if (arityint) {
						for (GeneralTRS trs: selectedTrssEditOrErase) {
							for (Function f: trs.functions) {
								if (f.functionsymbol.equals(function))
									f.arity = arity;
							}
						}
					}
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
	 * This method changes the panel into the appropriate new
	 * panel according to the features that are still to be edited
	 * according to the users selections.
	 */
	private static void continueEditing(ActionEvent evt) {
		selectedFunctions.remove(function);
		function = null;
    	if (selectedFunctions.isEmpty()) {
    		continueEditing2(evt);
    	} else {
    		editTrsFunctions2(evt);
    	}
	}
}