package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame that shows the user the
 * confluence proof for the given term and steps within the selected trss.
 * 
 * @author Marieke Peeters
 * 
 */
public class ConfluencePanel extends GUI {
	
	private static JPanel[] trss;
	private static JPanel trsdescr, confluence, buttons;
	private static JScrollPane confluenceProof;
	private static JLabel[] jLabelsTrss;
	private static JLabel[][] jLabelsTrssRules; 
	private static JLabel confluenceLabel1;
	private static JLabel[] confluenceLabel2;
	private static JButton newStartButton, quitButton;
	
	/**
	 * This method changes the panel into a panel that gives you the confluence for the term 
	 * within the chosen TRS's and gives you two choices:
	 * - end program
	 * - restart program from MainPanel
	 */
	public static void goToConfluencePanel(ActionEvent evt) {
		// set the title of the frame
		gui.setTitle("Confluence Construction");
		
		// initialize all of the panels
		initTrss();
		initConfluence();
		initButtons();


		// remove old panels and add new panels to the frame
		gui.getContentPane().removeAll();
		gui.getContentPane().setPreferredSize(new Dimension(1000,500));
		gui.getContentPane().setLayout(new GridLayout(3,1));
		gui.getContentPane().add(trsdescr, BorderLayout.WEST);
		gui.getContentPane().add(confluenceProof, BorderLayout.WEST);
		gui.getContentPane().add(buttons, BorderLayout.WEST);
		
		gui.pack();
	}
	
	/**
	 * This method creates an array of panels. Each panel contains an array of labels.
	 * The panel-array contains all trss. Each label-array displays all the rules of each trs.
	 */
	private static void initTrss() {
		// initialize components
		trsdescr = new JPanel();
		trss = new JPanel[selectedTrssConfluence.size()];
		for (int l=0; l<trss.length; l++)
			trss[l] = new JPanel();
		jLabelsTrss = new JLabel[selectedTrssConfluence.size()];
		for (int i=0; i<jLabelsTrss.length; i++)
			jLabelsTrss[i] = new JLabel();
		jLabelsTrssRules = new JLabel[selectedTrssConfluence.size()][];
		
		trsdescr.setLayout(new BoxLayout(trsdescr, BoxLayout.X_AXIS));
		trsdescr.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		trsdescr.setPreferredSize(new Dimension(500, 100));
		
		for (JPanel trs: trss) {
			trs.setLayout(new BoxLayout(trs,BoxLayout.Y_AXIS));
			trs.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
		}
		
		// set preferences for the labels and add them to the panels
		int i=0;
		for (GeneralTRS trs: selectedTrssConfluence) {
			jLabelsTrssRules[i] = new JLabel[trs.rules.size()];
			for (int k=0;k<jLabelsTrssRules[i].length;k++)
				jLabelsTrssRules[i][k] = new JLabel();
			jLabelsTrss[i].setText("TRS" + (i+1) + " " + trs.name + ": ");
			trss[i].add(jLabelsTrss[i]);
			int j=0;
			for (Rule r: trs.rules) {
				jLabelsTrssRules[i][j].setText(r.ruleToString() + "			 ");
				trss[i].add(jLabelsTrssRules[i][j]);
				j++;
			}
			i++;
		}
		
		for (JPanel trs: trss)
			trsdescr.add(trs);
	}
	
	/**
	 * This method creates a panel containing a label that displays the
	 * confuence for the term.
	 */
	private static void initConfluence() {
		// initialize components
		confluence = new JPanel();
		confluenceProof = new JScrollPane(confluence);
		confluenceLabel1 = new JLabel();
		
		// set preferences for the panel
		confluence.setLayout(new BoxLayout(confluence, BoxLayout.Y_AXIS));
		confluence.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		// settings of the label and add it to panel
		if (term!=null) {
			confluenceLabel1.setText("Confluence for term: " + term.termToString());
		} else {
			confluenceLabel1.setText("Cannot perform confluence check on empty term.");
		}
		confluence.add(confluenceLabel1);
		initConfluence1();
	}
	
	/**
	 * This method creates the confluence proof and adds it to the
	 * label. The label is scrollable.
	 */
	private static void initConfluence1() {
		Rule emptyRule = new Rule(term,term);
		ArrayList<Integer> nul = new ArrayList<Integer>();
		nul.add(0);
		Position emptyPos = new Position(nul);
		if (redRowL.isEmpty()) {
			redRowL.add(new Reductionstep(term,term,emptyRule,emptyPos));
		}
		if (redRowR.isEmpty()) {
			redRowR.add(new Reductionstep(term,term,emptyRule,emptyPos));
		}	
		String[] proof, proof1, proof2a;
		proof = null;
		proof2a = null;
		ArrayList<String> proof2b = new ArrayList<String>();
		proof1 = new String[3+redRowR.size()+redRowL.size()];
		proof1[0] = ("You selected the following diverging reduction rows:");
		proof1[1] = ("First reduction row starting from term " + term.termToString() +":");
		for (int i=0;i<redRowL.size();i++)
			proof1[2+i] = redRowL.get(i).reductionToString();
		proof1[2 + redRowL.size()] = ("Second reduction row starting from term " + term.termToString() +":");
		for (int i=0; i<redRowR.size();i++)
			proof1[3 + redRowL.size() + i] = redRowR.get(i).reductionToString();
		if (selectedTrssConfluence.size()==1) {
			for (GeneralTRS trs: selectedTrssConfluence) {
				boolean ortho = false;
				boolean wcrsn = false;
				for (String[] s: trs.assumptions) {
					if (s[1].equals("Orthogonal"))
						ortho = true;
					else if (s[1].equals("WCR&SN"))
						wcrsn = true;
				}
				if (wcrsn) {
					ConfluenceWCRSN.confluenceWCRSN(redRowL.get(redRowL.size()-1).resultTerm,redRowR.get(redRowR.size()-1).resultTerm,trs);
					proof2a = ConfluenceWCRSN.outputConfl;
					break;
				} else if (ortho) {
					ConfluenceOrthogonal.confluenceOrthogonal(redRowL,redRowR,trs);
					proof2a = new String[ConfluenceOrthogonal.completeOutput.size()];
					for (int i=0;i<proof2a.length;i++)
						proof2a[i] = ConfluenceOrthogonal.completeOutput.get(i);
				}
			}
			if (proof2a!=null) {
				proof = new String[proof1.length + proof2a.length];
				for (int i=0;i<proof2a.length;i++) 
					proof[i+proof1.length] = proof2a[i];
			}
		} else {
			int rank = CrankRterm.getRank(term,newTRS);
			// create reductionrows from steps
			ArrayList<ReductionRow> leftRow = ReductionRow.createReductionRowArray(redRowL,rank,newTRS);
			ArrayList<ReductionRow> rightRow = ReductionRow.createReductionRowArray(redRowR,rank,newTRS);
			proof2b = ConfluenceModular.confluenceModularStart(leftRow,rightRow,newTRS);
			if (!proof2b.isEmpty()) {
				proof = new String[proof1.length + proof2b.size()];
				int i=0;
				for (String s: proof2b) {
					proof[i+proof1.length] = s;
					i++;
				}
			}
		}
		if (proof2a==null&&proof2b.isEmpty()) {
			proof = new String[proof1.length];
		}
		for (int i=0;i<proof1.length;i++)
			proof[i] = proof1[i];
		if (proof!=null) {
			confluenceLabel2 = new JLabel[proof.length];
			for (int i=0;i<proof.length;i++) {
				confluenceLabel2[i] = new JLabel();
				confluenceLabel2[i].setText(proof[i]);
				confluence.add(confluenceLabel2[i]);
			}
		}
	}
	
	/**
	 * This method creates a panel that contains two buttons:
	 * one to go back to the main panel and one to quit the program.
	 */
	private static void initButtons() {
		// initialize components
		buttons = new JPanel();
		newStartButton = new JButton();
		quitButton = new JButton();
		
		// set preferences of panel
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		buttons.setBorder(new EmptyBorder(new Insets(90, 850, 10, 10)));
		buttons.setPreferredSize(new Dimension(500, 100));
		
		// edit button and add to panel
		newStartButton.setText("New Start");
		newStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainPanel.mainPanel(evt);
			}
		});
		buttons.add(newStartButton);
		
		// edit button and add to panel
		quitButton.setText("    Quit    ");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitForm(evt);
			}
		});
		buttons.add(quitButton);
	}
}
