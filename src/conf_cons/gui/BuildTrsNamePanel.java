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
 * This class is a subclass of GUI. This frame contains a textfield 
 * in which the user can enter the name of the new trs he or she is building.
 * After adding the name, it will be displayed above the textfield (go back to this frame).
 * The user can decide to change it, by entering a different name.
 * 
 * @author Marieke Peeters
 * 
 */
public class BuildTrsNamePanel extends GUI {
	
	private static JPanel buildTRSname, buttCont, buttPan1, buttPan2;
	private static JLabel enterName, emptyLabel;
	private static JTextField givenName;
	private static JButton contButt, backButt;
	
	/**
	 * This method changes the panel into the first TRS-building panel.
	 * In this panel the user can enter the name of the TRS.
	 */
	public static void goToBuildTRSName (ActionEvent evt){
		// set the title of the frame
		gui.setTitle("Create new TRS: enter name");
		
		//initialize panels
		initTextPanel();
		initButtPanel();
		
		// remove old panels and add new panels to frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().add(buildTRSname);
		emptyLabel = new JLabel();
		gui.getContentPane().add(emptyLabel);
		gui.getContentPane().add(buttCont);
		
		gui.pack();
	}
	
	/**
	 * This method creates a panel containing a label
	 * that tells the user to give a name to the new TRS
	 * and a Textfield to type the new name.
	 */
	private static void initTextPanel() {
		// initialize components
		buildTRSname = new JPanel();
		enterName = new JLabel();
		givenName = new JTextField();
		
		buildTRSname.setLayout(new GridLayout(2,1));
		buildTRSname.setPreferredSize(new Dimension(500, 100));
		if (name == null) {
			buildTRSname.setBorder(new EmptyBorder(new Insets(20, 10, 40, 240)));
			enterName.setText("What is the name of this new TRS?");
		} else {
			buildTRSname.setBorder(new EmptyBorder(new Insets(20, 10, 40, 190)));
			enterName.setText("Current name is " + name + ". To change, enter new name:");
		}
		buildTRSname.add(enterName);
		
		buildTRSname.add(givenName);
	}
	
	/**
	 * This method creates a panel containing a continue button and
	 * a back button.
	 */
	private static void initButtPanel() {
		// initialize components
		buttCont = new JPanel();
		buttPan1 = new JPanel();
		buttPan2 = new JPanel();
		contButt = new JButton();
		backButt = new JButton();
		
		buttCont.setLayout(new GridLayout(2,1));
		buttCont.setBorder(new EmptyBorder(new Insets(40, 10, 10, 10)));
		buttCont.setPreferredSize(new Dimension(500, 100));
		
		buttPan1.setLayout(new GridLayout(1,1));
		buttPan1.setBorder(new EmptyBorder(new Insets(0, 395, 0, 10)));
		buttPan1.setPreferredSize(new Dimension(500, 75));
		
		contButt.setText("OK");
		contButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (givenName.getText().length()<1 && name==null) {
					JOptionPane.showMessageDialog (buildTRSname, "You must enter a name!");
					return;
				}
				if (givenName.getText().length()>0)
					name = givenName.getText();
				BuildTrsVarsPanel.goToBuildTRSvariables(evt);
			}
		});
		buttPan1.add(contButt);
		buttCont.add(buttPan1);
		
		buttPan2.setLayout(new GridLayout(1,1));
		buttPan2.setBorder(new EmptyBorder(new Insets(0, 300, 0, 10)));
		buttPan2.setPreferredSize(new Dimension(500, 75));
		
		backButt.setText("Back to editing Panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				name=null;
				variables.clear();
				functions.clear();
				rules.clear();
				assumptions.clear();
				MainEditingPanel.goToMainEditingPanel(evt);
			}
		});
		buttPan2.add(backButt);
		buttCont.add(buttPan2);
	}
}