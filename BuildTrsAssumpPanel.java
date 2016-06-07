package trs;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * This class is a subclass of GUI. This frame contains checkboxes 
 * for the assumptions (WCR, SN, ORTHOGONAL, UN, WN), which the user 
 * can check to select certain assumptions that are true for the trs 
 * he or she is building.
 * After selecting the assumptions, the user is finished entering all 
 * elements of the new TRS and the TRS will be built and added to 
 * the set of known TRS's. All created sets of elements known in the GUI 
 * are erased.
 * 
 * @author Marieke Peeters
 * 
 */
public class BuildTrsAssumpPanel extends GUI {
	
	private static JPanel chooseAss,labPan,checkPan,buttPan, finishPan, backPan;
	private static JLabel emptyLabel, explAss;
	private static JCheckBox[] checkAssump;
	private static JButton finishButt, backButt;
	
	/**
	 * This method changes the panel into a panel where the user can
	 * enter the assumptions that are valid for this TRS.
	 */
	public static void goToBuildTRSassumptions (ActionEvent evt) {
		// set title of the frame
		gui.setTitle("Create new TRS: select assumptions");
		
		// initialize panels
		initAssumpt();
		initAsscont();
		
		// remove old panels and add new panels to frame
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500,300));
		gui.getContentPane().add(chooseAss);
		emptyLabel = new JLabel();
		gui.getContentPane().add(emptyLabel);
		gui.getContentPane().add(buttPan);
		
		gui.pack();
		
	}
	
	/**
	 * This panel contains a label that explains to the user
	 * he can define assumptions for the trs. Also the panel
	 * contains checkboxes with the different assumptions.
	 */
	private static void initAssumpt() {
		// initialize components
		chooseAss = new JPanel();
		labPan = new JPanel();
		checkPan = new JPanel();
		explAss = new JLabel();
		checkAssump = new JCheckBox[2];
		for (int i=0; i<checkAssump.length;i++)
			checkAssump[i] = new JCheckBox();
		
		labPan.setLayout(new GridLayout(1,1));
		labPan.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		labPan.setPreferredSize(new Dimension(500, 50));
		
		checkPan.setLayout(new GridLayout(3,2));
		checkPan.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		checkPan.setPreferredSize(new Dimension(500, 50));
		
		explAss.setText("Choose the assumptions that are valid in your TRS:");
		labPan.add(explAss);
		
		checkAssump[0].setText("WCR&SN");
		checkAssump[1].setText("Orthogonal");
		for (int i=0;i<checkAssump.length;i++) {
			for (String s: assumptions) {
				if (s.equals(checkAssump[i].getText())) {
					checkAssump[i].setSelected(true);
				}
			}
			checkPan.add(checkAssump[i]);
			checkAssump[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: checkAssump) {
						if (evt.getStateChange() == ItemEvent.DESELECTED)
							assumptions.remove(checkbox.getText());
						else if (evt.getItemSelectable() == checkbox)
							assumptions.add(checkbox.getText());
					}
				}
			});
		}
		// set preferences panel
		chooseAss.setLayout(new GridLayout(2,1));
		chooseAss.setBorder(new EmptyBorder(new Insets(0, 10, 0, 0)));
		chooseAss.setPreferredSize(new Dimension(500, 100));
		chooseAss.add(labPan);
		chooseAss.add(checkPan);
	}
	
	/**
	 * This panel contains a button to finish building the trs.
	 */
	private static void initAsscont () {
		// initialize components
		buttPan = new JPanel();
		finishPan = new JPanel();
		backPan = new JPanel();
		finishButt = new JButton();
		backButt = new JButton();
		
		// set preferences panel
		buttPan.setLayout(new GridLayout(2,1));
		buttPan.setBorder(new EmptyBorder(new Insets(40, 10, 10, 10)));
		buttPan.setPreferredSize(new Dimension(500, 100));
		
		// settings of buttons and add buttons to panel
		finishPan.setLayout(new GridLayout(1,1));
		finishPan.setBorder(new EmptyBorder(new Insets(0, 295, 0, 10)));
		finishPan.setPreferredSize(new Dimension(500, 75));
		finishButt.setText("Finish and build TRS");
		finishButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addAssumptionsandTRS(evt);
			}
		});
		finishPan.add(finishButt);
		buttPan.add(finishPan);

		backPan.setLayout(new GridLayout(1,1));
		backPan.setBorder(new EmptyBorder(new Insets(0, 260, 0, 10)));
		backPan.setPreferredSize(new Dimension(500, 75));
		backButt.setText("Back to 'Enter Rules'-panel");
		backButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuildTrsRulesPanel.goToBuildTRSrules(evt);
			}
		});
		backPan.add(backButt);
		buttPan.add(backPan);
	}
	
	/**
	 * This method takes you back to the start and creates a new TRS from the
	 * entered information in the previous frames.
	 */
	private static void addAssumptionsandTRS (ActionEvent evt) {
		// create arrays from the HashSets
		String [] var = new String[variables.size()];
		int i = 0;
		for (String s: variables) {
			var[i] = new String(s);
			i++;
		}
		String[][] funct = new String[functions.size()][2];
		int j = 0;
		for (String[] s: functions) {
			funct[j] = s;
			j++;
		}
		functions.clear();
		String[][][] rul = new String[rules.size()][2][];
		int k = 0;
		for (String[][] s: rules) {
			rul[k] = s;
			k++;
		}
		String [] ass = new String[assumptions.size()];
		int l = 0;
		for (String s: assumptions) {
			ass[l] = s;
			l++;
		}
		// create TRS from these arrays
		try {
			GeneralTRS newtrs = new GeneralTRS(name,var,funct,rul,ass);
			GUI.trsset.add(newtrs);
		} catch (NullPointerException npe) {
			JOptionPane.showMessageDialog (finishButt, "Cannot create this trs! Check rules on amount of arguments corresponding to arity of functions.");
			return;
		}
		// clear all HashSets to start with a clean slate
		variables.clear();
		functions.clear();
		rules.clear();
		assumptions.clear();
		// go back to main editing panel
		MainEditingPanel.goToMainEditingPanel(evt);
	}

}