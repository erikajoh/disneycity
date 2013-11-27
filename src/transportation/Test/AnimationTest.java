package transportation.Test;

import AnimationTools.AnimationModule;
import junit.framework.*;

public class AnimationTest extends TestCase {
	//these are instantiated for each test separately via the setUp() method.
	AnimationModule animModule1, animModule2, animModule3, animModule4;
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		animModule1 = new AnimationModule("Waiter", "WalkUp", 10);
		animModule2 = new AnimationModule("Chocobus", "Left");
		animModule3 = new AnimationModule("Relm");
		animModule4 = new AnimationModule();
	}	

	//Tests 4 different types of contructors to ensure variables are being set properly
	public void testConstructors(){
		//animModule1
		assertTrue("First Animation Module should have a character of \"Waiter\". Instead it is: " + animModule1.character, animModule1.character.equals("Waiter"));
		assertTrue("First Animation Module should have an animation of \"WalkUp\". Instead it is: " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertEquals("First Animation Module should have a frame delay of 10. Intead it is: " + String.valueOf(animModule1.frameDelay), animModule1.frameDelay, 10);

		//animModule2
		assertTrue("Second Animation Module should have a character of \"Chocobus\". Instead it is: " + animModule2.character, animModule2.character.equals("Chocobus"));
		assertTrue("Second Animation Module should have an animation of \"Left\". Instead it is: " + animModule2.animation, animModule2.animation.equals("Left"));
		assertEquals("Second Animation Module should have a frame delay of 30. Intead it is: " + String.valueOf(animModule2.frameDelay), animModule2.frameDelay, 30);

		//animModule3
		assertTrue("Third Animation Module should have a character of \"Relm\". Instead it is: " + animModule3.character, animModule3.character.equals("Relm"));
		assertTrue("Third Animation Module should have an animation of \"WalkDown\". Instead it is: " + animModule3.animation, animModule3.animation.equals("WalkDown"));
		assertEquals("Third Animation Module should have a frame delay of 30. Intead it is: " + String.valueOf(animModule3.frameDelay), animModule3.frameDelay, 30);

		//animModule4
		assertTrue("Fourth Animation Module should have a character of \"Edgar\". Instead it is: " + animModule4.character, animModule4.character.equals("Edgar"));
		assertTrue("Fourth Animation Module should have an animation of \"WalkDown\". Instead it is: " + animModule4.animation, animModule4.animation.equals("WalkDown"));
		assertEquals("Fourth Animation Module should have a frame delay of 30. Intead it is: " + String.valueOf(animModule4.frameDelay), animModule4.frameDelay, 30);
	}

	//Tests the find Frame Count utility
	public void testFrameCountFinder() {
		assertEquals("First Animation Module should find 4 frames. Instead it finds " + String.valueOf(animModule1.findFrameCount()), animModule1.findFrameCount(), 4);

		assertEquals("Second Animation Module should find 8 frames. Instead it finds " + String.valueOf(animModule1.findFrameCount()), animModule2.findFrameCount(), 8);
	}

	//Tests updateAnimation Utility for a couple changes of frame and then a change of animation
	public void testUpdateAnimationChange() {
		//Pre Conditions
		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be null. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals(""));

		animModule1.updateAnimation();//previous animation should change

		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);

		animModule1.changeAnimation("WalkDown");

		assertTrue("Current Animation should be WalkDown. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkDown"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);

		animModule1.updateAnimation();
		assertTrue("Current Animation should be WalkDown. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkDown"));
		assertTrue("Previous Animation should be WalkDown. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkDown"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);

		//Now the frames should start changing
		for(int i = 1; i <= 10; i++) {
			animModule1.updateAnimation();

			assertTrue("Current Animation should be WalkDown. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkDown"));
			assertTrue("Previous Animation should be WalkDown. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkDown"));

			assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
			assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
			assertEquals("Frame Delay counter should be " + String.valueOf(i) + "Instead it is " + String.valueOf(animModule1.frameDelayCounter), i, animModule1.frameDelayCounter);
		}

		//Now the frame should change
		animModule1.updateAnimation();

		assertTrue("Current Animation should be WalkDown. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkDown"));
		assertTrue("Previous Animation should be WalkDown. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkDown"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 2. Instead it is " + String.valueOf(animModule1.currentFrame), 2, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);
	}

	//Testing to ensure that the frame gets reset after changing animation
	public void testChangeAnimationAfterAFewFrames() {
		//Pre Conditions
		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be null. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals(""));

		animModule1.updateAnimation();//previous animation should change

		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);

		for(int i = 1; i <= 27; i++) {//2 frames and delay counter of 5 (delay counter resets on 2 of the updateAnimation calls
			animModule1.updateAnimation();
		}

		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 3. Instead it is " + String.valueOf(animModule1.currentFrame), 3, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 5. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 5, animModule1.frameDelayCounter);

		animModule1.changeAnimation("WalkRight");

		assertTrue("Current Animation should be WalkRight. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkRight"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		animModule1.updateAnimation();

		assertTrue("Current Animation should be WalkRight. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkRight"));
		assertTrue("Previous Animation should be WalkRight. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkRight"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 5. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 5, animModule1.frameDelayCounter);
	}

	public void testResetFrames() {//Loops frame back to 1 after all frames have been gone though
		//Pre Conditions
		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be null. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals(""));
		
		animModule1.updateAnimation();//previous animation should change

		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);
		
		for(int i = 1; i <= 43; i++) {//Should be one update away from resetting frame
			animModule1.updateAnimation();
		}
		
		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 4. Instead it is " + String.valueOf(animModule1.currentFrame), 4, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 10. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 10, animModule1.frameDelayCounter);
		
		animModule1.updateAnimation();//reset the frame to 1
		assertTrue("Current Animation should be WalkUp. Instead it is " + animModule1.animation, animModule1.animation.equals("WalkUp"));
		assertTrue("Previous Animation should be WalkUp. Instead it is " + animModule1.previousAnimation, animModule1.previousAnimation.equals("WalkUp"));

		assertEquals("Frame Delay should be 10. Instead it is " + String.valueOf(animModule1.frameDelay), 10, animModule1.frameDelay);
		assertEquals("Frame should be 1. Instead it is " + String.valueOf(animModule1.currentFrame), 1, animModule1.currentFrame);
		assertEquals("Frame Delay counter should be 0. Instead it is " + String.valueOf(animModule1.frameDelayCounter), 0, animModule1.frameDelayCounter);
	}
}
