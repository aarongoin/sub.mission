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
	
	public void update(float dt) {
		for (FreeBody child : children) {
			// calculate acceleration on child
			Vector A = child.getPosition().subtract(getPosition()).unit().scale(dt * getGravity() / child.getMass());
			child.setVelocity( child.getVelocity().add(A) );
		}
	}
}
