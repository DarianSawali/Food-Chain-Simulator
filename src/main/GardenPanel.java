package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import ecosys.simulation.SimulationObject;
import main.GardenPanel.MyKeyAdapter;
import ecosys.simulation.Hunter;
import ecosys.simulation.Predator;
import ecosys.simulation.Prey;
import util.Util;


//Controls :
//SPACE to pause display
//SHIFT to shoot venom from frog
//UP ARROW key to speed up
//DOWN ARROW key to slow down
//'T' to toggle information on creatures

public class GardenPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<SimulationObject> objList;
	private Timer t;

	private int PREDATOR_COUNT = 6;
	private int PREY_COUNT = 12;
	private int MAX_FOOD = PREY_COUNT * 2;
	private String status = "status...";
	private boolean showInfo = true;
	private boolean reset;
	private boolean createdHunter = false;
	private Hunter hunter;

	private int respawnTimer = 0;

	public static Line2D.Double rightEdge;
	public static Line2D.Double leftEdge;
	public static Line2D.Double topEdge;
	public static Line2D.Double bottomEdge;

	private Ellipse2D.Double oval, oval1, oval2, oval3;
	private Ellipse2D.Double petal, petal1, mid;

	private boolean fire;

	public static final Dimension PAN_SIZE = new Dimension(1920, 1080);
	public Dimension panelSize;

	public GardenPanel(Dimension initialSize) {
		super();
		this.panelSize = initialSize;

		this.objList = new ArrayList<>();

		oval = new Ellipse2D.Double();
		oval1 = new Ellipse2D.Double();
		oval2 = new Ellipse2D.Double();
		oval3 = new Ellipse2D.Double();
		petal = new Ellipse2D.Double();
		petal1 = new Ellipse2D.Double();
		mid = new Ellipse2D.Double();

		for (int i = 0; i < PREY_COUNT; i++) {
			this.addPrey();
		}

		for (int i = 0; i < PREDATOR_COUNT; i++) {
			this.addPredator();
		}

		hunter = new Hunter(60, panelSize.height / 2, 0.9f);

		t = new Timer(33, this);
		t.start();

		addKeyListener(new MyKeyAdapter());
		setFocusable(true);

		rightEdge = new Line2D.Double(panelSize.width - 150, 75, panelSize.width - 150, panelSize.height - 150);
		leftEdge = new Line2D.Double(75, 75, 75, panelSize.height - 150);
		topEdge = new Line2D.Double(75, 75, panelSize.width - 150, 75);
		bottomEdge = new Line2D.Double(75, panelSize.height - 150, panelSize.width - 150, panelSize.height - 150);
	}

	public void setFlowerAttributes() {
		int bodyW = 20;
		petal.setFrame(0, -bodyW / 4, bodyW, bodyW / 2);
		petal1.setFrame(-bodyW / 4, 0, bodyW / 2, bodyW);
		mid.setFrame(-bodyW / 2, -bodyW / 2, bodyW, bodyW);
	}

	public void setBushAttributes() {
		oval.setFrame(0, 0, 90, 60);
		oval1.setFrame(60, -20, 90, 60);
		oval2.setFrame(50, 30, 90, 60);
		oval3.setFrame(100, 0, 90, 60);
	}

	public void setPathAttributes() {
		oval.setFrame(0, 0, 60, 35);
		oval1.setFrame(60, -20, 60, 35);
		oval2.setFrame(50, 30, 60, 35);
		oval3.setFrame(110, 10, 60, 35);
	}

	public void drawFlowr(Graphics2D g2, int x, int y) {
		setFlowerAttributes();
		AffineTransform aw = g2.getTransform();
		g2.translate(x, y);
		g2.setColor(new Color(255, 255, 255));

		g2.scale(1, 1);

		AffineTransform af = g2.getTransform();
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(af);

		AffineTransform am = g2.getTransform();
		g2.scale(-1, 1);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(am);

		AffineTransform as = g2.getTransform();
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal1);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal1);
		g2.setTransform(as);

		AffineTransform ag = g2.getTransform();
		g2.setColor(new Color(255, 255, 255));
		g2.scale(1, -1);
		g2.fill(petal1);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal1);
		g2.setTransform(ag);

		AffineTransform ah = g2.getTransform();
		g2.rotate(-45);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(ah);

		AffineTransform az = g2.getTransform();
		g2.rotate(45);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(az);

		AffineTransform ab = g2.getTransform();
		g2.scale(-1, 1);
		g2.rotate(-45);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(ab);

		AffineTransform ac = g2.getTransform();
		g2.scale(-1, 1);
		g2.rotate(45);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(ac);

		g2.setColor(new Color(207, 62, 62));
		g2.fill(mid);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(mid);
		g2.setTransform(aw);

	}

	public void drawBush(Graphics2D g2) {
		setBushAttributes();
		AffineTransform wu = g2.getTransform();
		g2.translate(panelSize.width * 5 / 32, panelSize.height * 5 / 27);
		g2.scale(3, 3);
		g2.setColor(new Color(14, 130, 37));
		g2.fill(oval);
		g2.fill(oval1);
		g2.fill(oval2);
		g2.fill(oval3);

		g2.setTransform(wu);

		AffineTransform wd = g2.getTransform();
		g2.translate(panelSize.width * 25 / 48, panelSize.height * 5 / 9);
		g2.scale(3, 3);
		g2.setColor(new Color(14, 130, 37));
		g2.fill(oval);
		g2.fill(oval1);
		g2.fill(oval2);
		g2.fill(oval3);

		g2.setTransform(wd);
	}

	public void drawPath(Graphics2D g2) {
		setPathAttributes();
		AffineTransform wg = g2.getTransform();
		g2.translate(panelSize.width * 5 / 24, panelSize.height * 35 / 54);
		g2.scale(2, 2);
		g2.setColor(new Color(110, 82, 34));
		g2.fill(oval);
		g2.fill(oval1);
		g2.fill(oval2);
		g2.fill(oval3);

		g2.setTransform(wg);

		AffineTransform wu = g2.getTransform();
		g2.translate(panelSize.width * 35 / 96, panelSize.height * 25 / 54);
		g2.scale(2, 2);
		g2.setColor(new Color(110, 82, 34));
		g2.fill(oval);
		g2.fill(oval1);
		g2.fill(oval2);
		g2.fill(oval3);

		g2.setTransform(wu);

		AffineTransform ws = g2.getTransform();
		g2.translate(panelSize.width * 25 / 48, panelSize.height * 5 / 18);
		g2.scale(2, 2);
		g2.setColor(new Color(110, 82, 34));
		g2.fill(oval);
		g2.fill(oval1);
		g2.fill(oval2);
		g2.fill(oval3);

		g2.setTransform(ws);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		panelSize = getSize();
		setBackground(Color.darkGray);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(new Color(66, 245, 132));

		g2.fillRect(75, 75, panelSize.width - 150, panelSize.height - 150);

		drawBush(g2);
		drawPath(g2);

		for (SimulationObject obj : objList) {
			obj.draw(g2);
			if (showInfo)
				obj.drawInfo(g2);
		}

		drawStatusBar(g2);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < objList.size(); i++) {
			objList.get(i).update(objList, this);

		}

		if (Util.countPrey(objList) <= PREY_COUNT / 2 && !createdHunter) {
			createdHunter = true;
			objList.add(hunter);
		}

		else if (Util.countPrey(objList) <= PREY_COUNT / 2) {
			int count = Util.countPredator(objList) - (PREDATOR_COUNT / 2);
			String str = "Kill " + count + " wasps to restore balance";
			setStatus(str);
			if (count <= 0) {
				str = "Balance restored!";
				setStatus(str);
			}
		}

		else {
			int count = Util.countPrey(objList) - (PREY_COUNT / 2);
			if (count < 0) {
				count = 0;
			}
			String str = count + " bees remain until frog appears.";
			setStatus(str);
		}

		if (Util.countPredator(objList) <= PREDATOR_COUNT / 2) {
			objList.remove(hunter);
			reset = true;
		}

		if (reset) {
			respawnTimer++;
			if (respawnTimer >= 100) {

				for (int i = 0; i < PREY_COUNT; i++) {
					addPrey();
				}
				for (int i = 0; i < PREDATOR_COUNT; i++) {
					addPredator();
				}

				reset = false;
				respawnTimer = 0;
				createdHunter = false;

			}
		}

		if (Util.countFood(objList) < MAX_FOOD)
			objList.add(Util.randomFood(this));

		repaint();
	}

	public void addPrey() {
		float x = (float) Util.random(panelSize.width - 100);
		float y = (float) Util.random(panelSize.height - 100);
		float size = Util.random(0.2f, 0.5f);
		this.objList.add(new Prey(x, y, size));
	}

	public void addPredator() {
		float x = (float) Util.random(panelSize.width - 100);
		float y = (float) Util.random(panelSize.height - 100);
		float size = Util.random(0.4f, 0.7f);
		this.objList.add(new Predator(x, y, size));
	}

	private void drawStatusBar(Graphics2D g) {
		Font f = new Font("Arial", Font.BOLD, 12);
		g.setFont(f);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, getSize().height - 24, getSize().width, 24);
		g.setColor(Color.BLACK);
		g.drawString(status, 12, getSize().height - 8);
	}

	public void setStatus(String st) {
		this.status = st;
	}

	public class MyKeyAdapter extends KeyAdapter {
		private boolean shoot = true;

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_SPACE) {
				if (t.isRunning())
					t.stop();
				else
					t.start();
			}

			if (keyCode == KeyEvent.VK_SHIFT) {
				if (shoot) {
					hunter.fire();
					shoot = false;
					
				} else
					fire = false;
				System.out.println("fire");
			}

			if (keyCode == KeyEvent.VK_UP) {
				t.setDelay(t.getDelay() / 2);
			}
			if (keyCode == KeyEvent.VK_DOWN) {
				t.setDelay(t.getDelay() * 2);
			}
			if (keyCode == KeyEvent.VK_T) {
				if (showInfo == false) {
					showInfo = true;
				} else {
					showInfo = false;
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_SHIFT) {
				fire = false;
				shoot = true;
			}
		}
	}
}
