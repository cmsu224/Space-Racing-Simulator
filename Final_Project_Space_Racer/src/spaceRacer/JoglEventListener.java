/*****
 * JoglEventListener.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Listener for events to drive the program
 *****/

package spaceRacer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.jogamp.opengl.*;

// JoglEventListener class - handles most of the user input and 
//   graphic capabilities
public class JoglEventListener implements GLEventListener, 
		KeyListener, MouseListener, MouseMotionListener {
	
	// JoglEventListener Class variables // 

	// GLU for GLU calls
	private MouseTracker mouseTracker;		// to track mouse movements
	private Simulator sim;					// to handle simulation display

	/////////////////////
	// Class functions // 
	/////////////////////
	
	// init - sets initial values for drawing the scene
	@Override
	public void init(GLAutoDrawable glDrawable) {

        mouseTracker = new MouseTracker();		// initialize MouseTracker
        sim = new Simulator(glDrawable);				// initialize Simulator

	}

	// display - called when we need to draw a frame
	@Override
	public void display(GLAutoDrawable glDrawable) {

		sim.display();		// call Simulator display

	}

	// reshape - called on open and when we resize the window
	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, 
			int width, int height) {
        final GL2 gl = glDrawable.getGL().getGL2();

        if (height <= 0) // avoid a divide by zero error!
            height = 1;
        
        // set Simulator screen aspect
        sim.setAspect(width, height);

        // set up glViewport with the new screen
        gl.glViewport(0, 0, width, height);
        // set the screen size in MouseTracker
        mouseTracker.setWindow(width, height);

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		
	}

	// keyTyped
	@Override
	public void keyTyped(KeyEvent e) {
		// wasd
		char key= e.getKeyChar();
		// if the key is a 'W':
		if((key == 'w') || (key == 'W')) {
			sim.move(0, 1);
		}
		// if the key is an 'A':
		else if((key == 'a') || (key == 'A')) {
			sim.move(1, 0);
		}
		// if the key is an 'S':
		else if((key == 's') || (key == 'S')) {
			sim.move(0, -1);
		}
		// if the key is an 'D':
		else if((key == 'd') || (key == 'D')) {
			sim.move(-1, 0);
		}
		
		// if the key is an 'H':
		else if((key == 'h') || (key == 'H')) {
			sim.debug();
		}
		
		else if(key == KeyEvent.VK_ESCAPE) {
			sim.toggleFreeView();
		}
		
		else if(key == KeyEvent.VK_SPACE) {
			sim.shoot();
		}

		else if((key == 'q') || (key == 'Q')) {
			sim.tiltLeft();
		}

		else if((key == 'e') || (key == 'E')) {
			sim.tiltRight();
		}


	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	// mouseDragged - captures the mouse drags for tilt and zoom
	@Override
	public void mouseDragged(MouseEvent e) {
		// get the current position of the mouse
		mouseTracker.setPostion(e);
		sim.rotate(-mouseTracker.change_x(), -mouseTracker.change_y());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	// mousePressed - sets up mouse position 
	@Override
	public void mousePressed(MouseEvent e) {
		// whenever the user presses the mouse, track the current 
		//   location so we can determine the drag distance later
		mouseTracker.setPostion(e);		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
}
