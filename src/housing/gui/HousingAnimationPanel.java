package housing.gui;

import housing.ResidentAgent;

import javax.swing.*;

import simcity.PersonAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HousingAnimationPanel extends JPanel implements ActionListener {

	private int lastTableX = 0;
	private int lastTableY = 0;
	private int tableWidth = this.getWidth()/9;
	private int tableHeight = this.getHeight()/7;
	
    private Image bufferImage;
    private Dimension bufferSize;
   
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    Timer timer = new Timer(5, this);
    Graphics2D g2;
    
    private List<Line> lines = new ArrayList<Line>();
    
    String picture;
    
    class Line {
    	int xs, ys, xe, ye;
    	public Line(int x1, int y1, int x2, int y2) {
    		xs = x1;
    		xe = x2;
    		ys = y1;
    		ye = y2;
    	}
    }
    
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
        
        g2.setColor(getBackground());
        g2.fillRect(0, 330, 400, 50);
        
        g2.setColor(Color.MAGENTA);
        for (Line l : lines) {
        	g2.drawLine(l.xs, l.ys, l.xe, l.ye);
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }
    
    public void addLine(int x1, int x2, int y1, int y2) {
    	lines.add(new Line(x1, y1, x2, y2));
    }
    
    public void updatePosition() {
    	synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
        }
    	
    	if (g2 != null) {
    		g2.setColor(Color.MAGENTA);
	        for (Line l : lines) {
	        	g2.drawLine(l.xs, l.ys, l.xe, l.ye);
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
        
        g2.setColor(Color.MAGENTA);
        for (Line l : lines) {
        	System.out.println("print lines");
        	g2.drawLine(l.xs, l.ys, l.xe, l.ye);
        }
        
    }
    
    public void addGui(ResidentGui gui) {
    	guis.add(gui);
    }
    
    public void addRenter(ResidentAgent r, String type, int n) {
    	ResidentGui g = new ResidentGui(r, type, n, this);
    	r.startThread();
    	addGui(g);
    }
   
}
