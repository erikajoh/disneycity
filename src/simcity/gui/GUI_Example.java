package simcity.gui;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GUI_Example implements Gui {
	
	String character = "";//This is the name of the folder that the agent's sprites will be pulled from
	String animation = "";//Each animation is split into its own folder for ease of use
	//YOU MUST SET A DEFAULT ANIMATION HERE OR THE PROGRAM MAY FREAK OUT AT NOT HAVING A CORRENT FILEPATHWAY
	
	String previousAnimation = "";
	int currentFrame = 1;//Frame counts start at 1. Make sure that the files are named with no whitespace
	int totalFrames = 0;
	int frameDelay = 6;//makes it so that the animation doesn't play at ludicrous speeds. Depending on the character, you may have to find a way to change this for your implementation
	int frameDelayCounter = 0;
	
	String filePathway;
	BufferedImage img = null;//This is the image that gets drawn

    private float xPos = -20.0f, yPos = -20.0f;//default position, changed to floats for greater precision.
    private float xDestination = -20.0f, yDestination = -20.0f;//default destination, changed to floats for greater precision.
	private float xSpeed = 1.0f, ySpeed = 1.0f;//if you have agents with different speeds

    //SIZE variable removed, although most sprites for agents are 24x24.
    
    public SimCityGui SCGui;

    public GUI_Example(/*Pointer to Agent,*/ SimCityGui gui, String character) {//the character determines what set of animations an instance of the GUI will use. Should be set here or in a different constructor
		this.SCGui = gui;
		this.character = character;
    }
    
    public GUI_Example(/*Pointer to Agent,*/ SimCityGui gui) {//for when you know the character already. Like if a waiter has a uniform or something
  		this.SCGui = gui;
  		this.character = "blah";//Replace blah with whatever you feel like
      }

    public void updatePosition() {//This may be different depending on how your agent moves
        if (xPos < xDestination)
            xPos += xSpeed;
        else if (xPos > xDestination)
            xPos -= xSpeed;

        if (yPos < yDestination)
            yPos+= ySpeed;
        else if (yPos > yDestination)
            yPos -= ySpeed;
		
		//NEEDED: SOME LOGIC FOR DETERMINING ANIMATION CHANGES. USUALLY FOR DIRECTION OF WALKING
    }

    public void draw(Graphics2D g) {
    	/*IMPORTANT ANIMATION CODE CHANGES*/
    	
    	updateAnimation();//updates the frame and animation 
    	g.drawImage(img, (int)xPos, (int)yPos, null);
        
    }

    public boolean isPresent() {
        return true;
    }

    private void updateAnimation() {
    	if(animation != previousAnimation) {//New animation gets called
    		totalFrames = findFrameCount();
    		currentFrame = 1;
    	}
    	else {
    		//Frame updates
    		if(frameDelayCounter == frameDelay) {//UpdateFrame
    			currentFrame ++;
    			if(currentFrame > totalFrames)
    				currentFrame = 1;
    			frameDelayCounter = 0;
    		}
    		else {//Wait and do nothing
    			frameDelayCounter++;
    		}
    	}
    	String filePathway = ".." + File.separator + character + File.separator + animation + File.separator + String.valueOf(currentFrame);
    	System.out.println(filePathway + ": being drawn.");
    	
    	try {
    	    img = ImageIO.read(new File(filePathway));
    	}
    	catch (IOException e) {
    	}
    	
    	previousAnimation = animation;
    }
    
    private int findFrameCount() {//returns the number of frames in the current animation
    	File f = new File(".." + File.separator + character + File.separator + animation);
        int count = 0;
        for (File file : f.listFiles()) {
                if (file.isFile()) {
                        count++;
                }
        }
        return count;
    }

    public float getXPos() {
        return xPos;
    }

    public float getYPos() {
        return yPos;
    }

    public void changeAnimation(String animation) {
    	this.animation = animation;
    }
}
