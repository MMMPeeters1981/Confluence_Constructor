package trs;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;

/**
 * This class is a subclass of GUI. This frame contains a textfield 
 * in which the user can enter a symbol to represent a variable, 
 * that will be added as a variable to the new trs he or she is building.
 * After adding a variable, it will be displayed next to a checkbox
 * The user can decide to remove it, by checking the checkbox.
 * 
 * @author Marieke Peeters
 * 
 */
public class BuildTrsVarsPanel extends GUI {
	
	private static JPanel textPan, buttPan, contPan, addPan, backPan, checkPan;
	private static JLabel knownVars, entVar;
	private static JCheckBox[] checkVars;
	private static JScrollPane checkScrollPan;
	private static JTextField varName;
	private static JButton addButt, contButt, backButt;
	private static String[] string;
	
	/**
	 * This method changes the panel into a panel where the user has to add
	 * the variables of the TRS that is built.
	 */
	public static void goToBuildTRSvariables(ActionEvent evt) {
		// set the title of the frame
		gui.setTitle("Create new TRS: enter variables");
		
		// initialize panels
		initTextfields();
		initCheckBoxes();
		initButtons();
		
		// remove old panels and add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().add(textPan);
		gui.getContentPane().add(checkScrollPan);
		gui.getContentPane().add(buttPan);
		
		gui.pack();
	}
	
	/**
	 * This method creates a new panel containing a label
	 * that tells the user to add a variable to the new TRS
	 * and a Textfield to type the new variable.
	 */
	private static void initTextfields() {
		//initialize components
		textPan = new JPanel();
		entVar = new JLabel();
		varName = new JTextField();
		
		textPan.setLayout(new GridLayout(2,1));
		textPan.setBorder(new EmptyBorder(new Insets(20, 10, 40, 250)));
		textPan.setPreferredSize(new Dimension(500, 100));
		
		entVar.setText("Enter variable (1 symbol)");
		textPan.add(entVar);
		textPan.add(varName);
	}
	
	/**
	 * This method creates a new panel containing a
	 * label that tells the user whether variables have
	 * been added and checkboxes for each variable.
	 * Variables can be removed by checking them and
	 * clicking remove&continue.
	 */
	private static void initCheckBoxes() {
		// initialize components
		checkPan = new JPanel();
		checkScrollPan = new JScrollPane(checkPan);
		knownVars = new JLabel();
		checkVars = new JCheckBox[variables.size()];
		
		checkPan.setLayout(new GridLayout(variables.size()+1, 1));
		checkPan.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
		
		if (variables.isEmpty())
			knownVars.setText("So far, you have not entered any variables.");
		else
			knownVars.setText("Entered variables, check to remove:");
		checkPan.add(knownVars);
		
		string = new String[variables.size()];
		int i=0;
		for (String s: variables) {
			string[i] = s;
			i++;
		}
		
		for (int j=0;j<string.length;j++) {
			checkVars[j] = new JCheckBox();
			checkVars[j].setText(string[j]);
			checkPan.add(checkVars[j]);
			checkVars[j].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: checkVars) {
						if (evt.getStateChange() == ItemEvent.DESELECTED)
							variables.add(checkbox.getText());
						else if (evt.getItemSelectable() == checkbox)
							variables.remove(checkbox.getText());
					}
				}
			});
		}
	}
	
	/**
	 * This method creates a new panel containing a button to 
	 * add a variable to the trs, a button to continue
	 * building the trs and a button to go back to the panel 
	 * where the trs is given a name.
	 */
	private static void initButtons() {
		//initialize components
		buttPan = new JPanel();
		addPan = new JPanel();
		contPan = new JPanel();
		backPan = new JPanel();
		addButt = new JButton();
		contButt = new JButton();
		backButt = new JButton();
		
		buttPan.setLayout(new GridLayout(3,1));
		buttPan.setBorder(new EmptyBorder(new Insets(5, 10, 10, 10)));
		buttPan.setPreferredSize(new Dimension(500, 100));
		
		addPan.setLayout(new GridLayout(1,1));
		addPan.setBorder(new EmptyBorder(new Insets(0, 375, 0, 10)));
		addButt.setText("Add");
		addButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addVariable(evt,varName.getText());
			}
		});
		addPan.add(addButt);
		buttPan.add(addPan);

		contPan.setLayout(new GridLayout(1,1));
		contPan.setBorder(new EmptyBorder(new Insets(0, 150, 0, 10)));
		contButt.setText("Continue (and remove checked variables)");
		contButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsFunctionsPanel.goToBuildTRSfunctions(evt);
			}
		});
		contPan.add(contButt);
		buttPan.add(contPan);

		backPan.setLayout(new GridLayout(1,1));
		backPan.setBorder(new EmptyBorder(new Insets(0, 240, 0, 10)));
		backButt.setText("Back to 'Enter name'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsNamePanel.goToBuildTRSName(evt);
			}
		});
		backPan.add(backButt);
		buttPan.add(backPan);
	}
	
	/**
	 * This method adds the new variable to the set that is sent 
	 * to GeneralTRS to create a new TRS and reloads this frame.
	 * @param variable The variable that is to be added.
	 */
	private static void addVariable (ActionEvent evt, String variable){
		boolean add = true;
		if (variable.length()<1 || variable.length()>1) {
			JOptionPane.showMessageDialog (buttPan, "You must enter exactly one symbol!");
			return;
		}
		for (String s: variables) {
			if (s.equals(variable)) {
				JOptionPane.showMessageDialog (buttPan, "You already entered that variable!");
				add = false;
				break;
			}
		}
		if (add) {
			variables.add(variable);
			BuildTrsVarsPanel.goToBuildTRSvariables(evt);
		}
	}
}