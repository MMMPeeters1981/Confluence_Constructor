package conf_cons.gui;

import conf_cons.conf.*;
import conf_cons.basictrs.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * This class is a subclass of GUI. It contains a couple of methods 
 * to take the user to a new view: the openingpanel.
 * 
 * @author Marieke Peeters
 */
public class OpeningPanel extends GUI {
	
	public static JPanel panel;
	private static JLabel jLabel1;
	public static JButton jButton1;
	
	/**
	 * The openingPanel contains information about the program and
	 * a button to start the program.
	 */
	public static void openingPanel() {

		// initialize panel
		initPanel();

		// add panels to the frame
		gui.getContentPane().setPreferredSize(new Dimension(500, 300));
		gui.getContentPane().add(panel, BorderLayout.CENTER);

		gui.pack();
	}
	
	/**
	 * Create the openingPanel containing information about the program and
	 * a button to start the program.
	 */
	private static void initPanel() {
		// initialize components
		panel = new JPanel();
		jButton1 = new JButton();
		jLabel1 = new JLabel();
		
		// settings panel
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    panel.setBorder(new EmptyBorder(new Insets(40, 20, 50, 50)));
		
	    // settings label and add to panel
	    jLabel1.setText("Confluence Constructor by M. Peeters");
	    jLabel1.setBorder(new EmptyBorder(new Insets(10,10,40,10)));
	    panel.add(jLabel1);
		
	    panel.add(Box.createVerticalGlue());
	    
	    // settings button and add button to panel
	    jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainPanel.mainPanel(evt);
			}
		});
		jButton1.setText("Start program");
		jButton1.setPreferredSize(new Dimension (25,25));
		panel.add(jButton1);
	}
}

