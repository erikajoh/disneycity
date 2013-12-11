package restaurant_haus.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class HausAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 400;
    private final int WINDOWY = 330;
    private Image bufferImage;
    private Dimension bufferSize;
    
    static final int TABLEDIM = 50;
    static final int TABLEPOSX = 75;
    static final int TABLEPOSY = 125;
    
    final int TABLEOFFSET = 100;

    private List<Gui> guis = new ArrayList<Gui>();
    
    Timer timer;
    Image backgroundImage;

    public HausAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
        backgroundImage = Toolkit.getDefaultToolkit().getImage("res/hausRest.png");
 
    	timer = new Timer(12, this );
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
    
    public void addGui(CookGui gui) {
    	guis.add(gui);
    }
    
    public void pauseAnim() {
    	timer.stop();
    }
    
    public void unpauseAnim() {
    	timer.start();
    }
}
