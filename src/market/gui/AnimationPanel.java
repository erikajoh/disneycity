package market.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

//    private final int WINDOWX = 350;
//    private final int WINDOWY = 450;
	private int lastTableX = 0;
	private int lastTableY = 0;
	private int tableWidth = this.getWidth()/9;
	private int tableHeight = this.getHeight()/7;
	
    private Image bufferImage;
    private Dimension bufferSize;
    
    private List<Gui> guis = new ArrayList<Gui>();
    public List<Table> tables = new ArrayList<Table>();
    Timer timer = new Timer(5, this);
    Graphics2D g2;
    
    public class Table {
    	int xpos, ypos, width, height;
    	boolean isOccupied;
    	public Table(int x, int y, int w, int h, boolean occupied){
    		xpos = x; ypos = y; width = w; height = h; isOccupied = occupied;
    	}
    }

    public AnimationPanel(int x, int y) {
    	this.setSize(x, y);
        setVisible(true);
        
        bufferSize = this.getSize();
                
    	timer.start();	
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
	public void pauseOrResume(){
		if (timer.isRunning()) timer.stop();
		else timer.start();
	}
	

    public void paintComponent(Graphics g) {
        g2 = (Graphics2D)g;
        
    	//Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        //Here are the tables
        addTable();
        
        g2.setColor(Color.BLACK);
        g2.draw(new Line2D.Double(0, this.getHeight()/12, this.getWidth()/2, this.getHeight()/12));
        g2.draw(new Line2D.Double(3*this.getWidth()/4, 0, 3*this.getWidth()/4, this.getHeight()));
        g2.draw(new Line2D.Double(3*this.getWidth()/4, 5*this.getHeight()/12, this.getWidth(), 5*this.getHeight()/12));
        g2.draw(new Line2D.Double(3*this.getWidth()/4, 5*this.getHeight()/6, this.getWidth(), 5*this.getHeight()/6));
    	
        g2.drawString("customers", 4*this.getWidth()/9-10, this.getHeight()/12);
        g2.drawString("waiters", 4*this.getWidth()/9, this.getHeight()/12+10);
        g2.drawString("kitchen", 5*this.getWidth()/6, 10);
        g2.drawString("(plating)", 5*this.getWidth()/6, 20);
        g2.drawString("(cooking)", 5*this.getWidth()/6, 5*this.getHeight()/12+10);
        g2.drawString("(fridge)", 5*this.getWidth()/6, 5*this.getHeight()/6+10);
        
        g2.setColor(Color.BLACK);
        g2.fillRect(5*this.getWidth()/6, 5*this.getHeight()/6+20, this.getWidth()/16, this.getHeight()/16); // fridge
        g2.fillRect(5*this.getWidth()/6, 5*this.getHeight()/12+20, this.getWidth()/16, this.getHeight()/16); // grill
        g2.fillRect(5*this.getWidth()/6, 30, this.getWidth()/16, this.getHeight()/16); // plating
        
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

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(CashierGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WorkerGui gui) {
    	guis.add(gui);
    }
    
    public void addTable() {
    	
        Table t1 = new Table(100, (int)(this.getHeight()*0.7143), this.getWidth()/10, this.getHeight()/7, false);
        Table t2 = new Table(200, (int)(this.getHeight()*0.7143), this.getWidth()/10, this.getHeight()/7, false);
        Table t3 = new Table(300, (int)(this.getHeight()*0.7143), this.getWidth()/10, this.getHeight()/7, false);
        Table t4 = new Table(400, (int)(this.getHeight()*0.7143), this.getWidth()/10, this.getHeight()/7, false);
        tables.add(t1);
    	tables.add(t2);
    	tables.add(t3);
    	tables.add(t4);
    	
    	List<Color> colors = new ArrayList<Color>();
    	colors.add(Color.RED);
    	colors.add(Color.ORANGE);
    	colors.add(Color.BLUE);
    	colors.add(Color.RED);
    	
    	int colorCount = 0;
        for (Table t: tables) {
        	g2.setColor(colors.get(colorCount++%4));
        	g2.fillRect(t.xpos,  t.ypos, t.width, t.height);
        }
        
    }
   
}
