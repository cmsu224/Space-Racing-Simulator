/*****
 * Asteroid.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Asteroid class for managing asteroid information
 *****/

package spaceRacer;

import java.util.Random;
import com.jogamp.opengl.GL2;

public class Asteroid  {
	final private int size;				// size (radius)
	final private int SHIP_SIZE = 5;	// size of the camera
	final private int FIELD_SIZE = 400;	// bounce back if too far
	final private int MAX_SPEED = 4;	// max speed of random speed
 	private Sphere sphere;				// Sphere for drawing
	private Random randomNumber;		// random number generator
	private Tuple location; 			// location tuple 
	private Tuple movement;				// movement vector
	private int texture = 0;			// texture ID
	private int disabled = 0;			// enabled = 0 or explosion level
	private int explodeTime = 30;		// explosion time in frames

	public Asteroid() {
		// create, seed random number
		randomNumber = new Random();

		// random location
		location = new Tuple(
				(randomNumber.nextDouble() - 0.5) * FIELD_SIZE, 
				(randomNumber.nextDouble() - 0.5) * FIELD_SIZE, 
				(randomNumber.nextDouble() - 0.5) * FIELD_SIZE);

		// random movement vector
		movement = new Tuple(
				(randomNumber.nextDouble() - 0.5) * MAX_SPEED,
				(randomNumber.nextDouble() - 0.5) * MAX_SPEED, 
				(randomNumber.nextDouble() - 0.5) * MAX_SPEED);
		
		// random radius
		size = randomNumber.nextInt() % 20 + 1;
		
		// create Sphere
		sphere = new Sphere(size, location);

	}

	// draw the sphere - don't if it's done exploding
	public void draw(GL2 gl) {
		if (disabled > explodeTime) {
			return;
		}
		sphere.draw(gl, texture);
	}
	

	// continue exploding this or move it according to vector
	public void animate() {
		
		// already exploded
		if (disabled > explodeTime) {
			return;
		} // if

		// exploding now
		if (disabled > 0) {
			sphere.explode();
			disabled++;
			return;
		} // if
		
		// move sphere and this
		location.add(movement);
		sphere.move(movement);
		
		// bounce back if too far out
		if(location.distance(new Tuple(0, 0, 0)) > FIELD_SIZE) {
			movement.scale(-1, -1, -1);
		} // if
	} // animate()
	
	// get texture from outside
	public void setTexture(int set_texture) {
		texture = set_texture;
	} // setTexture()
	
	// check collisions against camera
	public boolean checkCollisions(Tuple camera) {

		// already exploded
		if(disabled > 0) {
			return false;
		} // if
		
		// collision detected
		if(location.distance(camera) - size < SHIP_SIZE) {
			disabled = 1;
			return true;
		} // if
		return false;
	} // checkCollisions()

}
