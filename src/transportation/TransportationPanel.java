package transportation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import simcity.gui.Gui;

public class TransportationPanel extends JPanel implements ActionListener{
	private final int WINDOWX = 400;
	private final int WINDOWY = 350;
	
	private List<Gui> guis = new ArrayList<Gui>();
	Timer timer;
	
	public TransportationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	timer = new Timer(20, this );
    	timer.start();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
