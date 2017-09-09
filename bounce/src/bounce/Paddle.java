package bounce;

import org.newdawn.slick.Image;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Paddle extends Entity {
	
	/*
		
	*/
	float offset;
	Vector zero = new Vector(0f, -1f);

	public Paddle(Vector pos, float o) {
		super(pos.getX(), pos.getY());
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.PADDLE_RSC), new Vector(0f, -o));
		
		offset = o;
	}
	
	public void update(Vector mouse) {
		// calculate vector from position to mouse location
		Vector d = mouse.subtract(getPosition()).unit().scale(offset);
		setRotation(d.angleTo(zero) - 90);
	}

}
