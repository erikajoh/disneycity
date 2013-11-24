package restaurant_bayou.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class BayouAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 400;
    private final int WINDOWY = 330;
	private int lastTableX = 0;
	private int lastTableY = 0;
	private int tableWidth = this.getWidth()/9;
	private int tableHeight = this.getHeight()/7;
	private Image backgroundImage; 
	
    private Image bufferImage;
    private Dimension bufferSize;
    
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
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

    public BayouAnimationPanel() {
    	this.setSize(WINDOWX, WINDOWY);
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
        backgroundImage = Toolkit.getDefaultToolkit().getImage("res/BlueBayou.png");
        g2.drawImage(backgroundImage, 0, 0, 400, 330, null);
      
        synchronized(guis) {
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
                gui.draw(g2);
            }
        }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(CashierGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WaiterGui gui) {
    	guis.add(gui);
    }
    
    public void addGui(CookGui gui) {
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
