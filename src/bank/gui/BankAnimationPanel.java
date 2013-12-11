package bank.gui;

import javax.swing.*;

import bank.gui.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class BankAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 400;
    private final int WINDOWY = 330;
	Timer timer = new Timer(5, this);
	private Image backgroundImage = Toolkit.getDefaultToolkit().getImage("res/floorboard.png"); 
	private Image tellerLayoutImage  = Toolkit.getDefaultToolkit().getImage("res/bankTellerLayout.png");
	boolean initialPaint = false;

	private int tellerCount = 0;

	private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

    public BankAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
         
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		updatePosition();
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY);
        g2.drawImage(backgroundImage, 0, 0, WINDOWX, WINDOWY, null);
        g2.drawImage(tellerLayoutImage, 0, 0, WINDOWX, 80, null);
        
        g2.setColor(getBackground());
        g2.fillRect(0, 330, WINDOWX, 50);


        synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
    }

    public void updatePosition() {
    	synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
        }
    }
   
    public void addGui(BankCustomerGui gui) {
        guis.add(gui);
    }
 
    public void addGui(TellerGui gui) {
        guis.add(gui);
        tellerCount++;
    }

}
