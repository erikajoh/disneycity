package restaurant_haus.gui;


import restaurant_haus.CookAgent;
import restaurant_haus.CustomerAgent;
import restaurant_haus.WaiterAgent;
import simcity.gui.SimCityGui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CookGui implements Gui {
	
    private CookAgent agent = null;
    private SimCityGui gui;

    public final int size = 20;
    
    boolean atDestination = false;
    private int xPos = 350, yPos = -20;//default cook position
    private int xDestination = -20, yDestination = -20;//default start position
    private Point refrigerator = new Point(425-size, 290);
    private Point stove = new Point(425-size, 170);
    private Point plating = new Point(375, 200);
    private Point phone = new Point(375, 290);
    
    private enum STATE {CARRYING, STATIONARY};
    
    private class FoodGui {
    	boolean cooked;
    	STATE state;
    	String choice;
    	int table;
    	int xPos;
    	int yPos;
    	int size;
    	
    	FoodGui(int table, String choice) {
    		this.table = table;
    		this.choice = choice;
    		state = STATE.CARRYING;
    		cooked = false;
    		xPos = 425;
    		yPos = 290;
    		size = 20;
    	}
    }
    
    public void SpawnFood(int table, String choice) {
    	foods.add(new FoodGui(table, choice));
    }
    
    public void GrabFood(int table) {
    	synchronized(foods) {
    		for(FoodGui food : foods) {
    			if(food.table == table) {
    				food.state = STATE.CARRYING;
    				food.cooked = true;
    			}
    		}
    	}
    }
    
    public void LeaveFood(int table, boolean plating) {
    	synchronized(foods) {
    		for(FoodGui food : foods) {
    			if(food.table == table) {
    				food.state = STATE.STATIONARY;
    				if(plating){
    					food.xPos -= size*2;
    				}
    			}
    		}
    	}
    }
    
    public void deleteFood(int table) {
    	synchronized(foods) {
    		for(FoodGui food : foods) {
    			if(food.table == table) {
    				foods.remove(food);
    				return;
    			}
    		}
    	}
    }
    
    List<FoodGui> foods = Collections.synchronizedList(new ArrayList<FoodGui>());
    
    public CookGui(CookAgent agent, SimCityGui g) {
    	super();
        this.agent = agent;
        gui = g;
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination && !atDestination) {
           agent.msgAtDestination();
           atDestination = true;
        }
        
        synchronized(foods) {
        	for(FoodGui food : foods) {
        		if(food.state == STATE.CARRYING) {
        			food.xPos = xPos + size;
        			food.yPos = yPos;
        		}
        	}
        }
        
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(xPos, yPos, size, size);
        
        synchronized(foods) {
        	for(FoodGui food : foods) {
    			if(food.cooked) {
        				g.setColor(Color.MAGENTA);
    			}
    			else {
        				g.setColor(Color.PINK);
    			}
    			g.fillRect(food.xPos, food.yPos, food.size, food.size);
    			g.setColor(Color.BLACK);
            	g.drawString(food.choice.substring(0,2), food.xPos, food.yPos + (int)(food.size * 0.75));
        	}
        }
        /*
        if(state == STATE.FOOD) {
        	g.setColor(Color.MAGENTA);
        	g.fillRect(xPos, yPos + waiterSize, waiterSize, waiterSize);
        	g.setColor(Color.BLACK);
        	g.drawString(carrying, xPos, yPos + (int)(waiterSize * 1.5));
        }
        */
    }

    public boolean isPresent() {
        return true;
    }

    public void GoToRefrigerator() {
    	xDestination = refrigerator.x;
    	yDestination = refrigerator.y;
    	atDestination = false;
    }
    
    public void GoToStove() {
    	xDestination = stove.x;
    	yDestination = stove.y;
    	atDestination = false;
    }
    
    public void GoToPlating() {
    	xDestination = plating.x;
    	yDestination = plating.y;
    	atDestination = false;
    }
    
    public void GoToPhone() {
    	xDestination = phone.x;
    	yDestination = phone.y;
    	atDestination = false;
    }
    
    /*
    public void GoToTable(Point tableLocation) {
    	state = STATE.NOFOOD;
    	xDestination = (int)tableLocation.getX() + waiterSize;
        yDestination = (int)tableLocation.getY() - waiterSize;
        TablePosX = xDestination;
        TablePosY = yDestination;
    }
    
    public void CarryingFood(String food) {
    	carrying = food.substring(0,2);
    	state = STATE.FOOD;
    }

    public void DoLeaveCustomer() {
        xDestination = -waiterSize;
        yDestination = -waiterSize;
        state = STATE.RETURNING;
    }
	*/
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
