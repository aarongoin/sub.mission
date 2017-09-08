package bounce;

import org.newdawn.slick.Graphics;

import jig.Vector;

public class Belt extends Attractor {
	
	float radius;

	public Belt(Vector pos, float g, float r) {
		super(pos, g);
		radius = r;
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
	
	public void render(final Graphics g) {
		if (!children.isEmpty())
			for (FreeBody child : children) child.render(g);
	}
	
}