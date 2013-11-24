package simcity.gui;

import housing.Housing;
import housing.ResidentAgent.State;
import housing.gui.HousingAnimationPanel;
import housing.test.mock.LoggedEvent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import restaurant_rancho.gui.RanchoAnimationPanel;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.gui.RestaurantRancho;
import simcity.PersonAgent;


public class SimCityGui extends JFrame implements ActionListener  {

	public static final int WINDOWX = 800;
	public static final int WINDOWY = 600;
	
	String name;
	
	SimCityPanel simCityPanel;
	
	JPanel cards;
	
	enum Panel {housing, restaurant, market, bank};
	Panel currP;
			
	public static RestaurantRancho restRancho;
	public RanchoAnimationPanel ranchoAniPanel = new RanchoAnimationPanel();
	
	public static Housing hauntedMansion;
	public HousingAnimationPanel housAniPanel = new HousingAnimationPanel();
		
	CityAnimationPanel cityAniPanel = new CityAnimationPanel();
	private JPanel cityBanner = new JPanel();
	private JPanel zoomBanner = new JPanel();
	private JPanel cityAnimation = new JPanel();
	private JPanel zoomAnimation = new JPanel();
	private JPanel cityPanel = new JPanel();
	private JPanel zoomPanel = new JPanel();
	private JLabel cityLabel = new JLabel("Disney City View");
	private JLabel zoomLabel = new JLabel("Click on a Building to see Animation Inside");
	private JButton panelB = new JButton("next panel");
		
	public SimCityGui(String name) {
		
//		int delay = 1000; //milliseconds
//		  ActionListener taskPerformer = new ActionListener() {
//		      public void actionPerformed(ActionEvent evt) {
////		          housAniPanel.update();
//		      }
//		  };
//		  new Timer(delay, taskPerformer).start();
				
		cards = new JPanel(new CardLayout());
		cards.add(housAniPanel, "Housing");
		cards.add(ranchoAniPanel, "Restaurant");
				
		panelB.addActionListener(this);
		panelB.setPreferredSize(new Dimension(0, 0));
		currP = Panel.housing;
					
		// Restaurants etc. must be created before simCityPanel is constructed, as demonstrated below
		restRancho = new RestaurantRancho(this, "Rancho Del Zocalo");
		
		simCityPanel = new SimCityPanel(this);
		
		hauntedMansion = new Housing(this, "Haunted Mansion");
		
		setLayout(new GridBagLayout());
		setBounds(WINDOWX/20, WINDOWX/20, WINDOWX, WINDOWY);
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.BOTH;
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = .5;
		c1.gridx = 0;
		c1.gridy = 0; 
		c1.gridwidth = 3;
		cityBanner.setBorder(BorderFactory.createTitledBorder("City Banner"));
		add(cityBanner, c1);
		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.BOTH;
		c3.weightx = .5; 
		c3.gridx = 5;
		c3.gridy = 0;
		c3.gridwidth = 5;
		zoomBanner.setBorder(BorderFactory.createTitledBorder("Zoom Banner"));
		add(zoomBanner, c3);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.weightx = .5;
		c2.weighty = .32;
		c2.gridx = 0;
		c2.gridy = 1;
		c2.gridwidth = 3;
		c2.gridheight = 3;
		cityAnimation.setLayout(new BoxLayout(cityAnimation, BoxLayout.Y_AXIS));
		cityAnimation.add(cityAniPanel);
		//cityAnimation.setBorder(BorderFactory.createTitledBorder("City Animation"));
		add(cityAnimation, c2);
		GridBagConstraints c4 = new GridBagConstraints();
		c4.fill = GridBagConstraints.BOTH;
		c4.weightx = .5;
		c4.weighty=.32;
		c4.gridx = 3;
		c4.gridy=1;
		c4.gridwidth = GridBagConstraints.REMAINDER;
		c4.gridheight = 3;
		zoomAnimation.setLayout(new BoxLayout(zoomAnimation, BoxLayout.Y_AXIS));
		zoomAnimation.add(cards);
//		ranchoAniPanel.setVisible(false);
//		zoomAnimation.add(housAniPanel);
		//zoomAnimation.setBorder(BorderFactory.createTitledBorder("Zoom Animation"));
		add(zoomAnimation, c4);
		GridBagConstraints c5 = new GridBagConstraints();
		c5.fill = GridBagConstraints.BOTH;
		c5.weightx= .5;
		c5.weighty = .18;
		c5.gridx=0;
		c5.gridy=5;
		c5.gridwidth = 3;
		c5.gridheight = 2;
		cityPanel.setBorder(BorderFactory.createTitledBorder("City Panel"));
		add(cityPanel, c5);
		GridBagConstraints c6 = new GridBagConstraints();
		c6.fill = GridBagConstraints.BOTH;
		c6.weightx= 0;
		c6.weighty = .18;
		c6.gridx=3;
		c6.gridy=5;
		c6.gridwidth = GridBagConstraints.REMAINDER;
		c6.gridheight =2;
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom Panel"));
		add(panelB, c6);			
	}
	
	public static void main(String[] args) {
		SimCityGui gui = new SimCityGui("Sim City Disneyland");
		gui.setTitle("SimCity Disneyland");
		gui.setVisible(true);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		restRancho.addPerson(null, "Cook", "cook", 50);
        restRancho.addPerson(null, "Waiter", "w", 50);
        restRancho.addPerson(null, "Cashier", "cash", 50);
        restRancho.addPerson(null, "Market", "Trader Joes", 50);
        restRancho.addPerson(null, "Host", "Host", 50);
		restRancho.addPerson(null, "Customer", "Sally", 50);
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == panelB) {
			CardLayout cl = (CardLayout)(cards.getLayout());			
			if (currP == Panel.restaurant) {
				System.out.println("showing housing");
				cl.show(cards, "Housing");
				currP = Panel.housing;
			} else if (currP == Panel.housing) {
				System.out.println("showing rest");
				cl.show(cards, "Restaurant");
				currP = Panel.restaurant;
			}
		}
	}
	
}
