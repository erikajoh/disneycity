package market.gui;

import market.CustomerAgent;
import market.WorkerAgent;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class MarketGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	MarketAnimationPanel animationPanel = new MarketAnimationPanel();


    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public MarketGui() {
        int WINDOWX = 400;
        int WINDOWY = 360;
    	
    	setBounds(WINDOWX/10, WINDOWY/12, WINDOWX, WINDOWY);
        setLayout(new GridLayout(2,1));
        add(animationPanel);
    }

    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        MarketGui gui = new MarketGui();
        gui.setTitle("le market");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
   
}
