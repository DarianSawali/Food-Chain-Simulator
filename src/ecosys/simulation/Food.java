package ecosys.simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import main.GardenPanel;
import util.Util;

public class Food extends SimulationObject {

	private Ellipse2D.Double foodShape; // geometric shape
	private Color foodColor; // shape color
	private Ellipse2D.Double petal, petal1, mid;
	private Color color;

	public Food(float x, float y, float size) {
		super(x, y, 75, 75, size);
		this.foodColor = Color.red;
		this.color = Util.randomColor();
	}

	@Override
	public void draw(Graphics2D g2) {
		AffineTransform aw = g2.getTransform();
		g2.translate(pos.x, pos.y);
		g2.setColor(color);
		g2.scale(size, size);

		AffineTransform af = g2.getTransform();
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(af);

		AffineTransform am = g2.getTransform();
		g2.scale(-1, 1);
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(am);

		AffineTransform as = g2.getTransform();
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal1);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal1);
		g2.setTransform(as);

		AffineTransform ag = g2.getTransform();
		g2.setColor(new Color(235, 95, 52));
		g2.scale(1, -1);
		g2.fill(petal1);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal1);
		g2.setTransform(ag);

		AffineTransform ah = g2.getTransform();
		g2.rotate(-45);
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(ah);

		AffineTransform az = g2.getTransform();
		g2.rotate(45);
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(az);

		AffineTransform ab = g2.getTransform();
		g2.scale(-1, 1);
		g2.rotate(-45);
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(ab);

		AffineTransform ac = g2.getTransform();
		g2.scale(-1, 1);
		g2.rotate(45);
		g2.setColor(new Color(235, 95, 52));
		g2.fill(petal);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(petal);
		g2.setTransform(ac);

		g2.setColor(color);
		g2.fill(mid);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(mid);

		g2.setTransform(aw);

		drawInfo(g2);
	}

	@Override
	protected void setShapeAttributes() {
		this.mid = new Ellipse2D.Double(-dim.width / 2, -dim.width / 2, dim.width, dim.width);
		petal = new Ellipse2D.Double(0, -dim.width / 4, dim.width, dim.width / 2);
		petal1 = new Ellipse2D.Double(-dim.width / 4, 0, dim.width / 2, dim.width);
	}

	@Override
	public void setOutline() {
		outline = new Area(mid);
	}

	@Override
	public Shape getOutline() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.scale(size, size);
		return at.createTransformedShape(outline);
	}

	@Override
	public void update(ArrayList<SimulationObject> objList, GardenPanel panel) {
		// nothing
	}

	public void drawInfo(Graphics2D g2) {
		AffineTransform at = g2.getTransform();
		g2.translate(pos.x, pos.y);
		g2.setColor(Color.WHITE);

		Font f = new Font("Arial", Font.BOLD, 12);
		g2.setFont(f);
		String st = String.format("%.2f", size);
		FontMetrics metrics = g2.getFontMetrics(f);
		g2.drawString(st, -metrics.stringWidth(st) / 2, -dim.height / 2 * size - 5);
		g2.setTransform(at);
	}

	@Override
	protected Shape getFOV() {
		return null;
	}

}
