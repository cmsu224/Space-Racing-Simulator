/*****
 * ReverseBox.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Derived Box class with specific functions for the skybox star field
 *****/

package spaceRacer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class ReverseBox extends Box {
	private CameraTracker cambot;		// the camera for this

	// constructor
	public ReverseBox(double orig_x, double orig_y, double orig_z, 
			double new_height) {
		super(orig_x, orig_y, orig_z, new_height);
		cambot = new CameraTracker(0, 0, 0,
				0, 0, 1,
				0, 1, 0);
	} // ReverseBox()

	// overloaded draw function to handle the special cubemap image we're using
	protected void draw(GL2 gl, GLU glu, int texture) {

		// index of vertices - instructions for drawing the quads
		int[] verIndex = {
				0, 1, 2, 3,	// front
				0, 4, 7, 3,
				1, 5, 6, 2,
				5, 4, 7, 6,	// back
				2, 3, 7, 6,	// top
				0, 1, 5, 4	// bottom
		};
		
		// index of texture coordinates - corresponding to vertices for
		//   texture binding
		int[] texIndex = {
				20, 18, 8, 10,	// front
				20, 22, 12, 10,
				18, 16, 6, 8,
				16, 14, 4, 6,	// back
				8, 10, 2, 0,	// top
				20, 18, 24, 26	// bottom
				
		};
		
		// texture coordinates for texture binding
		int[] tVertices = {
				2, 0,	// 0 
				3, 0,	// 2
				0, 1,	// 4
				1, 1,	// 6
				2, 1,	// 8
				3, 1,	// 10
				4, 1,	// 12
				0, 2,	// 14
				1, 2,	// 16
				2, 2,	// 18
				3, 2,	// 20
				4, 2,	// 22
				2, 3,	// 24
				3, 3	// 26
		};
		
		// set up perspective
        gl.glPushMatrix();
        cambot.lookAt(glu);

        // set up texture and begin drawing
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl.glBegin(GL2.GL_QUADS);

		// using the indices, plot the vertices and apply textures
		for(int i = 0; i < verIndex.length-3; i += 4) {
	        gl.glTexCoord2f(tVertices[texIndex[i]]/4f, tVertices[texIndex[i]+1]/3f);
	        gl.glVertex3d(
	        		vertices[verIndex[i]].x(),
	        		vertices[verIndex[i]].y(),
	        		vertices[verIndex[i]].z());

	        gl.glTexCoord2f(tVertices[texIndex[i+1]]/4f, tVertices[texIndex[i+1]+1]/3f);
	        gl.glVertex3d(
	        		vertices[verIndex[i+1]].x(),
	        		vertices[verIndex[i+1]].y(),
	        		vertices[verIndex[i+1]].z());

	        gl.glTexCoord2f(tVertices[texIndex[i+2]]/4f, tVertices[texIndex[i+2]+1]/3f);
	        gl.glVertex3d(
	        		vertices[verIndex[i+2]].x(),
	        		vertices[verIndex[i+2]].y(),
	        		vertices[verIndex[i+2]].z());

	        gl.glTexCoord2f(tVertices[texIndex[i+3]]/4f, tVertices[texIndex[i+2]+1]/3f);
	        gl.glVertex3d(
	        		vertices[verIndex[i+3]].x(),
	        		vertices[verIndex[i+3]].y(),
	        		vertices[verIndex[i+3]].z());

		}

		// close up
		gl.glEnd();
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		gl.glPopMatrix();
	} // draw()
	
	public void setTarget(double targetx, double targety, double targetz) {
		cambot.setTarget(targetx, targety, targetz);
	} // setTarget()
}
