import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.interpolators.*;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;

import java.applet.*;


public class Java3D extends Applet implements ActionListener, AppletStub{
	
	public static void main(String[] args) {
		new MainFrame(new Java3D(), 800, 600);
	}
	
	Button cB = new Button("Color");
    TextField txR = new TextField("R");
    TextField txG = new TextField("G");
    TextField txB = new TextField("B");
    Color3f shapeColor;
    
    Panel panel2;
    
    private ColorInterpolator color = null;
    
	public void init() {
		
		// add buttons
		setLayout(new BorderLayout());
	    Panel panel = new Panel();
	    panel.setLayout(new GridLayout(1, 4));
	    add(panel, BorderLayout.SOUTH);
	    txR.setMaximumSize(new Dimension(1, 1));
	    panel.add(txR);
	    txG.setMaximumSize(new Dimension(1, 1));
	    panel.add(txG);
	    txB.setMaximumSize(new Dimension(1, 1));
	    panel.add(txB);
	    
	    cB.addActionListener(this);
	    panel.add(cB);
	    
	    Panel panel1 = new Panel();
	    panel1.setLayout(new GridLayout(10, 1));
	    panel1.setBackground(Color.black);
	    add(panel1, BorderLayout.EAST);
	    Button help = new Button("Help");
	    panel1.add(help);
	    
	    //panel2 = new Panel(new GridLayout());
	    //TextArea helpArea = new TextArea("Click and drag to rotate", 1, 1, TextArea.SCROLLBARS_NONE);
	    //helpArea.setEnabled(false);
	    //help.addActionListener(this);
	    //panel2.add(helpArea);
	    //add(panel2, BorderLayout.CENTER);
	    
	    
	    //canvas
	    GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
	    Canvas3D cv = new Canvas3D(gc);
	    add(cv, BorderLayout.CENTER);
	    BranchGroup bg = createSceneGraph();
	    bg.compile();
	    SimpleUniverse su = new SimpleUniverse(cv);
	    su.getViewingPlatform().setNominalViewingTransform();
	    su.addBranchGraph(bg);
		
		
	}
	
	public void Canvas() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
	    Canvas3D cv = new Canvas3D(gc);
	    add(cv);
	    BranchGroup bg = createSceneGraph();
	    bg.compile();
	    SimpleUniverse su = new SimpleUniverse(cv);
	    su.getViewingPlatform().setNominalViewingTransform();
	    su.addBranchGraph(bg);
	    
	}
	
	
	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();
	    TransformGroup spin = new TransformGroup();
	    spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    root.addChild(spin);
	    
	    //object
	    Appearance ap = new Appearance();
	    Material material = new Material();
	    material.setCapability(Material.ALLOW_COMPONENT_WRITE);
	    material.setColorTarget(Material.AMBIENT);
	    ap.setMaterial(material);
	    Shape3D shape = new Shape3D(new TestShape(), ap);
	    
	    //rotating object
	    Transform3D tr = new Transform3D();
	    tr.setScale(0.25);
	    TransformGroup tg = new TransformGroup(tr);
	    spin.addChild(tg);
	    tg.addChild(shape);
	    Alpha alpha = new Alpha(-1, 4000);
	    
	    
	    RotationInterpolator rotator = new RotationInterpolator(alpha, spin);
	    BoundingSphere bounds = new BoundingSphere();
	    rotator.setSchedulingBounds(bounds);
	    //spin.addChild(rotator);
	    
	    //mouse rotation
	    MouseRotate mRotator = new MouseRotate(spin);
	    mRotator.setSchedulingBounds(bounds);
	    spin.addChild(mRotator);
	    
	    //mouse zoom
	    MouseZoom mZoom = new MouseZoom(spin);
	    mZoom.setSchedulingBounds(bounds);
	    spin.addChild(mZoom);
	    
	    //mouse translation
	    MouseTranslate mTranslator = new MouseTranslate(spin);
	    mTranslator.setSchedulingBounds(bounds);
	    spin.addChild(mTranslator);
	    
	    // color
	    //color = new ColorInterpolator(alpha, material, new Color3f(1,1,1), shapeColor);
	    //color.setSchedulingBounds(bounds);
	    //color.setEnable(true);
	    //root.addChild(color);
	    	    
	    //Background
	    Background background = new Background(new Color3f(Color.black));
	    background.setApplicationBounds(bounds);
	    root.addChild(background);
	    
	    //Light
	    if(shapeColor == null) {
	    	shapeColor = new Color3f(1, 1, 1);
	    }
	    PointLight ptlight = new PointLight(shapeColor,
	        new Point3f(0.1f,0.1f,1f), new Point3f(1f,0.2f,0f));
	    ptlight.setInfluencingBounds(bounds);
	    root.addChild(ptlight);
	    return root;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if("Color".equals(cmd)) {
			if(txR.getText().equals("R") || 
					txG.getText().equals("G") ||
					txB.getText().equals("B")) {
				//cB.setBackground(Color.red);
				
			} else {
				shapeColor.set(Integer.parseInt(txR.getText()), 
						Integer.parseInt(txG.getText()), 
						Integer.parseInt(txB.getText()));
				System.out.print(shapeColor);
				System.out.println("called");
				//stop();
				restart();
				
				System.out.println("called applet");
				
				
			}
		}
		
		if("Help".equals(cmd)){
			if(panel2.isVisible()) {
				panel2.setVisible(false);
				//System.out.println("disabled");
			} else {
				panel2.setVisible(true);
				//System.out.println("enabled");
			}
				
		}
		
	}
	
	
//	public void start() {
//		Applet applet = new Java3D();
//		applet.setStub(this);
//		this.setLayout(new BorderLayout());
//		add(applet, BorderLayout.CENTER);
//		applet.init();
//		//applet.start();
//		
//	}
	
	public void stop() {
		this.destroy();
	}
	
	public void restart() {
		//stop();
		this.removeAll();
		init();
		
	}

	public void appletResize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

}
