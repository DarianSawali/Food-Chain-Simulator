package ecosys.simulation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.GardenPanel;
import processing.core.PVector;
import util.Util;

public class Hunter extends Animal {

	private Ellipse2D.Double body, head, eye, wings, pupil;
	protected Arc2D.Double stripe1, stripe2, fov;
	protected Line2D.Double ant;
	protected Polygon sting;

	private ArrayList<Venom> missileList = new ArrayList<Venom>();

	public Hunter(float x, float y, float size) {
		super(x, y, 100, 80, size);
		this.color = Util.randomColor();
		wing = (float) (Math.PI / 12);
		vangle = 0;
		this.speed = new PVector(0, 8);
	}

	@Override
	protected void traceBestFood(ArrayList<SimulationObject> fList) {

	}

	@Override
	protected boolean eatable(SimulationObject food) {
		return (food instanceof Predator);
	}

	public void fire() {
		// create speed for the missile that goes along the same direction as the player
		PVector mSpeed = PVector.fromAngle(speed.heading()).mult(12);

		// Add a missile object to the list for shooting
		missileList.add(new Venom(pos.x, pos.y, mSpeed.x, mSpeed.y));
		System.out.println("a");
	}

	@Override
	public void draw(Graphics2D g2) {

		for (Venom v : missileList)
			v.draw(g2);

		AffineTransform az = g2.getTransform();

		g2.translate(pos.x, pos.y);
		// g2.rotate(speed.heading());
		g2.scale(size, size);
		if (speed.x < 0)
			g2.scale(1, -1);

		AffineTransform wu = g2.getTransform();
		g2.rotate(-Math.PI / 4);
		g2.setColor(color);
		g2.fill(wings);

		g2.setColor(new Color(0, 0, 0));
		g2.draw(wings);

		g2.setTransform(wu);

		// bee wing 2
		AffineTransform wd = g2.getTransform();
		g2.scale(1, -1);
		g2.rotate(-Math.PI / 4);
		g2.setColor(color);
		g2.fill(wings);

		g2.setColor(new Color(0, 0, 0));
		g2.draw(wings);

		g2.setTransform(wd);

		// bee body and head
		AffineTransform af = g2.getTransform();
		g2.setColor(color);
		g2.fill(body);

		g2.setColor(new Color(0, 0, 0));
		g2.draw(body);

		g2.setColor(color);
		g2.fill(head);

		g2.setColor(new Color(0, 0, 0));
		g2.draw(head);

		g2.setTransform(af);

		AffineTransform ay = g2.getTransform();
		g2.setColor(color.WHITE);
		// g2.setColor(new Color(0, 0, 0));
		g2.fill(eye);

		g2.setColor(color.black);
		g2.draw(eye);
		g2.scale(1, -1);

		g2.setColor(color.WHITE);
		g2.fill(eye);

		g2.setColor(color.black);
		g2.draw(eye);

		g2.setTransform(ay);

		AffineTransform am = g2.getTransform();
		g2.setColor(color.black);
		g2.fill(pupil);

		g2.setColor(color.black);
		g2.draw(pupil);
		g2.scale(1, -1);
		g2.fill(pupil);

		g2.setColor(color.black);
		g2.draw(pupil);

		g2.setTransform(am);

		g2.setTransform(az);

	}

	protected ArrayList<SimulationObject> filterTargetList(ArrayList<SimulationObject> fList) {
		ArrayList<SimulationObject> list = new ArrayList<>();
		for (SimulationObject f : fList)
			if (eatable(f))
				list.add(f);
		return list;
	}

	public void move() {
		PVector wallForce = wallPush().div((float) dim.width / 260f);
		pos.add(speed);
		speed.add(wallForce);
		speed.normalize().mult(speedMag);
	}

	private PVector wallPush() {
		PVector force = new PVector();
		float wallCoef = 300.0f;
		double distance;

		distance = GardenPanel.topEdge.ptLineDist(pos.x, pos.y) - dim.width * size;
		force.add(new PVector(0, (float) (+wallCoef / Math.pow(distance, 2))));
		distance = GardenPanel.bottomEdge.ptLineDist(pos.x, pos.y) - dim.width * size;
		force.add(new PVector(0, (float) (-wallCoef / Math.pow(distance, 2))));

		return force;

	}

	@Override
	protected void setShapeAttributes() {
		body = new Ellipse2D.Double(-dim.width / 2, -3 * dim.height / 4, 9 * dim.width / 8, 3 * dim.height / 2);
		head = new Ellipse2D.Double(dim.width / 4, -dim.height / 2, 3 * dim.width / 4, 3 * dim.width / 4);

		stripe1 = new Arc2D.Double(-dim.width / 4, -dim.height / 2, dim.width / 2, dim.height, -90, -180, Arc2D.PIE);
		stripe2 = new Arc2D.Double(-dim.width / 4, -dim.height / 2, dim.width / 2, dim.height, -90, 180, Arc2D.PIE);

		wings = new Ellipse2D.Double(-3 * dim.width / 4, 0, (int) (dim.height / 2), dim.width);

		ant = new Line2D.Double(0, 0, dim.width / 2, 0);

		eye = new Ellipse2D.Double((3 * dim.width / 4) - dim.width / 30, dim.height / 3 - dim.width / 30, dim.width / 4,
				dim.height / 4);
		pupil = new Ellipse2D.Double((3 * dim.width / 4) - dim.width / 500, dim.height / 3 - dim.width / 500,
				dim.width / 8, dim.height / 8);

		int[] x = { 0, 0, -(7 * dim.width / 100) };
		int[] y = { -(dim.width / 10), (dim.width / 10), 0 };
		sting = new Polygon(x, y, 3);

	}

	@Override
	protected void setOutline() {
		outline = new Area(body);
		outline.add(new Area(head));

	}

	public void update(ArrayList<SimulationObject> objList, GardenPanel panel) {
		super.update(objList, panel);

		ArrayList<SimulationObject> fList = filterTargetList(objList);

		for (int i = 0; i < missileList.size(); i++) {
			Venom m = missileList.get(i);
			m.update(objList, panel);
			for (int j = 0; j < fList.size(); j++) {
				SimulationObject c = fList.get(j);
				if (c.isColliding(m)) {
					missileList.remove(m);
					System.out.println("bullet");

				}
			}
		}
	}

	public void checkCollision() {
		float coef = 50f;

		Line2D.Double TOP_LINE = GardenPanel.topEdge;
		Line2D.Double BTM_LINE = GardenPanel.bottomEdge;

		double top_dist = TOP_LINE.ptLineDist(pos.x, pos.y) - getBoundingBox().getHeight() / 2;
		double btm_dist = BTM_LINE.ptLineDist(pos.x, pos.y) - getBoundingBox().getHeight() / 2;

		PVector top_f = new PVector(0, 1).mult((float) (coef / Math.pow(top_dist, 2f)));
		PVector btm_f = new PVector(0, -1).mult((float) (coef / Math.pow(btm_dist, 2f)));

		speed.add(top_f).add(btm_f);
	}

	@Override
	protected Shape getOutline() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		// at.rotate(speed.heading());
		at.scale(size, size);
		return at.createTransformedShape(outline);
	}

	@Override
	protected Shape getFOV() {
		// TODO Auto-generated method stub
		return null;
	}
}
