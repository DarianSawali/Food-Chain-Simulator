package ecosys.simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import main.GardenPanel;
import processing.core.PVector;
import util.Util;

public abstract class Animal extends SimulationObject implements Mover {

	protected PVector speed;
	protected float speedMag;
	protected float energy;
	protected final float FULL_ENERGY = 1000;
	protected float engGainRatio = 100;
	protected float engLossRatio = FULL_ENERGY / (70 * 15);
	protected float sizeGrowRatio = 0.0001f;

	protected float vangle, wing;
	protected Color color;
	protected Arc2D.Double fov;
	protected Ellipse2D.Double eye;
	protected Polygon tail;

	// FSM states
	protected int state;
	protected final int DEATH = -1;
	protected final int HUNGRY = 0;
	protected final int HALF_FULL = 1;
	protected final int FULL = 2;
	protected final int OVER_FULL = 3;
	protected final int SICK = 4;

	public Animal(float x, float y, int w, int h, float size) {
		super(x, y, w, h, size);
		speedMag = 10f;
		speed = Util.randomPVector(speedMag);
		state = OVER_FULL;

	}

	public void move() {
		PVector wallForce = wallPush().div((float) dim.width / 260f);
		pos.add(speed);
		speed.add(wallForce);
		speed.normalize().mult(speedMag);
		energy -= engLossRatio;
		if (state == SICK || state == DEATH)
			speed.mult((energy + 200) / 500);
	}

	public void approach(SimulationObject target) {
		float coef = .3f;
		PVector direction = PVector.sub(target.getPos(), pos).normalize();
		PVector accel = PVector.mult(direction, speedMag * coef);
		speed.add(accel);
	}

	@Override
	public void update(ArrayList<SimulationObject> objList, GardenPanel panel) {
		ArrayList<SimulationObject> fList = filterTargetList(objList);
		traceBestFood(fList);
		checkCollision();
		move();

		for (int i = 0; i < fList.size(); i++) {
			if (isColliding(fList.get(i))) {
				float foodSize = fList.get(i).getSize();
				energy += foodSize * engGainRatio;
				String st = String.format("%s gains energy by %.2f units to %.2f", animalType(), foodSize * 100,
						energy);

				if (state == OVER_FULL) {
					float extra = energy - FULL_ENERGY;
					energy = FULL_ENERGY;
					size += extra * sizeGrowRatio * size;
					st = String.format("%s grows by %.1f%% to %.2f%n", animalType(), energy * .01, size);
					panel.setStatus(st);
					System.out.println(st);
				}
				objList.remove(fList.get(i));
			}

		}
		if (energy > FULL_ENERGY)
			state = OVER_FULL;
		else if (energy == FULL_ENERGY)
			state = FULL;
		else if (energy > FULL_ENERGY / 2)
			state = HALF_FULL;
		else if (energy > FULL_ENERGY / 3)
			state = HUNGRY;
		else if (energy > -.2 * FULL_ENERGY)
			state = SICK;
		else
			state = DEATH;
		if (state == DEATH) {
			panel.setStatus(this.animalType() + " died ... ");
			objList.remove(this);
			return;
		}

	}

	public boolean sees(SimulationObject simulationObject) {
		return (getFOV().intersects(simulationObject.getFOV().getBounds2D())
				&& simulationObject.getFOV().intersects(getFOV().getBounds2D()));
	}

	protected void setShapeAttributes() {
		float sight = dim.width * speedMag * .75f;
		fov = new Arc2D.Double(-sight, -sight, sight * 2, sight * 2, -55, 110, Arc2D.PIE);
	}

	private String animalType() {
		String type = "unknown animal";
		if (this instanceof Predator)
			type = "Predator";
		else if (this instanceof Prey)
			type = "Prey";
		else if (this instanceof Hunter)
			type = "Hunter";
		return type;
	}

	protected ArrayList<SimulationObject> filterTargetList(ArrayList<SimulationObject> fList) {
		ArrayList<SimulationObject> list = new ArrayList<>();
		for (SimulationObject f : fList)
			if (eatable(f))
				list.add(f);
		return list;
	}

	public void drawInfo(Graphics2D g2) {
		AffineTransform at = g2.getTransform();
		g2.translate(pos.x, pos.y);

		String st1 = "Size     : " + String.format("%.2f", size);
		String st2 = "Speed  : " + String.format("%.2f", speed.mag());
		String st3 = "Energy : " + String.format("%.2f", energy);

		Font f = new Font("Courier", Font.PLAIN, 12);
		FontMetrics metrics = g2.getFontMetrics(f);

		float textWidth = metrics.stringWidth(st3);
		float textHeight = metrics.getHeight();
		float margin = 12, spacing = 6;

		g2.setColor(new Color(255, 255, 255, 60));
		g2.fillRect((int) (-textWidth / 2 - margin),
				(int) (-dim.height * size * .75f - textHeight * 5f - spacing * 4f - margin * 2f),
				(int) (textWidth + margin * 2f), (int) (textHeight * 5f + spacing * 4f + margin * 2f));

		g2.setColor(Color.blue.darker());
		g2.drawString(this.animalType(), -metrics.stringWidth(this.animalType()) / 2,
				-dim.height * size * .75f - margin - (textHeight + spacing) * 4f);
		g2.setColor(Color.black);
		g2.drawString(st1, -textWidth / 2, -dim.height * size * .75f - margin - (textHeight + spacing) * 2f);
		g2.drawString(st2, -textWidth / 2, -dim.height * size * .75f - margin - (textHeight + spacing) * 1f);

		if (state == SICK)
			g2.setColor(Color.red);
		g2.drawString(st3, -textWidth / 2, -dim.height * size * .75f - margin);

		g2.setTransform(at);
	}

	private PVector wallPush() {
		PVector force = new PVector();
		float wallCoef = 300.0f;
		double distance;

		distance = GardenPanel.rightEdge.ptLineDist(pos.x, pos.y) - dim.width * size;
		force.add(new PVector((float) (-wallCoef / Math.pow(distance, 2)), 0));
		distance = GardenPanel.leftEdge.ptLineDist(pos.x, pos.y) - dim.width * size;
		force.add(new PVector((float) (+wallCoef / Math.pow(distance, 2)), 0));
		distance = GardenPanel.topEdge.ptLineDist(pos.x, pos.y) - dim.width * size;
		force.add(new PVector(0, (float) (+wallCoef / Math.pow(distance, 2))));
		distance = GardenPanel.bottomEdge.ptLineDist(pos.x, pos.y) - dim.width * size;
		force.add(new PVector(0, (float) (-wallCoef / Math.pow(distance, 2))));

		return force;

	}

	public void checkCollision() {
		float coef = 50f;

		Line2D.Double TOP_LINE = GardenPanel.topEdge;
		Line2D.Double BTM_LINE = GardenPanel.bottomEdge;
		Line2D.Double LFT_LINE = GardenPanel.leftEdge;
		Line2D.Double RGT_LINE = GardenPanel.rightEdge;

		double top_dist = TOP_LINE.ptLineDist(pos.x, pos.y) - getBoundingBox().getHeight() / 2;
		double btm_dist = BTM_LINE.ptLineDist(pos.x, pos.y) - getBoundingBox().getHeight() / 2;
		double lft_dist = LFT_LINE.ptLineDist(pos.x, pos.y) - getBoundingBox().getWidth() / 2;
		double rgt_dist = RGT_LINE.ptLineDist(pos.x, pos.y) - getBoundingBox().getWidth() / 2;

		PVector top_f = new PVector(0, 1).mult((float) (coef / Math.pow(top_dist, 2f)));
		PVector btm_f = new PVector(0, -1).mult((float) (coef / Math.pow(btm_dist, 2f)));
		PVector lft_f = new PVector(1, 0).mult((float) (coef / Math.pow(lft_dist, 2f)));
		PVector rgt_f = new PVector(-1, 0).mult((float) (coef / Math.pow(rgt_dist, 2f)));

		speed.add(top_f).add(btm_f).add(lft_f).add(rgt_f);
	}

	protected abstract void traceBestFood(ArrayList<SimulationObject> fList);

	protected abstract boolean eatable(SimulationObject food);
}
