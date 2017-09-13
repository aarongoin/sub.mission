package bounce;

import bounce.Physics;
import jig.Entity;
import jig.Vector;


public abstract class FreeBody extends Entity {

	private Vector velocity;
	public float mass;
	private float radius;
	
	public FreeBody(final Vector pos, final Vector vel, final float m, final float r) {
		super(pos);

		velocity = vel;
		mass = m;
		radius = r;
		
	}

	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	public void setMass(final float m) {
		mass = m;
	}

	public float getMass() {
		return mass;
	}

	public float getRadius() {
		return radius;
	}

	/**
	 * Update the FreeBody based on how much time has passed...
	 * 
	 * @param delta
	 *            the number of milliseconds since the last update
	 */
	public void update(final float delta) {
		translate(velocity.scale(delta));
		//System.out.print("Velocity: " + velocity.length() + "\n");
	}

	public void collision(FreeBody other) {
		
		float d = getPosition().distance(other.getPosition());
		float R = getRadius() + other.getRadius();
		// if they did collide
		if (d <= R) {
			
			//System.out.println("!!! COLLISION !!!");

			// rewind bodies back to where they perfectly collide (removing overlap)
			float dt = Physics.rewindToCollision(this, other);
			
			// calculate perfectly elastic collision (ignoring rotation and friction)
			Vector[] newVelocities = Physics.elasticCollision(getPosition(), other.getPosition(), getVelocity(), other.getVelocity(), getMass(), other.getMass());
			setVelocity(newVelocities[0]);
			other.setVelocity(newVelocities[1]);
			
			// undo our rewind, playing out their collision
			Physics.advanceToPresent(dt, this, other);
			
			// hook for custom collide events
			onCollide(other);
			other.onCollide(this);
		}
	}
	
	abstract public void onCollide(FreeBody other);
}
