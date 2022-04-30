import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.awt.event.*;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColorInterpolator;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransformInterpolator;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TransparencyInterpolator;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Click3D extends Applet implements MouseListener, ActionListener{
	
	public static void main(String[] args) {
		new MainFrame(new Click3D(), 800, 600);
		
	}
	
	AmbientLight aLight;
	PickCanvas pc;
	PositionInterpolator translator;
	TransformInterpolator transformInterpolator;
	RotationInterpolator rotator;
	BranchGroup root;
	TransformGroup spin;
	BoundingSphere bounds;
	Alpha alpha;
	BranchGroup bg;
	JButton startButton;
	boolean isClicked;
	JPanel panel;
	JTextArea score;
	JTextArea time;
	int currentScore;
	int highScore;
	PrintWriter writer;
	static Timer timer;
	static int interval;
	JTextArea textArea;
	double scale;
	long elapsedTime;
	
	public void init() {
		
		startNewGameMainMenu();
	    
	  }
	
	public void startNewGameMainMenu() {
		textArea = new JTextArea();
		try {
			File file = new File("highscore.txt");
			if(!file.exists()) {
				highScore = 0;
			} else{
				Scanner input = new Scanner(file);
				while(input.hasNextInt()) {
					highScore = input.nextInt();	
				}
				input.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		textArea.setText("Highscore: " + highScore);
		textArea.setFont(new Font("Serif", Font.BOLD, 30));
		textArea.setBackground(Color.white);
		textArea.setEditable(false);
		
		
		GridLayout gridLayout = new GridLayout(2,1);
		panel = new JPanel();
		panel.setLayout(gridLayout);
		panel.setPreferredSize(new Dimension(200,200));
		
		startButton = new JButton("Start");
		startButton.setFont(new Font("Arial", Font.PLAIN, 30));
		startButton.setBackground(Color.LIGHT_GRAY);
		
		panel.add(startButton);
		panel.add(textArea);
		panel.setBackground(Color.white);
		
		add(panel);
		this.setBackground(Color.white);
		
		
	    startButton.addActionListener(this);
	}
	
	public void startGame() {
		
		// create canvas
	    GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
	    Canvas3D cv = new Canvas3D(gc);
	    
	    // mouse behavior
	    cv.addMouseListener(this);
//	    cv.addMouseListener(new MouseAdapter() {
//	    	// destroy shape on click
//	    	public void mouseClicked(MouseEvent e) {
//	    		
//	    		pc.setShapeLocation(e);
//	    		PickResult[] results = pc.pickAll();
//	    		
//	    		
//	    	}
//	    });
	    
	    setLayout(new BorderLayout());
	    Panel panel = new Panel(new GridLayout(2,1));
	    score = new JTextArea();
	    currentScore = 0;
	    score.setText("Score: " + currentScore);
	    score.setFont(new Font("Serif", Font.BOLD, 30));
	    score.setBackground(Color.WHITE);
	    score.setEditable(false);
	    panel.add(score);
	    //add(score, BorderLayout.NORTH);
	    
	    // add a timer
	    time = new JTextArea();
	    time.setText("Time: " + 60);
	    time.setFont(new Font("Serif", Font.BOLD, 30));
	    time.setBackground(Color.WHITE);
	    time.setEditable(false);
	    //add(time, BorderLayout.NORTH);
	    panel.add(time);
	    
	    add(panel, BorderLayout.NORTH);
	    
	    
	    int delay = 1000;
	    int period = 1000;
	    timer = new Timer();
	    interval = 60;
	    timer.scheduleAtFixedRate(new TimerTask() {
	    	public void run() {
	    		time.setText("Time: " + setInterval());
	    		//System.out.println(setInterval());
	    		if(interval <= 0) {
	    			stopGame();
	    		}
	    		
	    		elapsedTime++;
	    		if(elapsedTime >= 2) {
	    			elapsedTime = 0;
	    			newScene();
	    		}
	    	}
	    }, delay, period);
		
	    add(cv, BorderLayout.CENTER);
	    bg = createSceneGraph();
	    bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	    bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	    bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	    bg.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
	    bg.compile();
	    
	    pc = new PickCanvas(cv, bg);
	    pc.setMode(PickTool.GEOMETRY); 
	    
	    SimpleUniverse su = new SimpleUniverse(cv);
	    su.getViewingPlatform().setNominalViewingTransform();
	    su.addBranchGraph(bg);
	    
	    
	}
	
	public void stopGame() {
		this.removeAll();
		//this.repaint();
		startNewGameMainMenu();
		this.revalidate();
		
		
	}

	  private BranchGroup createSceneGraph() {
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_DETACH);
	    spin = new TransformGroup();
	    spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    spin.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    root.addChild(spin);
	    
	    // generate shape
	    //generateShape();
	    generateSphere();
	    
	    
	    
	    
	    

	    //light and background
	    Background background = new Background(new Color3f(Color.white));
	    background.setApplicationBounds(bounds);
	    root.addChild(background);
	    
	    Alpha alp = new Alpha(-1, 10000);
	    rotator = new RotationInterpolator(alp, spin);
	    rotator.setSchedulingBounds(bounds);
	    
	    spin.addChild(rotator);
	    

	    return root;
	  }
	  
	  private static final int setInterval() {
		  if (interval == 1) {
			  timer.cancel();
		  }
		  return --interval;
	  }

	  @Override
	  public void mouseClicked(MouseEvent e) {
		pc.setShapeLocation(e);
		PickResult result = pc.pickClosest();
		Node node;
		
		
		if(result != null) {
			node = result.getObject();
			Shape3D s = (Shape3D)result.getNode(PickResult.SHAPE3D);
			
			if(node instanceof Shape3D) {
				//System.out.println(node.toString());
				System.out.println(s.toString());
				System.out.println(s.getGeometry());
				
				
				//remove the shape
				//if(!translator.getEnable()) {
					
					//rotator.setEnable(false);
//					translator.setEnable(true);
//					//Transform3D tr = new Transform3D();
//				    //tr.setTranslation(getRandomVector());
//				    //translator.setTransformAxis(tr);
//				    
//					translator.setStartPosition(10f);  
//				    translator.setEndPosition(10f);
//				    
//				
//					bg.removeChild(root);
//				    bg.addChild(createSceneGraph());
				newScene();
				currentScore++;
				score.setText("Score: " + currentScore);
				if(currentScore > highScore) {
					updateFile(currentScore);
				}
				
				//get time and start the timer
				isClicked = true;
				elapsedTime = 0;
				    
				    //System.out.println(isClicked);
					//s.setPickable(false);
					
					
					
				//}
				
			   // System.out.println(isClicked);
				//System.out.println("removed");
				
		}
		
			
			
		}
	  }
	  
	  public void newScene() {
			translator.setEnable(true);
			//Transform3D tr = new Transform3D();
		    //tr.setTranslation(getRandomVector());
		    //translator.setTransformAxis(tr);
		    
			translator.setStartPosition(10f);  
		    translator.setEndPosition(10f);
		    
		    
		    
			bg.removeChild(root);
		    bg.addChild(createSceneGraph());
		    
		    this.revalidate();
		    
	  }
	  
	  public Vector3f getRandomVector() {
		  	float maxY = 0.5f;
		  	float minY = -maxY;
		    float randomY = (float) (minY + new Random().nextDouble() * (maxY  - minY));
		    Vector3f randVector = new Vector3f(getRandomX(), randomY, getRandomZ());
		    
		    return randVector;
	  }
	  
	  public float getRandomX() {
		    float maxX = 1f;
		    float minX = -maxX;
		    float randomX = (float) (minX + new Random().nextDouble() * (maxX  - minX));
		    return randomX;
	  }
	  
	  public float getRandomZ() {
		    float maxZ = 0.5f;
		    float minZ = -maxZ;
		    float randomZ = (float) (minZ + new Random().nextDouble() * (maxZ  - minZ));
		    return randomZ;
	  }

	  public Shape3D generateShape() {
		//object
		    Appearance ap = new Appearance();
		    Material mat = new Material();
		    ap.setMaterial(mat);
		    TransparencyAttributes transAttr = new TransparencyAttributes();
		    transAttr.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
		    ap.setTransparencyAttributes(transAttr);
		    
		    // spheres
		    Shape3D shape = new Shape3D(new TestShape(), ap);
		    shape.setCapability(Shape3D.ALLOW_PICKABLE_WRITE);
		    
		    
		    

		    Transform3D tr = new Transform3D();
		    tr.setScale(0.25);
		    
		    // random y location
		    Vector3f randomY = getRandomVector();
		    tr.setTranslation(randomY);
		    
		    TransformGroup tg = new TransformGroup(tr);
		    spin.addChild(tg);
		    tg.addChild(shape);
		    
		    

		    alpha = new Alpha();
		    bounds = new BoundingSphere();
		    
		    
		    // removing shape
		    translator = new PositionInterpolator(alpha, spin);
		    translator.setSchedulingBounds(bounds);
		    translator.setEnable(false);
		    tg.addChild(translator);
		    
		    
		    System.out.println(isClicked);
		    
		    return shape;
	  }
	  
	  public Sphere generateSphere() {
		  	Sphere sphere = new Sphere(1.0f, Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 120);
		    sphere.setCapability(Sphere.ALLOW_PICKABLE_WRITE);
		    System.out.println("sphere " + sphere);
		    Transform3D spheretr = new Transform3D();
		    
		    // Change scale of sphere based on surrentScore
		    if(currentScore >= 0 && currentScore < 10) {
		    	scale = 0.25;
		    } else if (currentScore >= 10 && currentScore < 20) {
		    	scale = 0.15;
		    } else if (currentScore >= 20 && currentScore < 30) {
		    	scale = 0.1;
		    } else if (currentScore >= 30 && currentScore < 40) {
		    	scale = 0.05;
		    } else {
		    	scale = 0.01;
		    }
		    spheretr.setScale(scale);
		    
		    spheretr.setTranslation(getRandomVector());
		    TransformGroup tg1 = new TransformGroup(spheretr);
		    spin.addChild(tg1);
		    tg1.addChild(sphere);
		    
		    alpha = new Alpha();
		    bounds = new BoundingSphere();
		    
		    translator = new PositionInterpolator(alpha, spin);
		    translator.setSchedulingBounds(bounds);
		    translator.setEnable(false);
		    tg1.addChild(translator);
		    
		    return sphere;
	  }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if("Start".equals(cmd)) {
			startButton.setVisible(false);
			panel.setVisible(false);
			startGame();
			this.revalidate();
		}
		
		

	}
	
	// create highscore file
	public void updateFile(int score) {
		try {
			FileWriter fwriter = new FileWriter("highscore.txt");
			fwriter.write(new Integer(score).toString());
			System.out.println(score);
			fwriter.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}


