package bank.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 400;
    private final int WINDOWY = 330;
	
	private int tellerCount = 0;


    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
         
    	Timer timer = new Timer(5, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //Clear the screen by painting a rectangle the size of the frame
        for(int i = 0; i<4; i++){
          g2.setColor(getBackground());
          g2.fillRect(90*i+35, 0, WINDOWX, WINDOWY );
          
          Color stone = new Color(189, 165, 150);
          g2.setColor(stone);
          g2.fillRect(90*i+35, 0, 60, 60);
        
          g2.setColor(getBackground());
          g2.fillRect(90*i+45, 10, 40, 40);
        
          Color window = new Color(205, 227, 239);
          g2.setColor(window);
          g2.fillRect(90*i+45, 50, 40, 10);
        
          g2.setColor(Color.DARK_GRAY);
          g2.fillRect(90*i+45, 10, 20, 20);
        }

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

    public void addGui(BankCustomerGui gui) {
        guis.add(gui);
    }
 
    public void addGui(TellerGui gui) {
        guis.add(gui);
        tellerCount++;
    }

}
