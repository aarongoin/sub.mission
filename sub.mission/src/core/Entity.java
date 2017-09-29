package core;

import jig.Vector;

public abstract class Entity extends jig.Entity {
	
	String type;

	public Entity(String t, Vector position) {
		super(position);
		type = t;
	}

	public Entity(float x, float y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	public Entity() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract void update(final float dt);

}
