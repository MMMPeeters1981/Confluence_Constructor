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
 * to take the user to a new view: a frame to edit the name of the
 * selected trs.
 * 
 * @author Marieke Peeters
 */
public class EditTrsNamePanel extends GUI {

	private static JPanel editName, editNameButton;
	private static JLabel giveName;
	private static JTextField newName;
	private static JButton editNameOk, stopEditName, backButt;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * edit the name of an already created trs.
	 */
	public static void editTrsName (ActionEvent evt){
		// set the title of the frame
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Edit name of trs '" + trs.name +"'");
		
		// initialize panels
		initName();
		initNameButton();
		
		// remove old panels and add new panels to frame
		gui.getContentPane().setLayout(new GridLayout(2,1));
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().add(editName);
		gui.getContentPane().add(editNameButton);
		
		gui.pack();
	}
	
	/**
	 * This method creates a new panel containing a label
	 * that tells the user to pick a new name.
	 */
	private static void initName() {
		// initialize components
		editName = new JPanel();
		giveName = new JLabel();
		newName = new JTextField();
		
		for (GeneralTRS trs: selectedTrssEditOrErase) 
			gui.setTitle("Edit name of trs " + trs.name);

		// preferences for panel
		editName.setLayout(new GridLayout(2,1));
		editName.setBorder(new EmptyBorder(new Insets(10, 10, 80, 250)));
		editName.setPreferredSize(new Dimension(500, 150));

		// settings for label, add label to panel
		giveName.setText("Enter the new name of the trs:");
		editName.add(giveName);
		
		newName.setText("new name");
		editName.add(newName);
	}
	
	
	/**
	 * This method creates a panel that
	 * contains three buttons:
	 * - 'ok' button, changes the name to the given name, takes user to next editing panel
	 * - 'cancel editing' button, takes user back to main editing panel
	 * - back button which takes the user back to the panel where he can select the 
	 * 	 features of the trs that are to be edited
	 */
	private static void initNameButton() {
		// initialize components
		editNameButton = new JPanel();
		editNameOk = new JButton();
		stopEditName = new JButton();
		backButt = new JButton();

		// preferences for panel
		editNameButton.setLayout(new GridLayout(3,1));
		editNameButton.setBorder(new EmptyBorder(new Insets(70, 210, 10, 10)));
		editNameButton.setPreferredSize(new Dimension(500, 150));
		
		// create 'ok' button and add to panel
		editNameOk.setText("Change name and continue editing trs");
		editNameOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectedFeatures.remove("name");
				// check if user selected any features to edit
				if (newName.getText().length()<1 || newName.getText().equals("new name")) {
					String[] message = new String[2];
					message[0] = ("You did not enter a name!");
					message[1] = ("Are you sure you want to continue editing other features of the trs?");
				    int reply = JOptionPane.showConfirmDialog(editNameOk, message, "Continue without changing name?", JOptionPane.YES_NO_OPTION);
				    if (reply == JOptionPane.YES_OPTION) 
				    	continueEditing(evt);
				    else
				    	return;
				} else {
					for (GeneralTRS trs: selectedTrssEditOrErase)
						trs.name = newName.getText();
					// continue to next panel, depending on other 
					// selected features to edit
					continueEditing(evt);
				}
			}
		});
		editNameButton.add(editNameOk);
		
		backButt.setText("Back to 'Select features to edit'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		editNameButton.add(backButt);

		// settings for 'cancel' button
		createStopEditingButton (stopEditName);
		editNameButton.add(stopEditName);
	}

	/**
	 * This method takes the user to a new view depending
	 * on the selected features that still need to be edited.
	 */
	private static void continueEditing (ActionEvent evt) {
		continueEditing2(evt);
	}
	
}
