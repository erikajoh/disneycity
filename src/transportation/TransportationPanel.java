package transportation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import simcity.gui.Gui;
import simcity.gui.SimCityGui;

public class TransportationPanel extends JPanel implements ActionListener, MouseListener {
	private final int WINDOWX = 400;
	private final int WINDOWY = 330;
	
	private BufferedImage img;
	SimCityGui gui;
	TransportationController controller;
	
	private List<Gui> guis = new ArrayList<Gui>();
	Timer timer;
	
	class BuildingFinder {
		int xLeft, xRight, yTop, yBottom;
		String name;
		
		BuildingFinder(int xLeft, int xRight, int yTop, int yBottom, String name) {
			this.xLeft = xLeft;
			this.xRight = xRight;
			this.yTop = yTop;
			this.yBottom = yBottom;
			this.name = name;
		}
	}
	
	List<BuildingFinder> buildings;
	
	public TransportationPanel(SimCityGui gui) {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        this.gui = gui;
        
        try {
			img =  ImageIO.read(new File("src" + File.separator + "res" + File.separator + "Background.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    	timer = new Timer(20, this );
    	timer.start();
    	
    	controller = new TransportationController();
    	
    	addMouseListener(this);
    	
    	buildings = new ArrayList<BuildingFinder>();
    	
    	buildings.add(new BuildingFinder(50,100,2,52,"Rancho"));
    	buildings.add(new BuildingFinder(150,225,2,52,"Bank"));
    	buildings.add(new BuildingFinder(225,275,2,52,"Apt1"));
    	buildings.add(new BuildingFinder(300,350,2,52,"Apt2"));
    	buildings.add(new BuildingFinder(0,50,52,102,"Apt3"));
    	buildings.add(new BuildingFinder(0,50,152,202,"Apt4"));
    	buildings.add(new BuildingFinder(350,400,102,152,"Bayou"));
    	buildings.add(new BuildingFinder(0,50,227,277,"Haus"));
    	buildings.add(new BuildingFinder(350,400,227,277,"Apt6"));
    	buildings.add(new BuildingFinder(50,100,277,327,"Apt5"));
    	buildings.add(new BuildingFinder(100,150,277,327,"Pizza"));
    	buildings.add(new BuildingFinder(175,275,277,327,"Market"));
    	buildings.add(new BuildingFinder(300,350,277,327,"Cafe"));
    	buildings.add(new BuildingFinder(305,400, 2, 102,"Mansion"));
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

	@Override
	public void mouseClicked(MouseEvent me) {
		String name = findBuilding(me.getX(), me.getY());
		if(name != null)
			gui.showPanel(name);
	}
	
	private String findBuilding(int x, int y) {
		for(BuildingFinder b : buildings) {
			if(x >= b.xLeft && x < b.xRight && y >= b.yTop && y < b.yBottom)
				return b.name;
		}
		return null;
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
}
