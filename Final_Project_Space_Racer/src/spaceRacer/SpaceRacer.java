/*****
 * SpaceRacer.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Main driver and entry point for the Space Racer program
 *****/

package spaceRacer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

// SpaceRacer class - sets up the screen and canvas for the 
//   JoglEventListener to use
public class SpaceRacer extends Frame {

	// Add serialVersionUID to correct warnings
	private static final long serialVersionUID = 1L;
	static Animator anim = null;
	
	// setupJOGL - sets values for JOGL to use
	private void setupJOGL(){
		// get the GL capabilites from the OS
	    GLCapabilities caps = new GLCapabilities(null);
	    caps.setDoubleBuffered(true);
	    caps.setHardwareAccelerated(true);
	    
	    // set up the canvas 
	    GLCanvas canvas = new GLCanvas(caps); 
        add(canvas);

        // make a new JoglEventListener and set it up as:
        //   * an EventListener
        //   * a KeyListener
        //   * a MouseListener
        //   * a MouseMotionListener
        JoglEventListener jgl = new JoglEventListener();
        canvas.addGLEventListener(jgl); 
        canvas.addKeyListener(jgl); 
        canvas.addMouseListener(jgl);
        canvas.addMouseMotionListener(jgl);

        // set up a new Animator to update the screen
        anim = new Animator(canvas);
        anim.start();

	}
	
	// constructor
	public SpaceRacer() {
		// set up a new window (Frame) with a title 
        super("Space Racer");

        // set the Frame layout
        setLayout(new BorderLayout());

        // set up a listener so the program shuts down when the window
        //   closes
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        // set the screen size, location, and visibility
        setSize(1200, 1200);
        setLocation(40, 40);
        setVisible(true);
        // everything else is ready, fire up JOGL
        setupJOGL();

	}
	
	// main - entry point to the program
    public static void main(String[] args) {
    	// set up an instance of this class and run it
        SpaceRacer active = new SpaceRacer();
        active.setVisible(true);
    }
}