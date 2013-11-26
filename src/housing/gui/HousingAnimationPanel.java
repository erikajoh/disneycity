package housing.gui;

import housing.ResidentAgent;

import javax.swing.*;

import simcity.PersonAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.ArrayList;

public class HousingAnimationPanel extends JPanel implements ActionListener {

	private int lastTableX = 0;
	private int lastTableY = 0;
	private int tableWidth = this.getWidth()/9;
	private int tableHeight = this.getHeight()/7;
	
    private Image bufferImage;
    private Dimension bufferSize;
    
    private List<Gui> guis = new ArrayList<Gui>();
    Timer timer = new Timer(5, this);
    Graphics2D g2;
    
    String picture;
    
    public HousingAnimationPanel() {
    	this.setSize(400, 360);
        setVisible(true);
        
        bufferSize = this.getSize();
                
    	timer.start();	
    	
    }
    
    public void setBackground(String f) {
    	picture = f;
    }

	public void actionPerformed(ActionEvent e) {
//		SwingUtilities.invokeLater(new Runnable() {
//		    public void run() {
//		      // Here, we can safely update the GUI
//		      // because we'll be called from the
//		      // event dispatch thread
//		    	repaint();
//		    }
//		  });
		updatePosition();
		repaint();  //Will have paintComponent called
	}	

    public void paintComponent(Graphics g) {
        g2 = (Graphics2D)g;
        
    	//Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        Image backgroundImage = Toolkit.getDefaultToolkit().getImage(picture);
        g2.drawImage(backgroundImage, 0, 0, 400, 330, null);

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
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
    
    public void update() {
    	this.getParent().setVisible(true);
    	System.out.println("updating");
    	for(Gui gui : guis) {
            if (gui.isPresent()) {
            	System.out.println("a gui is present");
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }
    
    public void addGui(ResidentGui gui) {
    	guis.add(gui);
    }
    
    public void addRenter(ResidentAgent r, String type, int n) {
    	ResidentGui g = new ResidentGui(r, type, n);
    	r.startThread();
    	addGui(g);
    }
   
}
