package bounce;

import java.util.Random;

import org.newdawn.slick.Graphics;

import jig.Vector;

public class Belt extends Attractor {
	
	private static Random random;
	float radius;
	Sun sun;

	public Belt(Vector pos, float g, float r, Sun s) {
		super(pos, g);
		radius = r;
		random = new Random( System.currentTimeMillis() );
		sun = s;
	}

	@Override
	public Vector acceleration(Vector P, float M, float dt) {
		Vector d = getPosition().subtract(P);
		//System.out.println("d: " + d.length() + " r: " + radius);
		if (d.length() > radius) d = d.scale(-1);
		d = d.unit().scale(dt * getGravity() / M);
		//System.out.println("Acceleration: " + d);
		return d;
	}
	
	public void clampChildToBelt(Asteroid c) {
		//System.out.println("Child position: " + c.getPosition());
		//System.out.println("Belt position: " + getPosition());
		float dist = c.getPosition().distance(getPosition());
		//System.out.println("Distance: " + dist);

		Vector normal = c.getPosition().subtract(getPosition()).unit();
		
		Vector offset = normal.scale(radius).subtract( normal.scale(dist) ).scale( (float) (0.95 + random.nextFloat() * (1.05 - 0.95)) );
			
		c.setPosition( c.getPosition().add(offset) );
		//System.out.println("Adjusted position: " + c.getPosition());
	}
	
	public void addChild(Asteroid c) {
		clampChildToBelt(c);
		c.setParent(this);
		children.add(c);
	}
	
	public void destroy(Asteroid c) {
		removeChild(c);
		// create debris
		int n = (int) c.getMass();
		//System.out.println("Creating " + n + " Debris.");
		for (int i = n; i > 0; i--) sun.addChild( new Debris(c.getPosition().add(new Vector(random.nextFloat(), random.nextFloat())), c.getVelocity().scale(-1), c.getMass() / (n * n), 2f) );
	}
	
	public void render(final Graphics g) {
		if (!children.isEmpty())
			for (FreeBody child : children) child.render(g);
	}
	
}