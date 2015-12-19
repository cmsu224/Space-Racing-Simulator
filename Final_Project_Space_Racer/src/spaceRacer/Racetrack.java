/*****
 * Racetrack.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Racetrack class to work for the simulation and store local data
 *****/

package spaceRacer;

import com.jogamp.opengl.GL2;

public class Racetrack {
	final private int NUMBER_OF_GATES = 10;
	final private int TRACK_RADIUS = 100;
	private int activeGates = NUMBER_OF_GATES;
	private Gate[] gates;
	private boolean finished = false;
	
	final private int NUMBER_OF_ASTEROIDS = 50;
	private Asteroid[] asteroids;
	final private int TEXTURE_GATE = 3;
	private Timer timer;
	boolean freeView = false;
	boolean replayMode = false;
	long lastTime = 0;
	
	public Racetrack(int[] textures) {
		gates = new Gate[NUMBER_OF_GATES];
		
		// construct the Racetrack - a circle with the origin at
		//   (TRACK_RADIUS, 0, 0)
		for(int i = 0; i < NUMBER_OF_GATES - 1; i++) {
			double x = Math.cos(2 * Math.PI / (NUMBER_OF_GATES) * (i + 1))
					* TRACK_RADIUS - TRACK_RADIUS;
			double y = 0;
			double z = Math.sin(2 * Math.PI / (NUMBER_OF_GATES) * (i + 1))
					* TRACK_RADIUS;
			gates[i] = new Gate(x, y, z);
			gates[i].setTexture(textures[TEXTURE_GATE]);
		}
		
		// put in the last gate and set it invisible until the other gates
		//   are done
		gates[NUMBER_OF_GATES - 1] = new Gate(0, 0, 0);
		gates[NUMBER_OF_GATES - 1].setTexture(textures[TEXTURE_GATE]);
		gates[NUMBER_OF_GATES - 1].setInvisible(true);
		
		// put in the asteroids
		asteroids = new Asteroid[NUMBER_OF_ASTEROIDS];
		for(int i = 0; i < NUMBER_OF_ASTEROIDS; i++) {
			asteroids[i] = new Asteroid();
			asteroids[i].setTexture(textures[i%7 + 7]);
		}
		
		// initialize the Timer
		timer = new Timer();
	} // Racetrack()
		
	// check for collisions with the camera (ship) and react properly
	public void checkCollisions(CameraTracker camera) {
		// if we're in the freeView mode, don't check
		if(freeView) {
			return;
		}
		
		// set cameraPosition to the camera's coordinates 
		Tuple cameraPosition = new Tuple();
		camera.position(cameraPosition);
		// check each gate for a collision
		for(int i = 0; i < NUMBER_OF_GATES; i++) {
			// if we have a collision with a gate, make it invisible and
			//   decrement the number of gates
			if(gates[i].checkCollisions(cameraPosition)) {
				activeGates--;
				// if we're on the last one (the finish), reveal it
				if(activeGates == 1) {
					gates[NUMBER_OF_GATES - 1].setInvisible(false);
				// if we have no more gates, game over
				} else if (activeGates == 0) {
					finished = true;
					timer.stop();
					lastTime = timer.time();
					timer.reset();
					// reveal all gates except the final
					//   for the fly through
					activeGates = NUMBER_OF_GATES;
					for(int j = 0; j < NUMBER_OF_GATES - 1; j++) {
						gates[j].setInvisible(false);
					}
					gates[NUMBER_OF_GATES - 1].setInvisible(true);
					return;
				} // if
			} // if
		} // for
		
		// check all asteroids for collisions
		for(int i = 0; i < NUMBER_OF_ASTEROIDS; i++) {
			// while we're in here, move the asteroid to its next position
			asteroids[i].animate();
			// check for collisions if we're not in replay mode
			if(!replayMode) {
				if(asteroids[i].checkCollisions(cameraPosition)) {
					System.out.println("You lost!");
					// reveal all gates except the final
					//   for the fly through
					activeGates = NUMBER_OF_GATES;
					for(int j = 0; j < NUMBER_OF_GATES - 1; j++) {
						gates[j].setInvisible(false);
					}
					gates[NUMBER_OF_GATES - 1].setInvisible(true);

					finished = true;
				} // if
			} // if
		} // for
		
	} // checkCollisions()
	
	// check on whether a shot position has a collision 
	public boolean checkShot(Tuple laser) {
		// don't do anything in replay mode
		if(replayMode) {
			return false;
		} // check every asteroid
		for(int i = 0; i < NUMBER_OF_ASTEROIDS; i++) {
			if(asteroids[i].checkCollisions(laser)) {
				System.out.println("Hit one!");
				return true;
			} // if
		} // for

		// haven't found a collision yet, it ain't there
		return false;
	} // checkShot
	
	// draw call to draw the course, gates and asteroids
	public void draw(GL2 gl) {
		// draw each gate
		for(int i = 0; i < NUMBER_OF_GATES; i++) {
			gates[i].draw(gl);
		} // for
		
		// if we're not in replay, draw each asteroid
		if(!replayMode) {
			for(int i = 0; i < NUMBER_OF_ASTEROIDS; i++) {
				asteroids[i].draw(gl);			
			} // if
		} // if
	} // draw()
	
	// enable freeView mode
	public void enableFreeView(boolean enable) {
		// if we're already there, we're done
		if(replayMode) {
			return;
		} // if

		// toggle the variable, stop or restart the timer
		freeView = enable;
		if(freeView) {
			timer.stop();
			System.out.println("Enabling free view");			
		} else {
			timer.start();
			System.out.println("Disabling free view");
		} // if
	} // enableFreeView()

	// returns the amount of gates this has left
	public int gatesLeft() {
		return activeGates;
	} // gatesLeft()

	// returns whether the game for this is over
	public boolean gameOver() {
		return finished;
	} // gameOver()
		
	// get the last successful time
	public long getLastTime() {
		return lastTime / 1000;
	} // getLastTime()
	
} // class
