package restaurant_pizza.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import restaurant_pizza.HostAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class PizzaAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOW_X = 400;
    private final int WINDOW_Y = 330;
    private final int CASHIER_X = 400;
    private final int CASHIER_Y = 600;
    private final int PERSON_WIDTH = 50;
    private final int PERSON_HEIGHT = 50;
    private final int TIMER_INTERVAL = 4;
    
    private final int PLATING_X = 210;
    private final int PLATING_Y = 140;
    private final int PLATING_WIDTH = 130;
    private final int PLATING_HEIGHT = 5;
    private final int FRIDGE_X = 260;
    private final int FRIDGE_Y = 0;
    private final int COOKING_X = 400;
    private final int COOKING_Y = 50;
    private final int COOKING_WIDTH = 200;
    private final int COOKING_HEIGHT = 50;
    
    private final int TEXT_OFFSET = 15;
    private final int LABEL_FONT = 14; 
    
    private Image bi;

    private List<Gui> guis = new ArrayList<Gui>();

    public PizzaAnimationPanel() {
    	setSize(WINDOW_X, WINDOW_Y);
        setVisible(true);
        
    
        bi = Toolkit.getDefaultToolkit().getImage("res/PizzaPort.png");
	
 
    	Timer timer = new Timer(TIMER_INTERVAL, this);
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        //g2.fillRect(PERSON_INITX, PERSON_INITY, WINDOWX, WINDOWY );
        g2.drawImage(bi, 0, 0, 400, 330, null);
        
       
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
        
    }

    public void addGui(Gui gui) {
        guis.add(gui);
    }
}
