package restaurant_rancho.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

import restaurant_rancho.gui.CookGui;

public class RanchoAnimationPanel extends JPanel implements ActionListener {

    public static final int WINDOWX = 400;
    public static final int WINDOWY = 340;
    private Image bufferImage;
    private Dimension bufferSize;
    private Image backgroundImage; 

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

    public RanchoAnimationPanel() {
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
       
      //  g2.drawImage(backgroundImage, 0, 0, 400, 320, null);
        
       

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        backgroundImage = Toolkit.getDefaultToolkit().getImage("res/restRanchoImage.png");
        g2.drawImage(backgroundImage, 0, 0, 400, 330, null);
        //g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
       // g2.setColor(Color.ORANGE);
       // g2.fillRect((WINDOWX*2)/11, (WINDOWY*6)/10, WINDOWY/10, WINDOWY/10);//100 and 250 need to be table params

        Graphics2D g3 = (Graphics2D)g;
        
       // g3.setColor(Color.ORANGE);
        //g3.fillRect((WINDOWX*5)/11, (WINDOWY*6)/10, WINDOWY/10, WINDOWY/10); //200 and 250
        
      //  g3.fillRect((WINDOWX*8)/11, (WINDOWY*6)/10, WINDOWY/10, WINDOWY/10); // 300 and 250
        synchronized(guis) {
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
        }
       /* g3.setColor(Color.magenta.darker());
        g3.fillRect((WINDOWX*15)/20, (WINDOWY*1)/10, (WINDOWY/15), WINDOWY/15);
        
        g3.setColor(Color.cyan.darker());
        g3.fillRect((WINDOWX*13)/20, (WINDOWY*5)/20, (WINDOWY/15), WINDOWY/6);
        
        g3.setColor(Color.cyan.darker());
        g3.fillRect((WINDOWX*17)/20, (WINDOWY*5)/20, WINDOWY/15, WINDOWY/6);

		*/
        
        synchronized(guis) {
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
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
}
