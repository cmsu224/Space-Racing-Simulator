/*****
 * Tuple.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Convenient way to store (x, y, z) coordinates and do basic and vector math
 *   on them
 *****/

package spaceRacer;

public class Tuple {
	double x_coord,		// x in Tuple (x, y, z)
		y_coord,		// y in Tuple (x, y, z)
		z_coord;		// z in Tuple (x, y, z)
	
	// default constructor - set all to 0
	public Tuple() {
		set(0, 0, 0);
	}
			
	// constructor - set internal vars based on parameters
	public Tuple(double set_x, double set_y, double set_z) {
		set(set_x, set_y, set_z);
	}
	
	// set value based on 3 parameters
	public void set(double new_x, double new_y, double new_z) {
		x_coord = new_x;
		y_coord = new_y;
		z_coord = new_z;
	}

	// set value based on another Tuple
	public void set(Tuple that) {
		this.x_coord = that.x_coord;
		this.y_coord = that.y_coord;
		this.z_coord = that.z_coord;
	}
	
	// return x coordinate
	public double x() {
		return x_coord;
	}

	// return y coordinate
	public double y() {
		return y_coord;
	}

	// return y coordinate	
	public double z() {
		return z_coord;
	}

	// sets x, y, and z values based on two doubles representing angles
	//   -- used for object and camera movement
	public void mouseRotate(double rot_x, double rot_y) {
		x_coord = Math.sin(rot_x);
		y_coord = Math.sin(rot_y);
		z_coord = Math.cos(rot_x) * Math.cos(rot_y);
	} // mouseRotate()
	
	// scale - multiply elements by scalar values
	public void scale(double scale_x, double scale_y, double scale_z) {
		x_coord *= scale_x;
		y_coord *= scale_y;
		z_coord *= scale_z;
	} // scale()
	
	// add coordinates with parameter values
	public void add(double add_x, double add_y, double add_z) {
		x_coord = x_coord + add_x;
		y_coord = y_coord + add_y;
		z_coord = z_coord + add_z;		
	} // add()
	
	// add this with that and store in this
	public void add(Tuple that) {
		this.x_coord = this.x_coord + that.x_coord;
		this.y_coord = this.y_coord + that.y_coord;
		this.z_coord = this.z_coord + that.z_coord;		
	} // add()
	
	// returns distance between this and parameter
	public double distance(Tuple that) {
		return Math.sqrt(
				Math.pow(that.x() - this.x(), 2) +
				Math.pow(that.y() - this.y(), 2) +
				Math.pow(that.z() - this.z(), 2));
	} // distance()
	
	// normalize a vector - create a unit vector
	public void normalize() {
		double magnitude = distance(new Tuple(0, 0, 0));
		x_coord = x_coord / magnitude;
		y_coord = y_coord / magnitude;
		z_coord = z_coord / magnitude;
	} // normalize()

	// return string of this contents for debugging
	public String toString() {
		return "(" + x_coord + ", " + y_coord + ", " + z_coord + ")";
	} // toString()
	
} // class