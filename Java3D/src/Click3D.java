import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Click3D extends Applet implements MouseListener, ActionListener {

	public static void main(String[] args) {
		new MainFrame(new Click3D(), 800, 600);
	}

	PickCanvas pc;
	TransformGroup spin;
	Alpha alpha;
	BranchGroup bg;
	JButton startButton;
	JPanel panel;
	JTextArea score;
	JTextArea time;
	int currentScore;
	int highScore;
	static Timer timer;
	static int interval;
	JTextArea textArea;
	double scale;
	long elapsedTime;
	final int totalGameTime = 60;
	SimpleUniverse su;
	GraphicsConfiguration gc;
	Canvas3D cv;
	float value;

	public void init() {
		startNewGameMainMenu();
	}

	public void startNewGameMainMenu() {
		textArea = new JTextArea();
		try {
			File file = new File("highscore.txt");
			if (!file.exists()) {
				highScore = 0;
			} else {
				Scanner input = new Scanner(file);
				while (input.hasNextInt()) {
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

		GridLayout gridLayout = new GridLayout(2, 1);
		panel = new JPanel();
		panel.setLayout(gridLayout);
		panel.setPreferredSize(new Dimension(200, 200));

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
		gc = SimpleUniverse.getPreferredConfiguration();
		cv = new Canvas3D(gc);

		// mouse behavior
		cv.addMouseListener(this);

		// add score keeper
		setLayout(new BorderLayout());
		Panel panel = new Panel(new GridLayout(2, 1));
		score = new JTextArea();
		currentScore = 0;
		score.setText("Score: " + currentScore);
		score.setFont(new Font("Serif", Font.BOLD, 30));
		score.setBackground(Color.black);
		score.setForeground(Color.white);
		score.setEditable(false);
		panel.add(score);

		// add a timer
		time = new JTextArea();
		time.setText("Time: " + totalGameTime);
		time.setFont(new Font("Serif", Font.BOLD, 30));
		time.setBackground(Color.black);
		time.setForeground(Color.white);
		time.setEditable(false);
		panel.add(time);

		add(panel, BorderLayout.NORTH);

		// countdown
		int delay = 1000;
		int period = 1000;
		timer = new Timer();
		interval = totalGameTime;
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				time.setText("Time: " + setInterval());
				if (interval <= 0) {
					stopGame();
				}
				elapsedTime++;
				if (elapsedTime >= 2) {
					elapsedTime = 0;
					newScene();
					if (currentScore > 0) {
						currentScore--;
						score.setText("Score: " + currentScore);
					}
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

		su = new SimpleUniverse(cv);
		su.getViewingPlatform().setNominalViewingTransform();
		su.addBranchGraph(bg);
	}

	public void stopGame() {
		this.removeAll();
		startNewGameMainMenu();
		this.revalidate();
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_DETACH);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		root.addChild(spin);

		generateSphere();

		BoundingSphere bounds = new BoundingSphere();

		// light and background
		Background background = new Background(new Color3f(Color.BLACK));
		background.setApplicationBounds(bounds);
		root.addChild(background);

		// Ambient lighting
		AmbientLight aLight = new AmbientLight(true, new Color3f(getRandom(value), getRandom(value), getRandom(value)));

		aLight = new AmbientLight(true, new Color3f(getRandom(value), getRandom(value), getRandom(value)));

		aLight.setInfluencingBounds(bounds);
		aLight.setCapability(PointLight.ALLOW_STATE_WRITE | PointLight.ALLOW_STATE_READ);
		root.addChild(aLight);

		// Directional Lighting
		DirectionalLight dLight = new DirectionalLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)), 
				new Vector3f(getRandom(value), getRandom(value), getRandom(value)));

		dLight = new DirectionalLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)),
				new Vector3f(getRandom(value), getRandom(value), getRandom(value)));

		dLight.setCapability(PointLight.ALLOW_STATE_WRITE | PointLight.ALLOW_STATE_READ);
		dLight.setInfluencingBounds(bounds);
		root.addChild(dLight);

		// Point Light
		PointLight pLight = new PointLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)), 
				new Point3f(getRandom(value), getRandom(value), getRandom(value)), 
				new Point3f(getRandom(value), getRandom(value), getRandom(value)));

		pLight = new PointLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)));
		pLight.setCapability(PointLight.ALLOW_STATE_WRITE | PointLight.ALLOW_STATE_READ);
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);

		// Point Light is always white, but points at random
		pLight = new PointLight(new Color3f(Color.white),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)));
		pLight.setCapability(PointLight.ALLOW_STATE_WRITE | PointLight.ALLOW_STATE_WRITE);
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);

		// Spot Light
		SpotLight sLight = new SpotLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)), 
				new Point3f(getRandom(value), getRandom(value), getRandom(value)), 
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Vector3f(getRandom(value), getRandom(value), getRandom(value)), (float) (Math.PI / 6.0), 0f);

		sLight = new SpotLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Vector3f(getRandom(value), getRandom(value), getRandom(value)), (float) (Math.PI / 6.0), 0f);
		sLight.setCapability(PointLight.ALLOW_STATE_WRITE | PointLight.ALLOW_STATE_WRITE);
		sLight.setInfluencingBounds(bounds);
		root.addChild(sLight);

		SpotLight sLight2 = new SpotLight(new Color3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Point3f(getRandom(value), getRandom(value), getRandom(value)),
				new Vector3f(getRandom(value), getRandom(value), getRandom(value)), (float) (Math.PI / 12.0), 128f);
		sLight2.setCapability(PointLight.ALLOW_STATE_WRITE | PointLight.ALLOW_STATE_WRITE);
		sLight2.setInfluencingBounds(bounds);
		root.addChild(sLight2);

		Alpha alp = new Alpha(-1, 10000);
		RotationInterpolator rotator = new RotationInterpolator(alp, spin);
		rotator.setSchedulingBounds(bounds);
		spin.addChild(rotator);

		return root;
	}

	// returns a random float value
	public float getRandom(float value) {
		Random random = new Random();
		value = random.nextFloat();

		return value;
	}

	private static final int setInterval() {
		if (interval == 1) {
			timer.cancel();
		}
		return --interval;
	}

	public void mouseClicked(MouseEvent e) {
		pc.setShapeLocation(e);
		PickResult result = pc.pickClosest();
		Node node;

		if (result != null) {
			node = result.getObject();
			if (node instanceof Shape3D) {
				newScene();
				currentScore++;
				score.setText("Score: " + currentScore);
				if (currentScore > highScore) {
					updateFile(currentScore);
				}

				// reset scene refresh timer
				elapsedTime = 0;
			}
		}
	}

	public void newScene() {
		su.getLocale().removeBranchGraph(bg);
		bg = createSceneGraph();
		pc = new PickCanvas(cv, bg);
		su.addBranchGraph(bg);
		this.revalidate();
	}

	public Vector3f getRandomVector() {
		float maxY = 0.5f;
		float minY = -maxY;
		float randomY = (float) (minY + new Random().nextDouble() * (maxY - minY));
		Vector3f randVector = new Vector3f(getRandomX(), randomY, getRandomZ());

		return randVector;
	}

	public float getRandomX() {
		float maxX = 1f;
		float minX = -maxX;
		float randomX = (float) (minX + new Random().nextDouble() * (maxX - minX));
		return randomX;
	}

	public float getRandomZ() {
		float maxZ = 0.5f;
		float minZ = -maxZ;
		float randomZ = (float) (minZ + new Random().nextDouble() * (maxZ - minZ));
		return randomZ;
	}

	public Sphere generateSphere() {
		Sphere sphere = new Sphere(1.0f,
				Sphere.GENERATE_NORMALS | Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS, 120);
		sphere.setCapability(Sphere.ALLOW_PICKABLE_WRITE);

		Transform3D spheretr = new Transform3D();

		// Change scale of sphere based on surrentScore
		if (currentScore >= 0 && currentScore < 10) {
			scale = 0.25;
		} else if (currentScore >= 10 && currentScore < 20) {
			scale = 0.20;
		} else if (currentScore >= 20 && currentScore < 30) {
			scale = 0.15;
		} else if (currentScore >= 30 && currentScore < 40) {
			scale = 0.10;
		} else {
			scale = 0.05;
		}

		spheretr.setScale(scale);
		spheretr.setTranslation(getRandomVector());
		TransformGroup tg1 = new TransformGroup(spheretr);
		spin.addChild(tg1);
		tg1.addChild(sphere);

		return sphere;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("Start".equals(cmd)) {
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
			fwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

}
