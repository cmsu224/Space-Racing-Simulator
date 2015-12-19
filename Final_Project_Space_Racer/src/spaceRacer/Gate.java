/*****
 * Gate.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Gate class for managing gate information
 *****/

package spaceRacer;

import com.jogamp.opengl.GL2;

public class Gate  {
	final private int GATE_SIZE = 5;	// this size
	final private int SHIP_SIZE = 5;	// a ship's size
 	private Sphere sphere;				// a Sphere to represent the gate
	private Tuple location; 			// this location
	private int texture = 0;			// this texture (to pass off)
	private int disabled = 0;			// whether this is disabled

	// constructor - takes an origin point
	public Gate(double x, double y, double z) {
		location = new Tuple(x, y, z);
		disabled = 0;
		sphere = new Sphere(GATE_SIZE, location);
	} // Gate()
		
	// draw function
	public void draw(GL2 gl) {
		if(disabled > 0) {
			return;
		}
				
		sphere.draw(gl, texture);

	} // draw()
		
	// takes bound texture ID and associates it with this
	public void setTexture(int set_texture) {
		texture = set_texture;
	} // setTexture()
	
	// checks this for collisions with a point
	public boolean checkCollisions(Tuple camera) {
		if(disabled > 0) return false;
		
		if(location.distance(camera) - GATE_SIZE < SHIP_SIZE) {
			disabled = 1;
			return true;
		}
		return false;
	} // checkCollisions()
	
	// returns whether this is invisible
	public boolean isInvisible() {
		if(disabled > 0) return true;
		return false;
	} // isInvisible()

	// fills in a Tuple object with this location
	public void getLocation(Tuple request) {
		request.set(location.x(), location.y(), location.z());
	} // getLocation()
	
	// sets this as either visible or invisible
	public void setInvisible(boolean visibility) {
		if(visibility) {
			disabled = 1;
		} else {
			disabled = 0;
		}
	} // setInvisible()
	
} // class
