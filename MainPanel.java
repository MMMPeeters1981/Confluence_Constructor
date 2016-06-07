package trs;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to start from.
 * This view gives the user the option to start the confluence
 * check on a term within a given (set of) trs(s), 
 * or to start editing the set of trss.
 * 
 * @author Marieke Peeters
 * 
 */
public class MainPanel extends GUI {
	
	private static JPanel checkboxesTRS, termConfluence, buttonEditTRS, buttonConfluence, termText, lrText, rightText, leftText, filler1, filler2;
	private static JScrollPane trsCheck;
	private static JLabel leftinfo, rightinfo, terminfo;
	private static JTextField enterTerm, enterStepsLeft, enterStepsRight;
	private static JCheckBox[] jCheckBoxesConfluence;
	private static JButton goEditButton, viewButton, goConfluenceButton;
	
	/**
	 * This method changes the panel into a panel from where you can choose to
	 * - edit the set of trss
	 * or
	 * - check confluence
	 */
	public static void mainPanel (ActionEvent evt) {
		// start with an empty set of selected trss
		selectedTrssConfluence.clear();
		redRowL.clear();
		redRowR.clear();

		// set the title of the frame
		gui.setTitle("Confluence Constructor: Main Panel");

		// initialize all of the panels
		initCheckboxes();
		initButtonEdit();
		initButtonConfluence();
		initTermConfluence();

		// remove old panels and add new panels to the frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(500, 300));
		gui.getContentPane().setLayout(new GridLayout(2,2));
		gui.getContentPane().add(trsCheck);
		gui.getContentPane().add(termConfluence);
		gui.getContentPane().add(buttonEditTRS);
		gui.getContentPane().add(buttonConfluence);
		gui.pack();
	}
	
	/**
	 * This method creates a panel that contains checkboxes for each trs.
	 * When a checkbox is checked, the trs is added to the set of
	 * selected trs's for the confluence check.
	 */
	private static void initCheckboxes(){
		// initialize components
		checkboxesTRS = new JPanel();
		trsCheck = new JScrollPane(checkboxesTRS);
		jCheckBoxesConfluence = new JCheckBox[trsset.size()];
		for (int i=0; i<jCheckBoxesConfluence.length;i++)
			jCheckBoxesConfluence[i] = new JCheckBox();

		// settings of panel
		checkboxesTRS.setLayout(new GridLayout(jCheckBoxesConfluence.length/2,3));

		// edit checkboxes, make sure they respond to users actions, add them to panel
		int i=0;
		for (GeneralTRS trs: trsset) {
			jCheckBoxesConfluence[i].setText(trs.name);
			checkboxesTRS.add(jCheckBoxesConfluence[i]);
			jCheckBoxesConfluence[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					for (JCheckBox checkbox: jCheckBoxesConfluence) {
						if (evt.getItemSelectable() == checkbox) {
							for (GeneralTRS trs: trsset) {
								if (checkbox.getText() == trs.name) {
									selectedTrssConfluence.add(trs);
									if (evt.getStateChange() == ItemEvent.DESELECTED)
										selectedTrssConfluence.remove(trs);
								}
							}
						}
					}
					
				}
			});
			i++;
		}
	}
	
	/**
	 * This method creates a panel that contains an edit button.
	 * It takes the user to the main editing panel.
	 */
	private static void initButtonEdit() {
		// initialize components
		buttonEditTRS = new JPanel();
		goEditButton = new JButton();
		filler1 = new JPanel();
		filler2 = new JPanel();
		viewButton = new JButton();

		// settings of panel
		buttonEditTRS.setLayout(new GridLayout(2,1));
		buttonEditTRS.setPreferredSize(new Dimension(250, 150));
		
		// settings of panel
		filler1.setLayout(new GridLayout(1,1));
		filler1.setBorder(new EmptyBorder(new Insets(10, 10, 40, 160)));
		filler1.setPreferredSize(new Dimension(250, 75));
		
		// settings 'view' button, add button
		viewButton.setText("View");
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user did not select 2 or more trss to edit
				if (selectedTrssConfluence.size() > 1) {
					JOptionPane.showMessageDialog (buttonEditTRS, "You can only look at one trs at a time! Make sure you uncheck all other trss.");
					return;
				}
				// check if user selected a trs to edit
				if (selectedTrssConfluence.size() < 1) {
					JOptionPane.showMessageDialog (buttonEditTRS, "You did not select a trs!");
					return;
				}
				for (GeneralTRS trs: selectedTrssConfluence)
					new ViewTrsPanel(trs);
				selectedTrssEditOrErase.clear();
				MainPanel.mainPanel(evt);
			}
		});
		filler1.add(viewButton);
		
		// settings of panel
		filler2.setLayout(new GridLayout(1,1));
		filler2.setBorder(new EmptyBorder(new Insets(40, 10, 10, 100)));
		filler2.setPreferredSize(new Dimension(250, 75));
		
		// settings button and add it to panel
		goEditButton.setText("Build/edit TRS's");
		goEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainEditingPanel.goToMainEditingPanel(evt);
			}
		});
		filler2.add(goEditButton);

		buttonEditTRS.add(filler1);
		buttonEditTRS.add(filler2);
	}
	
	/**
	 * This method creates a panel that contains a 'confluence'
	 * button. It takes the user to the Confluence panel, that shows
	 * the user the confluence proof for the given term in the chosen trss.
	 */
	private static void initButtonConfluence() {
		// initialize components
		buttonConfluence = new JPanel();
		goConfluenceButton = new JButton();

		// settings of panel
		buttonConfluence.setBorder(new EmptyBorder(new Insets(10, 60, 10, 60)));
		buttonConfluence.setPreferredSize(new Dimension(250, 150));

		// settings button and add it to panel
		goConfluenceButton.setText("Confluence");
		goConfluenceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// check if user selected a trs
				if (selectedTrssConfluence.isEmpty() || selectedTrssConfluence.size()>2) {
					JOptionPane.showMessageDialog (buttonConfluence, "You must select one or two trss to perform confluence check!");
					return;
				}
				String s = enterTerm.getText();
				// check if user entered a term.
				if (s.equals("Enter term")) {
					JOptionPane.showMessageDialog (buttonConfluence, "You must enter a term to perform confluence check!");
					return;
				}
				String newtrs = ("newtrs");
				GeneralTRS[] trss = (GeneralTRS[]) selectedTrssConfluence.toArray(new GeneralTRS[selectedTrssConfluence.size()]);
				newTRS = new GeneralTRS (newtrs, trss);
				newTRS.resetPointer();
				term = newTRS.createTerm(stringtoArray(s));
				// check if term is a legal term within the trss
				if (term==null||term.termToString2()==null || !(term.termToString2().equals(s))) {
					JOptionPane.showMessageDialog (buttonConfluence, "This is not a term in the combined trs!");
					return;
				}
				
				if (enterStepsLeft.getText().equals("nr. of steps left") || 
						enterStepsRight.getText().equals("nr. of steps right") ) {
					JOptionPane.showMessageDialog (buttonConfluence, "You must enter a number of steps to the left and to the right!");
					return;
				}
				try {
				    stepsLeft = Integer.parseInt(enterStepsLeft.getText());
				    lTotal = Integer.parseInt(enterStepsLeft.getText());
				    stepsRight = Integer.parseInt(enterStepsRight.getText());
				    rTotal = Integer.parseInt(enterStepsRight.getText());
				} catch (NumberFormatException nFE) {
				    JOptionPane.showMessageDialog (buttonConfluence, "The number of steps must be a number!");
				    return;
				}
				if((stepsLeft!=0 && stepsRight!=0))
					ConfluenceDefineStepsPanel.goToConfluenceDefineStepsPanel(evt);
				else {
					JOptionPane.showMessageDialog (buttonConfluence, "The number of steps must be higher than zero!");
					return;
				}
			}
		});
		buttonConfluence.add(goConfluenceButton);
	}
	
	/**
	 * This method creates a panel that contains a textfield.
	 * The user can enter a term into the textfield for which the 
	 * user wants to see a confluence proof within the chosen trss.
	 */
	private static void initTermConfluence() {
		// initialize components
		termConfluence = new JPanel();
		termText = new JPanel();
		lrText = new JPanel();
		leftText = new JPanel();
		rightText = new JPanel();
		terminfo = new JLabel();
		leftinfo = new JLabel();
		rightinfo = new JLabel();
		enterTerm = new JTextField();
		enterStepsLeft = new JTextField();
		enterStepsRight = new JTextField();

		// settings of panel
		termConfluence.setLayout(new GridLayout(2,1));
		termConfluence.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		termConfluence.setPreferredSize(new Dimension(250, 150));
		
		termText.setLayout(new GridLayout(2,1));
		termText.setBorder(new EmptyBorder(new Insets(10, 40, 10, 40)));
		leftText.setLayout(new GridLayout(2,1));
		rightText.setLayout(new GridLayout(2,1));
		lrText.setLayout(new GridLayout(1,2));
        
		// edit textfield and add it to the panel
		terminfo.setText("Enter term");
		termText.add(terminfo);
		termText.add(enterTerm);
		termConfluence.add(termText);
		
		leftinfo.setText("nr. of steps left");
		leftText.add(leftinfo);
		leftText.add(enterStepsLeft);
		lrText.add(leftText);
		
		rightinfo.setText("nr. of steps right");
		rightText.add(rightinfo);
		rightText.add(enterStepsRight);
		lrText.add(rightText);
		termConfluence.add(lrText);
	}
}
