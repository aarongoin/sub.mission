package bounce;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;

public class Asteroid extends FreeBody {
	
	private int strength;
	private Belt parent;
	private String type;

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
