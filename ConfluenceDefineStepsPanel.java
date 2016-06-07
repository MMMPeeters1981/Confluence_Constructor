package trs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.util.*;
/**
 * 
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame that gives the user the 
 * opportunity to select the diverging steps of the
 * reduction diagram.
 * 
 * @author Marieke Peeters
 *
 */
public class ConfluenceDefineStepsPanel extends GUI {
	private static JPanel labelPanel, stepCheck, buttonPanel;
	private static JScrollPane checkScroll;
	private static JLabel label;
	private static JCheckBox[] checkboxes;
	private static JButton continueButt;
	private static Term t = null;
	
	/**
	 * This panel is used to give the user the opportunity to select the
	 * exact steps of the wanted divergence.
	 */
	public static void goToConfluenceDefineStepsPanel(ActionEvent evt) {
		// set the title of the frame
		gui.setTitle("Define Divergence");
		
		// initialize all of the panels
		initLabel();
		initCheck(evt);
		initButtons();


		// remove old panels and add new panels to the frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(1000,500));
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().add(labelPanel, BorderLayout.WEST);
		gui.getContentPane().add(checkScroll, BorderLayout.WEST);
		gui.getContentPane().add(buttonPanel, BorderLayout.WEST);
		
		gui.pack();
	}
	
	/**
	 * 
	 */
	private static void initLabel() {
		// initialize components
		labelPanel = new JPanel();
		label = new JLabel();
		
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		labelPanel.setPreferredSize(new Dimension(500, 100));
		
		int i=0;
		String direction;
		if (stepsLeft>0) {
			i = lTotal - stepsLeft+1;
			direction = "left";
		} else {
			i = rTotal - stepsRight+1;
			direction = "right";
		}
		label.setText("Select step " + i + " of reductionrow on the " + direction + " side of the reductiondiagram.");
		labelPanel.add(label);
	}
	
	/**
	 * This is a scrollable panel that contains all the possible steps
	 * to take upon the term at this point in the reduction. Every step is
	 * put in a checkbox, so that it can be selected as the next step in the divergence.
	 */
	private static void initCheck(ActionEvent evt) {
		// initialize components
		stepCheck = new JPanel();
		checkScroll = new JScrollPane(stepCheck);
		// set preferences for the panel
		stepCheck.setLayout(new BoxLayout(stepCheck, BoxLayout.Y_AXIS));
		stepCheck.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		int size;
		if (stepsLeft == lTotal || (stepsLeft==0 && stepsRight==rTotal))
			t = term;
		else {
			if (rTotal==stepsRight)
				t = redRowL.get(lTotal - stepsLeft - 1).resultTerm;
			else
				t = redRowR.get(rTotal - stepsRight - 1).resultTerm;
		}
		// find all possible contractions of the term
		if (selectedTrssConfluence.size()==1) {
			TermReduction.reductionArrayInitialize(t,1,true,newTRS);
			size = t.possibleContractions.size();
			rss = new Reductionstep[size];
			int i=0;
			for (Reductionstep rs: t.possibleContractions) {
				rss[i] = new Reductionstep(rs);
				i++;
			}
		} else {
			int rank = CrankRterm.getRank(t,newTRS);
			Term[] tallAliens = CrankRterm.getAliens(rank,t,newTRS);
			Substitution decomposition = Substitution.getDecomposition(t,tallAliens);
			Term base = CrankRterm.getBase(decomposition,t);
			Set<Reductionstep> tallSteps = CrankRterm.getTallSteps(newTRS,rank,t);
			Set<Reductionstep> shortSteps = CrankRterm.getShortSteps(base,newTRS,decomposition);
			size = shortSteps.size() + tallSteps.size();
			rss = new Reductionstep[size];
			int i=0;
			for (Reductionstep rs: shortSteps) {
				rss[i] = new Reductionstep(rs);
				i++;
			}
			for (Reductionstep rs: tallSteps) {
				rss[i] = new Reductionstep(rs);
				i++;
			}
		}
		// settings of the checkboxes and add to panel
		checkboxes = new JCheckBox[size];
		for (int j=0;j<checkboxes.length;j++) {
			checkboxes[j] = new JCheckBox();
			checkboxes[j].setText(rss[j].reductionToString());
			checkboxes[j].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: checkboxes) {
						if (evt.getItemSelectable() == checkbox){
							if (evt.getStateChange() == ItemEvent.DESELECTED) {
								for (Reductionstep rs: rss)
									if (rs.reductionToString().equals(checkbox.getText())) {
										if (stepsLeft>0) {
											redRowL.remove(rs);
										}
										else if (stepsRight>0) {
											redRowR.remove(rs);
										}
										break;
									}
							} else {
								for (Reductionstep rs: rss)
									if (rs.reductionToString().equals(checkbox.getText())) {
										if (stepsLeft>0)
											redRowL.add(rs);
										else if (stepsRight>0)
											redRowR.add(rs);
										break;
									}
							}
						}
					}
				}
			});
			stepCheck.add(checkboxes[j]);
		}
	}
	
	/**
	 * This method creates a panel that contains a button
	 * to continue to the definition of the next step.
	 */
	private static void initButtons() {
		// initialize components
		buttonPanel = new JPanel();
		continueButt = new JButton();
		
		// edit button and add to panel
		continueButt.setText("Continue");
		continueButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int i=0;
				for (JCheckBox cb: checkboxes)
					if (cb.isSelected())
						i++;
				if (i!=1) {
					JOptionPane.showMessageDialog(gui,"You must select exactly 1 step!");
					return;
				}
				if (stepsLeft > 0)
					stepsLeft--;
				else 
					stepsRight--;
				if (stepsLeft>0 || stepsRight>0)
					goToConfluenceDefineStepsPanel(evt);
				else
					ConfluencePanel.goToConfluencePanel(evt);
			}
		});
		buttonPanel.add(continueButt);
	}
}