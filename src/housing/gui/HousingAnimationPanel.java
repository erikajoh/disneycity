package housing.gui;

import housing.OwnerAgent;
import housing.RenterAgent;

import javax.swing.*;

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
    
    private JButton kitchen = new JButton(" ");

    public HousingAnimationPanel() {
    	this.setSize(400, 360);
        setVisible(true);
        
        bufferSize = this.getSize();
                
    	timer.start();	
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
//	public void pauseOrResume(){
//		if (timer.isRunning()) timer.stop();
//		else timer.start();
//	}
	

    public void paintComponent(Graphics g) {
        g2 = (Graphics2D)g;
        
    	//Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        g2.setColor(Color.GRAY);
        g2.fillRect((int)(this.getWidth()*0.5), (int)(this.getHeight()*0.2), this.getWidth()/50, this.getHeight()/5);
        g2.fillRect((int)(this.getWidth()*0.6), (int)(this.getHeight()*0.93), (int)(this.getWidth()*0.4), (int)(this.getHeight()*0.9));
        g2.fillRect((int)(this.getWidth()*0.95), (int)(this.getHeight()*0.6), (int)(this.getWidth()*0.2), (int)(this.getHeight()*0.4));
        g2.fillRect((int)(this.getWidth()*0.15), (int)(this.getHeight()*0.6), (int)(this.getWidth()*0.2), (int)(this.getHeight()*0.15));

        g2.setColor(Color.BLACK);
        g2.draw(new Line2D.Double(3*this.getWidth()/5, this.getHeight()/2, this.getWidth(), this.getHeight()/2));
        g2.draw(new Line2D.Double(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight()/2));
        g2.draw(new Line2D.Double(this.getWidth()/4, (int)(this.getHeight()*0.95), this.getWidth()/4, this.getHeight()));
        g2.draw(new Line2D.Double(this.getWidth()/3, (int)(this.getHeight()*0.95), this.getWidth()/3, this.getHeight()));
        
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
    
    public void addGui(RenterGui gui) {
    	guis.add(gui);
    }
    
    public void addRenter(RenterAgent r) {
    	RenterGui g = new RenterGui(r);
    	r.startThread();
    	addGui(g);
    }
    
    public void setOwner(OwnerAgent o) {
    	o.startThread();
    }
    
//    public void addGui(OwnerGui gui) {
//    	guis.add(gui);
//    }
   
}