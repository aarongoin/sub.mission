package bounce;

import jig.ResourceManager;
import jig.Vector;

public class Asteroid extends FreeBody {
	
	private int strength;
	private Belt parent;
	private String type;
	Vector zero = new Vector(0f, -1f);

	public Asteroid(String t, Vector pos, Vector vel, float r) {
		super(pos, vel, 1, r);
				
		type = t;
		switch (type) {
			case "S":
				addImageWithBoundingBox(ResourceManager.getImage(BounceGame.ASTEROID_S_RSC));
				mass = 3;
				strength = 2;
				break;
			case "M":
				addImageWithBoundingBox(ResourceManager.getImage(BounceGame.ASTEROID_M_RSC));
				mass = 6;
				strength = 3;
				break;
			case "C":
				addImageWithBoundingBox(ResourceManager.getImage(BounceGame.ASTEROID_C_RSC));
				mass = 4;
				strength = 1;
				break;
		}
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		// rotate to stay facing the center
		Vector toSun = (new Vector(650, 400)).subtract(getPosition());
		setRotation(toSun.angleTo(zero) - 90);
	}
	
	public String getType() {
		return type;
	}
	
	public void setParent(Belt p) {
		parent = p;
	}

	@Override
	public void onCollide(FreeBody other) {
		if (other.getClass().getName() == "bounce.Ball") {
			// be destroyed?
			strength -= 1;
			if (strength <= 0) parent.destroy(this);
		}
	}
}
