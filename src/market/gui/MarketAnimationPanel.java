package market.gui;

import housing.ResidentAgent;

import javax.swing.*;

import simcity.PersonAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.ArrayList;

public class MarketAnimationPanel extends JPanel implements ActionListener {

	private int lastTableX = 0;
	private int lastTableY = 0;
	private int tableWidth = this.getWidth()/9;
	private int tableHeight = this.getHeight()/7;
	
    private Image bufferImage;
    private Dimension bufferSize;
    
    private List<Gui> guis = new ArrayList<Gui>();
    Timer timer = new Timer(5, this);
    Graphics2D g2;
    
    public MarketAnimationPanel() {
    	this.setSize(400, 360);
        setVisible(true);
        
        bufferSize = this.getSize();
                
    	timer.start();	
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}	

    public void paintComponent(Graphics g) {
        g2 = (Graphics2D)g;
        
    	//Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        g2.setColor(Color.GRAY);
        g2.fillRect((int)(this.getWidth()*0.15), (int)(this.getHeight()*0.4), (int)(this.getWidth()*0.075), (int)(this.getHeight()*0.2));
        g2.fillRect((int)(this.getWidth()*0.4), (int)(this.getHeight()*0.4), (int)(this.getWidth()*0.1), (int)(this.getHeight()*0.7));
        g2.fillRect((int)(this.getWidth()*0.65), (int)(this.getHeight()*0.4), (int)(this.getWidth()*0.1), (int)(this.getHeight()*0.7));
        g2.fillRect((int)(this.getWidth()*0.9), (int)(this.getHeight()*0.4), (int)(this.getWidth()*0.1), (int)(this.getHeight()*0.7));

        g2.setColor(Color.BLACK);
        g2.draw(new Line2D.Double((int)(this.getWidth()*0.25), 0, (int)(this.getWidth()*0.25), this.getHeight()));
        g2.draw(new Line2D.Double((int)(this.getWidth()*0.25), this.getHeight()/4, this.getWidth(), this.getHeight()/4));
        g2.draw(new Line2D.Double(2*this.getWidth()/3, 0, 2*this.getWidth()/3, this.getHeight()/4));
        
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
    
    public void addGui(WorkerGui gui) {
    	guis.add(gui);
    }
    
    public void addGui(CustomerGui gui) {
    	System.out.println("added gui");
    	guis.add(gui);
    }
   
}
