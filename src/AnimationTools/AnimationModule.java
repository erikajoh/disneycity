package AnimationTools;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/* TO USE THIS MODULE
 * Everything is pretty much implemented. You just need to tell this module when to change animations
 * and if the animation has a different frame delay. Otherwise the only change is in the draw function of your gui.
 * 
 * The constructor for the module takes a mix of parameters.
 * String character: The name of the sprite sheet used. Also see the setCharacter(String character) utility
 * String firstAnimation: The name of the first animation to return. Also see setAnimation(String animation) and setAnimation(String animation, int frameDelay)
 * int frameDelay: The higher this is, the longer a frame plays. Also see the setDelay(int delay) utility
 * 
 * The Following two lines must be within your draw function with whatever name you give the AnimationModule (named animModule in example)
 * animModule.updateAnimation();//updates the frame and animation 
 * g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
 * 
 * You can use whatever you want for determining the xPos and yPos, the null is there for something unnecessary. Just ignore it.
 */


public class AnimationModule {
	//ALL SPRITES AS PNGS PLEASE

	public String character = "";//This is the name of the folder that the agent's sprites will be pulled from
	public String animation = "";//Each animation is split into its own folder for ease of use
	//YOU MUST SET A DEFAULT ANIMATION HERE OR THE PROGRAM MAY FREAK OUT AT NOT HAVING A CORRENT FILEPATHWAY

	public String previousAnimation = "";//Determines whether changing image is needed automatically so people don't slide sideways
	public int currentFrame = 1;//Frame counts start at 1. Make sure that the files are named with no whitespace
	public int totalFrames = 0;//Set by the class. Don't worry about it.
	public int frameDelay;//You might need to set this yourself based on how fast you want your animation to play. 60 is the default.
	//makes it so that the animation doesn't play at ludicrous speeds. Depending on the character, you may have to find a way to change this for your implementation
	//I would recommend using a map to map animation names to frameDelays and possibly totalFrames
	public int frameDelayCounter = 0;//Don't worry about this. Animation variable
	public String filePathway;//Stores the file pathway to the image
	public BufferedImage img = null;//This is the image that gets drawn
	public boolean moving = true;

	public AnimationModule(String character, String firstAnimation, int frameDelay) {//Give it all the information you can
		this.character = character;
		animation = firstAnimation;
		this.frameDelay = frameDelay;
	}

	public AnimationModule(String character, String firstAnimation){//Defaults the frame delay to 60. Which should work for most sprites. 
		this.character = character;
		animation = firstAnimation;
		this.frameDelay = 30;
	}
	
	public AnimationModule(String character){//Defaults the animation to walking downward (the sprite looks into your soul).
		this.character = character;
		animation = "WalkDown";
		this.frameDelay = 30;
	}
	
	public AnimationModule() {//This is where you don't care at all and you get the Edgar sprite
		this.character = "Edgar";
		animation = "WalkDown";
		this.frameDelay = 30;
	}

	public void updateAnimation() {
		if(animation != previousAnimation) {//New animation gets called
			totalFrames = findFrameCount();
			currentFrame = 1;
		}
		else {
			//Frame updates
			if(frameDelayCounter >= frameDelay && moving) {//UpdateFrame
				currentFrame ++;
				if(currentFrame > totalFrames)
					currentFrame = 1;
				frameDelayCounter = 0;
			}
			else {//Wait and do nothing
				frameDelayCounter++;
			}
		}
		String filePathway = "src" + File.separator + "res" + File.separator + character + File.separator + animation + File.separator + String.valueOf(currentFrame) + ".png";
		//System.out.println(filePathway + ": being drawn.");

		try {
			img = ImageIO.read(new File(filePathway));
		}
		catch (IOException e) {
		}

		previousAnimation = animation;
	}

	private int findFrameCount() {//returns the number of frames in the current animation
		File f = new File("src" + File.separator + "res" + File.separator + character + File.separator + animation + File.separator);
		//System.out.println("src" + File.separator + "res" + File.separator + character + File.separator + animation + File.separator);
		/*try {
			System.out.println("Current dir : " + f.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		int count = 0;
		for (File file : f.listFiles()) {
			if (file.isFile()) {
				count++;
			}
		}
		// System.out.println("Found frames: " + String.valueOf(count));
		return count;
	}

	public void changeAnimation(String animation) {
		this.animation = animation;
		setMoving();
	}
	
	public boolean changeFrame(int frame) {
		if(frame > totalFrames) {
			System.out.println("Called frame that doesn't exist in animation, total frames is " +totalFrames);
			return false;
		}
		currentFrame = frame;
		frameDelayCounter = 0;
		return true;
	}
	
	public void changeAnimation(String animation, int frameDelay) {//if an individual animation has a different frame delay
		this.animation = animation;
		this.frameDelay = frameDelay;
		setMoving();
	}
	
	public void setDelay(int delay) {
		frameDelay = delay;
	}
	
	public void setCharacter(String character) {
		this.character = character;
	}
	
	public void setStill() {
		moving = false;
		currentFrame = 1;
	}
	
	public void setMoving() {
		moving = true;
	}
	
	public Image getImage(){
		return img;
	}
	
	public String getAnimation() {
		return animation;
	}
	
	public boolean getLastFrame() {
		return (currentFrame == totalFrames);
	}
}
