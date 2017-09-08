package bounce;

import org.newdawn.slick.Graphics;

import jig.Vector;

public class Belt extends Attractor {
	
	float radius;

	public Belt(Vector pos, float r, float g) {
		super(pos, g);
		radius = r;
	}

	public Vector acceleration(Vector P, float M, float dt) {
		Vector d = getPosition().subtract(P);
		if (d.length() > radius) d = d.scale(-1);
		return d.unit().scale(dt * getGravity() / M);
	}
	
	public void render(final Graphics g) {
		if (!children.isEmpty())
			for (FreeBody child : children) child.render(g);
	}
	
}