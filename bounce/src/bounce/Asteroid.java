package bounce;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;

public class Asteroid extends FreeBody {
	
	int strength;
	Belt parent;

	public Asteroid(Vector pos, Vector vel, float m, float r, int s) {
		super(pos, vel, m, r);
				
		//addShape(new ConvexPolygon(r), c, c);
		addImageWithBoundingBox(ResourceManager.getImage(BounceGame.ASTEROID_RSC));
		
		strength = s;
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
