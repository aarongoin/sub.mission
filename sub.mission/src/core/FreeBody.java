package core;

import jig.Vector;
import jig.Entity;

public abstract class FreeBody extends Entity {

	public float mass;
	protected float radius;
	protected float traction;
	private Vector velocity;
	
	public FreeBody(final Vector pos, final float m, final float r) {
		super(pos);
		
		velocity = new Vector(0, 0);
		mass = m;
		radius = r;
	}
	
	public FreeBody(final Vector pos, final Vector vel, final float m, final float r) {
		super(pos);

		velocity = vel;
		mass = m;
		radius = r;
	}

	public void collision(final Entity other) {
		if (super.collides(other) != null) onCollide(other);
	}
/*
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
	}*/

	public float getMass() {
		return mass;
	}
	
	public float getRadius() {
		return radius;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	abstract public void onCollide(Entity other);

	public void setMass(final float m) {
		mass = m;
	}

	public void setRadius(final float r) {
		radius = r;
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}
	
	/**
	 * Update the FreeBody based on how much time has passed...
	 * 
	 * @param delta
	 *            the number of milliseconds since the last update
	 */
	public void update(final float delta, final float friction) {
		translate(velocity.scale(delta * (1 - traction * friction) ));
	}
}
