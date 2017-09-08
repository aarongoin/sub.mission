package bounce;

import java.util.ArrayList;

import jig.Vector;

public class Attractor {
	
	private Vector position;
	private float gravity;
	ArrayList<FreeBody> children;
	
	public Attractor(final Vector pos, final float g) {
		gravity = g;
		position = pos;
		children = new ArrayList();
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public float getGravity() {
		return gravity;
	}
	
	protected void addChild(FreeBody c) {
		children.add(c);
	}
	
	protected void removeChild(FreeBody c) {
		children.remove(c);
	}
	
	public Vector acceleration(Vector P, float M, float dt) {
		return P.subtract(getPosition()).unit().scale(dt * getGravity() / M);
	}
	
	public void update(float dt) {
		if (!children.isEmpty())
			for (FreeBody child : children) {
				// calculate acceleration on child
				Vector A = acceleration(child.getPosition(), child.getMass(), dt);
				child.setVelocity( child.getVelocity().add(A) );
			}
	}
}
