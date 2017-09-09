package bounce;

import java.util.ArrayList;

import jig.Vector;

public abstract class Attractor {
	
	private Vector position;
	private float gravity;
	protected ArrayList<FreeBody> children;
	protected ArrayList<FreeBody> toRemove;
	
	public Attractor(final Vector pos, final float g) {
		gravity = g;
		position = pos;
		children = new ArrayList<FreeBody>();
		toRemove = new ArrayList<FreeBody>();
	}
	
	public int getCount() {
		return children.size();
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public float getGravity() {
		return gravity;
	}
	
	public void addChild(FreeBody c) {
		children.add(c);
	}
	
	public void removeChild(FreeBody c) {
		toRemove.add(c);
	}
	
	public abstract Vector acceleration(Vector P, float M, float dt);
	
	public void ballCollision(FreeBody ball) {
		for (FreeBody child : children) child.collision(ball);
	}
	
	public void update(float dt) {
		if (!children.isEmpty()) {
			for (FreeBody child : children) {
				// calculate acceleration on child
				Vector A = this.acceleration(child.getPosition(), child.getMass(), dt);
				child.setVelocity( child.getVelocity().add(A) );
				
				for (FreeBody other : children) {
					if (child != other) child.collision(other);
				}
				
				child.update(dt);
			}
			if (!toRemove.isEmpty()) {
				for (FreeBody child : toRemove) children.remove(child);
				toRemove = new ArrayList<FreeBody>();
			}
		}
	}
}
