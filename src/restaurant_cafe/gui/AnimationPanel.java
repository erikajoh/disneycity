package restaurant_cafe.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 550;
    private final int WINDOWY = 450;
    private Image bufferImage;
    private Dimension bufferSize;
	static final int TABLE1X = 110; //TABLE 1 X Position
	static final int TABLE1Y = 300; //TABLE 1 Y Position
	static final int TABLE2X = 360; //TABLE 2 X Position
	static final int TABLE2Y = 100; //TABLE 2 Y Position
	static final int TABLE3X = 360; //TABLE 3 X Position
	static final int TABLE3Y = 300; //TABLE 3 Y Position
	static final int TABLEWIDTH = 50; //TABLE Width
	static final int TABLEHEIGHT = 50; //TABLE Height


    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(5, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //customer waiting area
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 60, 60);
        
        //waiter waiting area
        Color brown = new Color(254, 240, 200);
        g2.setColor(brown);
        g2.fillRect(0, WINDOWY-100, 40, 100);
        
        //cooking area
        Color stone = new Color(189, 165, 150);
        g2.setColor(stone);
        g2.fillRect(WINDOWX-60, WINDOWY-60, 60, 60);
        
        //cooking area
        g2.setColor(Color.BLACK);
        g2.fillRect(WINDOWX-80, WINDOWY-60, 20, 60);
        
        //plating area
        Color gray = new Color(219, 214, 211);
        g2.setColor(gray);
        g2.fillRect(WINDOWX-60, WINDOWY-80, 60, 20);

        //Here is table 1
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLE1X, TABLE1Y, TABLEWIDTH, TABLEHEIGHT); //200 and 250 need to be table params

        //Here is table 2
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(TABLE2X, TABLE2Y, TABLEWIDTH, TABLEHEIGHT); 
        //Here is table 2
        g2.setColor(Color.RED);
        g2.fillRect(TABLE3X, TABLE3Y, TABLEWIDTH, TABLEHEIGHT); 


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
    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    public void addGui(CashierGui gui) {
        guis.add(gui);
    }
    public void addGui(CookGui gui) {
        guis.add(gui);
    }
}
