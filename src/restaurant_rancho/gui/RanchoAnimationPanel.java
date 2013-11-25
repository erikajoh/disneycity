package restaurant_rancho.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import restaurant_rancho.gui.CookGui;

public class RanchoAnimationPanel extends JPanel implements ActionListener {

    public static final int WINDOWX = 400;
    public static final int WINDOWY = 340;
    private Image backgroundImage; 

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

    public RanchoAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(5, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		updatePosition();
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(getBackground());
        backgroundImage = Toolkit.getDefaultToolkit().getImage("res/restRanchoImage.png");
        g2.drawImage(backgroundImage, 0, 0, 400, 330, null);

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

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    
    public void addGui(CookGui gui) {
    	guis.add(gui);
    }
}
