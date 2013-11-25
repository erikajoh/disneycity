package transportation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import simcity.gui.Gui;

public class TransportationPanel extends JPanel implements ActionListener{
	private final int WINDOWX = 400;
	private final int WINDOWY = 330;
	
	private BufferedImage img;
	
	private List<Gui> guis = new ArrayList<Gui>();
	Timer timer;
	
	public TransportationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        try {
			img =  ImageIO.read(new File("src" + File.separator + "res" + File.separator + "Background.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    	timer = new Timer(20, this );
    	timer.start();
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.drawImage(img, 0, 0, null);

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
	
	public void addGui (Gui gui) {
		guis.add(gui);
	}
	
	public void pauseAnim() {
    	timer.stop();
    }
    
    public void unpauseAnim() {
    	timer.start();
    }
}
