package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Paddle extends Entity {
	
	/*
		
	*/
	float offset;
	float elasticity;
	Vector zero = new Vector(0f, -1f);

	public Paddle(Vector pos, float o, float e) {
		super(pos.getX(), pos.getY());
		addImageWithBoundingBox(ResourceManager.getImage(BounceGame.PADDLE_RSC), new Vector(0f, -o));
		
		offset = o;
		elasticity = e;
	}
	
	public Vector getNormal() {
		return zero.rotate(getRotation());
	}
	
	public void update(Vector mouse) {
		// calculate vector from position to mouse location
		Vector d = mouse.subtract(getPosition()).unit().scale(offset);
		setRotation(d.angleTo(zero) - 90);
	}
	
	public void reflectBall(FreeBody ball) {
		Vector normal = getNormal();
				
		ball.setVelocity( normal.scale(ball.getVelocity().length() * elasticity) );
		
		ResourceManager.getSound(BounceGame.CORK_SND).play();
	}

}
