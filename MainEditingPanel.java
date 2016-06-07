package trs;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to start editing the set of trss.
 * The user can choose to edit an existing trs, to build a new trs, to erase a trs,
 * or to look at the details of a trs.
 * 
 * @author Marieke Peeters
 * 
 */
public class MainEditingPanel extends GUI {

	private static JPanel buildTRScheckboxes, buildNewTRS, eraseEditView, continuePanel;
	private static JScrollPane trsCheck;
	private static JCheckBox[] jCheckBoxesbuildTRSPanelStart;
	private static JButton buildButton, editButton, eraseButton, viewButton, jButtonContinue;
	
	/**
	 * This method changes the frame into a TRS-editing frame from where you can start
	 * building and erasing TRS's. You can also look at the details of existing TRS's.
	 */
	public static void goToMainEditingPanel(ActionEvent evt) {
		// start with an empty set of selected trss
		selectedTrssEditOrErase.clear();
		
		// set the title of the frame
		gui.setTitle("Build and/or edit TRSs");
		
		// initialize all of the panels
		initEditCheckboxes();
		initNewTrs();
		initEraseEditButtons();
		initContinue();
		
		// remove old panels and add new panels to the frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500, 300));
		gui.getContentPane().setLayout(new GridLayout(2,2));
		gui.getContentPane().add(trsCheck, BorderLayout.CENTER);
		gui.getContentPane().add(buildNewTRS, BorderLayout.CENTER);
		gui.getContentPane().add(eraseEditView, BorderLayout.CENTER);
		gui.getContentPane().add(continuePanel, BorderLayout.CENTER);
		
		gui.pack();
	}
	
	/**
	 * This method creates a panel that contains checkboxes for each trs.
	 * When a checkbox is checked, the trs is added to the set of
	 * selected trs's to edit or erase.
	 */
	private static void initEditCheckboxes() {
		// initialize components
		buildTRScheckboxes = new JPanel();
		trsCheck = new JScrollPane(buildTRScheckboxes);
		jCheckBoxesbuildTRSPanelStart = new JCheckBox[trsset.size()];
		for (int i=0; i<jCheckBoxesbuildTRSPanelStart.length;i++)
			jCheckBoxesbuildTRSPanelStart[i] = new JCheckBox();
		
		// settings of panel
		buildTRScheckboxes.setLayout(new GridLayout(jCheckBoxesbuildTRSPanelStart.length/2,3));
		buildTRScheckboxes.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		buildTRScheckboxes.setPreferredSize(new Dimension(225, 125));
		
		// edit checkboxes, make sure they respond to users actions, add them to panel
		int i=0;
		for (GeneralTRS trs: trsset) {
			jCheckBoxesbuildTRSPanelStart[i].setText(trs.name);
			jCheckBoxesbuildTRSPanelStart[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: jCheckBoxesbuildTRSPanelStart) {
						if (evt.getItemSelectable() == checkbox) {
							for (GeneralTRS trs: trsset) {
								if (checkbox.getText() == trs.name) {
									selectedTrssEditOrErase.add(trs);
									if (evt.getStateChange() == ItemEvent.DESELECTED)
										selectedTrssEditOrErase.remove(trs);
								}
							}
						}
					}
					
				}
			});
			buildTRScheckboxes.add(jCheckBoxesbuildTRSPanelStart[i]);
			i++;
		}
	}
	
	/**
	 * The method initNewTrs creates a panel containing a button that
	 * leads the way to the trs-building panels.
	 */
	private static void initNewTrs() {
		// initialize components
		buildNewTRS = new JPanel();
		buildButton = new JButton();
		
		// settings of panel
		buildNewTRS.setBorder(new EmptyBorder(new Insets(10, 110, 100, 10)));
		buildNewTRS.setPreferredSize(new Dimension(250, 150));
		
		// settings button and add it to panel
		buildButton.setText("Build new TRS");
		buildButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				name = null;
				variables.clear();
				functions.clear();
				rules.clear();
				assumptions.clear();
				BuildTrsNamePanel.goToBuildTRSName(evt);
			}
		});
		buildNewTRS.add(buildButton);
	}
	
	/**
	 * This method creates a panel containing three buttons:
	 * one to erase selected trss, one to edit a selected trs, 
	 * one to look at the details of a trs.
	 * It uses the collection of selected trss for editing or erasing.
	 */
	private static void initEraseEditButtons() {
		// initialize components
		eraseEditView = new JPanel();
		editButton = new JButton();
		eraseButton = new JButton();
		viewButton = new JButton();
		
		// preferences panel
		eraseEditView.setLayout(new BoxLayout(eraseEditView, BoxLayout.X_AXIS));
		eraseEditView.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));
		eraseEditView.setPreferredSize(new Dimension(250, 150));
		
		// settings 'erase' button, add button
		eraseButton.setText("Erase");
		eraseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user selected a trs to erase
				if (selectedTrssEditOrErase.size() < 1) {
					JOptionPane.showMessageDialog (eraseEditView, "You need to select the trs you want to erase!");
					return;
				}
				erase(evt);
			}
		});
		eraseEditView.add(eraseButton);
		
		// settings 'edit' button, add button
		editButton.setText("Edit");
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user did not select 2 or more trss to edit
				if (selectedTrssEditOrErase.size() > 1) {
					JOptionPane.showMessageDialog (eraseEditView, "You can only edit one trs at a time! Make sure you uncheck all other trss.");
					return;
				}
				// check if user selected a trs to edit
				if (selectedTrssEditOrErase.size() < 1) {
					JOptionPane.showMessageDialog (eraseEditView, "You did not select a trs! Check one of the boxes to select a trs for editing.");
					return;
				}
				EditTrsSelectPanel.editTrs(evt);
			}
		});
		eraseEditView.add(editButton);
		
		// settings 'view' button, add button
		viewButton.setText("View");
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user did not select 2 or more trss to edit
				if (selectedTrssEditOrErase.size() > 1) {
					JOptionPane.showMessageDialog (eraseEditView, "You can only look at one trs at a time! Make sure you uncheck all other trss.");
					return;
				}
				// check if user selected a trs to edit
				if (selectedTrssEditOrErase.size() < 1) {
					JOptionPane.showMessageDialog (eraseEditView, "You did not select a trs!");
					return;
				}
				for (GeneralTRS trs: selectedTrssEditOrErase)
					new ViewTrsPanel(trs);
				selectedTrssEditOrErase.clear();
				MainEditingPanel.goToMainEditingPanel(evt);
			}
		});
		eraseEditView.add(viewButton);
	}
	
	/**
	 * This method creates a panel containing a 'continue' button.
	 * It takes the user back to the MainPanel.
	 */
	private static void initContinue() {
		// initialize components
		continuePanel = new JPanel();
		jButtonContinue = new JButton();
		
		// preferences panel
		continuePanel.setBorder(new EmptyBorder(new Insets(100, 100, 10, 10)));
		continuePanel.setPreferredSize(new Dimension(250, 150));
		
		// button settings and add button to panel
		jButtonContinue.setText("Back to main panel");
		jButtonContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainPanel.mainPanel(evt);
			}
		});
		continuePanel.add(jButtonContinue);
	}
	
	/**
	 * This method erases trss from the set of usable trss
	 * and adjusts the panel accordingly.
	 */
	private static void erase (ActionEvent evt){
		for (GeneralTRS trs: selectedTrssEditOrErase)
			trsset.remove(trs);
		goToMainEditingPanel(evt);
	}
}
