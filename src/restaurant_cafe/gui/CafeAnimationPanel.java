package restaurant_cafe.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class CafeAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 400;
    private final int WINDOWY = 330;
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
	Image backgroundImage;


    private List<Gui> guis = new ArrayList<Gui>();

    public CafeAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        backgroundImage = Toolkit.getDefaultToolkit().getImage("res/restCafe.png");
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(5, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		updatePosition();
		repaint();  //Will have paintComponent called
	}
	
	public void updatePosition() {
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //customer waiting area
        
        g2.drawImage(backgroundImage, 0, 0, 400, 330, null);
        
        g2.setColor(getBackground());
        g2.fillRect(0, WINDOWY, WINDOWX, 50);

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
