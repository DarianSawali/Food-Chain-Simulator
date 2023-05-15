package ecosys.simulation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.GardenPanel;
import processing.core.PVector;

public class Venom extends SimulationObject implements Mover {

	private PVector speed;
	private Ellipse2D missile;
	protected float speedMag;

	public Venom(float x, float y, float speedx, float speedy) {
		super(x, y, 15, 15, 1f);
		speed = new PVector(speedx, speedy);
		speedMag = 12f;
	}

	@Override
	public void draw(Graphics2D g) {
		AffineTransform at = g.getTransform();
		g.translate(pos.x, pos.y);
		g.scale(size, size);
		g.rotate(speed.heading());
		g.setColor(new Color(214, 158, 4));
		g.fill(missile);
		g.setTransform(at);
	}
	
	protected boolean eatable(SimulationObject food) {
		return (food instanceof Predator);
	}

	@Override
	public void drawInfo(Graphics2D g2) {
		// nothing to do here
	}

	@Override
	public void update(ArrayList<SimulationObject> objList, GardenPanel panel) {
		//Rectangle2D env = new Rectangle2D.Double(0, 0, panel.PAN_SIZE.width, panel.PAN_SIZE.height);
		
		move();
		ArrayList<SimulationObject> fList = filterTargetList(objList);
		traceBestFood(fList);
		

		for (SimulationObject obj : fList)
			if (isColliding(obj)) {
				objList.remove(obj);
				break;
			}
		
		
	}
	
	public void approach(SimulationObject target) {
		float coef = .3f; // coefficient of acceleration relative to maxSpeed
		PVector direction = PVector.sub(target.getPos(), pos).normalize();
		PVector accel = PVector.mult(direction, speedMag * coef);
		speed.add(accel);
	}

	@Override
	protected void setShapeAttributes() {
		missile = new Ellipse2D.Double(-dim.width / 2, -dim.height / 2, dim.width, dim.height);
	}

	@Override
	protected void setOutline() {
		outline = new Area(missile);
	}

	@Override
	protected Shape getOutline() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(size, size);
		at.rotate(speed.heading());
		return at.createTransformedShape(outline);
	}

	@Override
	public void move() {
		//System.out.println(pos.x + " " + pos.y + " " + speed.x + " " + speed.y);
		speed.normalize().mult(speedMag);
		pos.add(speed);
		
	}


	@Override
	public void checkCollision() {
		// nothing to do here
	}
	
	protected ArrayList<SimulationObject> filterTargetList(ArrayList<SimulationObject> fList) {
		ArrayList<SimulationObject> list = new ArrayList<>();
		for (SimulationObject f : fList)
			if (eatable(f))
				list.add(f);
		return list;
	}
	
	protected void traceBestFood(ArrayList<SimulationObject> fList) {
		if (fList.size() > 0) {
			// find 1st target
			SimulationObject target = fList.get(0);
			float distToTarget = PVector.dist(pos, target.getPos());

			// find the closer one
			for (SimulationObject f : fList)
				if (PVector.dist(pos, f.getPos()) < distToTarget) {
					target = f;
					distToTarget = PVector.dist(pos, target.getPos());
				}

			// make animal follow this target
			this.approach(target);
		}
		
	}

	@Override
	protected Shape getFOV() {
		// TODO Auto-generated method stub
		return null;
	}
	

}