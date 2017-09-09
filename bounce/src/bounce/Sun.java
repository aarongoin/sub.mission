package bounce;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Sun extends Attractor {
	
	Entity body;
	
	public Sun(final Vector pos, final float g) {
		super(pos, g);
		
		body = new Entity(pos);
		body.addImageWithBoundingBox( ResourceManager.getImage(BounceGame.SUN_RSC) );
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		body.rotate(0.5);
		
		if (!children.isEmpty()) {
			for (FreeBody child : children) {
				if ( Physics.didCollide( body.getPosition(), child.getPosition(), 20f, child.getRadius() ) ) {
					removeChild(child);
				}
			}
		}
	}
	
	public Vector acceleration(Vector P, float M, float dt) {
		return P.subtract(getPosition()).unit().scale(dt * getGravity() / M);
	}
	
	public void render(final Graphics g) {
		body.render(g);
		if (!children.isEmpty()) {
			for (FreeBody child : children) child.render(g);
		}
	}
}
