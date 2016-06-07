package trs;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JButton;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: a frame to look at the details
 * of the selected trs.
 * 
 * @author Marieke Peeters
 */
public class ViewTrsPanel {
	
	public static JFrame view = new JFrame();
	
	private static JPanel labelView, labelView1, labelView2, labelView3, labelView4, buttPanel;
	private static JScrollPane varScroll,functScroll,rulScroll,assuScroll;
	private static JLabel labelName, labelViewVars, labelViewFunct, labelViewRul, labelViewAss;
	private static JLabel[] vars, functs, ruls, asss;
	private static JButton okButt;
	
	private static GeneralTRS trs;
	
	/**
	 * Constructor ViewTrsPanel creates the panel that will contain the features of a trs
	 */
	public ViewTrsPanel(GeneralTRS giventrs) {
		trs = giventrs;
		view.setVisible(true);
		viewTrs();
    }
	
	/**
	 * This method creates a frame that displays 
	 * the characteristics of the selected trs.
	 */
	private static void viewTrs(){
		// Set title of the frame
        view.setTitle("Look at trs " + trs.name);
        
        // initialize panels
        initLabel();
        initLabel1();
        initLabel2();
        initLabel3();
        initLabel4();
        initOkButt();
        
        // remove old panels and add new panels to the frame
        view.getContentPane().removeAll();
        view.getContentPane().setPreferredSize(new Dimension(300, 420));
        view.getContentPane().setLayout(new GridLayout(6,1));
        view.getContentPane().add(labelView);
        view.getContentPane().add(varScroll);
        view.getContentPane().add(functScroll);
        view.getContentPane().add(rulScroll);
        view.getContentPane().add(assuScroll);
        view.getContentPane().add(buttPanel);
        view.pack();
	}
	
	/**
	 * This method creates the label saying
	 * what trs is shown.
	 */
	private static void initLabel() {
		// initialize components
		labelView = new JPanel();
		labelName = new JLabel();
		
		//set preferences Panel
		labelView.setLayout(new GridLayout(1,1));
		labelView.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
		labelView.setPreferredSize(new Dimension(290,70));
		
		labelName.setText(trs.name + " contains the following features:");
		labelView.add(labelName);
	}
	
	/**
	 * This method initiates the creation of the label
	 * displaying all the variables. It is scrollable.
	 */
	private static void initLabel1() {
		//initialize components
		labelView1 = new JPanel();
		varScroll = new JScrollPane(labelView1);
		labelViewVars = new JLabel();
		
		//set preferences for panel
		labelView1.setLayout(new GridLayout(trs.variables.size()+1,1));
		labelView1.setBorder(new EmptyBorder(new Insets(5,10,5,10)));
		
		// settings label, add to panel
		labelViewVars = new JLabel();		
		labelViewVars.setText("Variables: ");
		labelView1.add(labelViewVars);
		
		vars = new JLabel[trs.variables.size()];
		int i=0;
		for (Variable v: trs.variables)  {
			vars[i] = new JLabel();
			vars[i].setText(v.name);
			labelView1.add(vars[i]);
			i++;
		}
	}
	
	/**
	 * This method initiates the creation of the label
	 * displaying all the functions. It is scrollable.
	 */
	private static void initLabel2() {
		//initialize components
		labelView2 = new JPanel();
		functScroll = new JScrollPane(labelView2);
		labelViewFunct = new JLabel();
		
		//set preferences for panel
		labelView2.setLayout(new GridLayout(trs.functions.size()+1,1));
		labelView2.setBorder(new EmptyBorder(new Insets(5,10,5,10)));
		
		// settings label, add to panel
		labelViewFunct = new JLabel();		
		labelViewFunct.setText("Functions: ");
		labelView2.add(labelViewFunct);
		
		functs = new JLabel[trs.functions.size()];
		int i=0;
		for (Function f: trs.functions)  {
			functs[i] = new JLabel();
			functs[i].setText(f.functionsymbol + "(arity=" + f.arity + ")");
			labelView2.add(functs[i]);
			i++;
		}
	}
	
	/**
	 * This method initiates the creation of the label
	 * displaying all the rules. It is scrollable.
	 */
	private static void initLabel3() {
		//initialize components
		labelView3 = new JPanel();
		rulScroll = new JScrollPane(labelView3);
		labelViewRul = new JLabel();
		
		//set preferences for panel
		labelView3.setLayout(new GridLayout(trs.rules.size()+1,1));
		labelView3.setBorder(new EmptyBorder(new Insets(5,10,5,10)));
		
		// settings label, add to panel
		labelViewRul = new JLabel();		
		labelViewRul.setText("Rules: ");
		labelView3.add(labelViewRul);
		
		ruls = new JLabel[trs.rules.size()];
		int i=0;
		for (Rule r: trs.rules)  {
			ruls[i] = new JLabel();
			ruls[i].setText(r.lefthandside.termToString() + " -> " + r.righthandside.termToString() + (". "));
			labelView3.add(ruls[i]);
			i++;
		}
	}
	
	/**
	 * This method initiates the creation of the label
	 * displaying all the assumptions. It is scrollable.
	 */
	private static void initLabel4() {
		// initialize components
		labelView4 = new JPanel();
		assuScroll = new JScrollPane(labelView4);
		labelViewAss = new JLabel();
		
		//set preferences for panel
		labelView4.setLayout(new GridLayout(trs.assumptions.size()+1,1));
		labelView4.setBorder(new EmptyBorder(new Insets(5,10,5,10)));
		
		// settings label, add to panel
		labelViewAss = new JLabel();
		labelViewAss.setText("Assumptions: ");
		labelView4.add(labelViewAss);
		
		asss = new JLabel[trs.assumptions.size()];
		int i=0;
		for (String[] a: trs.assumptions) {
			asss[i] = new JLabel();
			asss[i].setText(a[1]);
			labelView4.add(asss[i]);
			i++;
		}
	}
	
	/**
	 * This method initiates the creation of the button
	 * to close the panel.
	 */
	private static void initOkButt() {
		buttPanel = new JPanel();
		okButt = new JButton();
		
		//set preferences for panel
		buttPanel.setLayout(new GridLayout(1,1));
		buttPanel.setBorder(new EmptyBorder(new Insets(10,10,35,220)));
		buttPanel.setPreferredSize(new Dimension(290,70));
		
		// settings button & add to panel
		okButt.setText("ok");
		okButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitForm(evt);
			}
		});
		
		buttPanel.add(okButt);
	}
	
	/**
	 * Close frame
	 */
	private static void exitForm(ActionEvent evt) {
		view.dispose();
    }
}