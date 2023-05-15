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

import processing.core.PVector;
import util.Util;

public class Prey extends Animal{
	
	private Ellipse2D.Double body, head, eye, wings;
	protected Arc2D.Double stripe1, stripe2, fov;
	protected Line2D.Double ant;
	protected Polygon sting;
	//private float wing, vangle;
	
	
	public Prey(float x, float y, float size) {
		super(x, y, 100, 80, size);
		this.color = Util.randomColor();
		wing = (float) (Math.PI/12);
		vangle = 0;
	}

	@Override
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
	protected boolean eatable(SimulationObject food) {
		return (food instanceof Food);
	}
	
//	public boolean sees(Predator b) {
//		return (getFOV().intersects(b.getFOV().getBounds2D()) && b.getFOV().intersects(getFOV().getBounds2D()) );
//	}
	
	public void avoid(Predator b) {
		float coef = 0.05f;
		PVector direction = PVector.sub(pos, b.getPos()).normalize();
		PVector acceleration = PVector.mult(direction, speedMag*coef);
		speed.add(acceleration);
	}

	@Override
	public void draw(Graphics2D g2) {
		AffineTransform az = g2.getTransform();	
		if(vangle >= -(float)(Math.PI/4) || vangle <= (float)(Math.PI/4)){
			wing = -wing;
		}
		vangle += wing;
		
		g2.translate(pos.x, pos.y);
		g2.rotate(speed.heading()); 
		g2.scale(size,  size);
        if (speed.x < 0) g2.scale(1, -1);
        
        //bee stinger
        AffineTransform ab = g2.getTransform();
        g2.setColor(new Color(255, 255, 255));
        int [ ] x = {0,0,-70};
		int [ ] y = {-10,10,0};
		g2.fillPolygon(x, y, 3);
        g2.setTransform(ab);
        
        //bee body and head
        AffineTransform af = g2.getTransform();
        if(state == SICK || state == DEATH) g2.setColor(Color.LIGHT_GRAY);
		else g2.setColor(color);
		g2.fill(body);
		
		g2.setColor(new Color(0, 0, 0));
		g2.draw(body);
		
		g2.fill(stripe1);
		g2.fill(stripe2);
		
		if(state == SICK || state == DEATH) g2.setColor(Color.LIGHT_GRAY);
		else g2.setColor(color);
		g2.fill(head);
		
		g2.setColor(new Color(0, 0, 0));
		g2.draw(head);
		
		g2.setTransform(af);
		
		//be wing 1
		AffineTransform wu = g2.getTransform();
        g2.rotate(vangle);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(wings);
		
		g2.setColor(new Color(0, 0, 0));
		g2.draw(wings);
		
		g2.setTransform(wu);
		
		//bee wing 2
		AffineTransform wd = g2.getTransform();
		g2.scale(1, -1);
        g2.rotate(vangle);
		g2.setColor(new Color(255, 255, 255));
		g2.fill(wings);
		
		g2.setColor(new Color(0, 0, 0));
		g2.draw(wings);
		
		g2.setTransform(wd);
		
		AffineTransform ay = g2.getTransform();
		g2.setColor(new Color(0, 0, 0));
		g2.fill(eye);
		g2.scale(1,  -1);
		g2.fill(eye);
		
		g2.setTransform(ay);
		
		//g2.translate((int)bodyW,(int)bodyH/25);
		
		AffineTransform ad = g2.getTransform();
		g2.translate(dim.width, 0);
		g2.rotate(Math.PI/6);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(ant);
		
		g2.setTransform(ad);
		
		AffineTransform ai = g2.getTransform();
		g2.translate(dim.width, 0);
		g2.scale(1,  -1);
		
		g2.rotate(Math.PI/6);
		g2.setColor(new Color(0, 0, 0));
		g2.draw(ant);
		
		g2.setTransform(ai);
		
//		
//		g2.setColor(Color.red);
//		g2.draw(fov);
		
		g2.setTransform(az);
		
	}

	@Override
	protected void setShapeAttributes() {
		body = new Ellipse2D.Double(-dim.width/2, -dim.height/2, dim.width, dim.height);
		head = new Ellipse2D.Double(dim.width/4, -dim.height/2, 3*dim.width/4, 3*dim.width/4);
		
		stripe1 = new Arc2D.Double(-dim.width/4, -dim.height/2, dim.width/2, dim.height, -90, -180, Arc2D.PIE);
		stripe2 = new Arc2D.Double(-dim.width/4, -dim.height/2, dim.width/2, dim.height, -90, 180, Arc2D.PIE);
		
		wings = new Ellipse2D.Double(0, 0, (int)(dim.height/4), dim.width);
		
		//ant1 = new Arc2D.Double(0, 0, dim.width/3, dim.height/10, 0, 180, Arc2D.PIE);
		ant = new Line2D.Double(0, 0, dim.width/2, 0);
		
		eye = new Ellipse2D.Double((3*dim.width/4) - dim.width/30, dim.height/4 - dim.width/30, dim.width/15, dim.height/15);
		//eye2 = new Rectangle2D.Double((3*dim.width/4) - dim.width/30, dim.height/4 - dim.width/30, dim.width/15, dim.height/15);
		
		int [ ] x = {0,0,-(7*dim.width/100)};
		int [ ] y = {-(dim.width/10),(dim.width/10),0};
		sting = new Polygon(x, y, 3);
		
	}

	@Override
	protected void setOutline() {
		outline = new Area(body);
		outline.add(new Area(head));
		
	}
	
	

	@Override
	protected Shape getOutline() {
		AffineTransform at = new AffineTransform();
		at.translate(pos.x, pos.y);
		at.rotate(speed.heading());
		at.scale(size, size);
		return at.createTransformedShape(outline);
	}

	@Override
	protected Shape getFOV() {
		// TODO Auto-generated method stub
		return null;
	}
}
