package bounce;

import java.util.ArrayList;

import jig.Vector;

public abstract class Attractor {
	
	private Vector position;
	private float gravity;
	protected ArrayList<FreeBody> children;
	
	public Attractor(final Vector pos, final float g) {
		gravity = g;
		position = pos;
		children = new ArrayList<FreeBody>();
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
	
	public abstract Vector acceleration(Vector P, float M, float dt);
	
	public void ballCollision(FreeBody ball) {
		for (FreeBody child : children) child.collision(ball);
	}
	
	public void update(float dt) {
		if (!children.isEmpty())
			for (FreeBody child : children) {
				// calculate acceleration on child
				Vector A = this.acceleration(child.getPosition(), child.getMass(), dt);
				child.setVelocity( child.getVelocity().add(A) );
				
				for (FreeBody other : children) {
					if (child != other) child.collision(other);
				}
				
				child.update(dt);
			}
	}
}
