package bounce;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

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
	
	public void update(float dt) {
		super.update(dt);
		body.rotate(0.5);
		
		ArrayList<FreeBody> destroy = new ArrayList<FreeBody>();
		
		if (!children.isEmpty())
			for (FreeBody child : children) {
				if ( Physics.didCollide( body.getPosition(), child.getPosition(), 20f, child.getRadius() ) ) {
					destroy.add(child);
				} else child.update(dt);
			}
		if (!destroy.isEmpty())
			for (FreeBody child : destroy) {
				removeChild(child);
			}
	}
	
	public void render(final Graphics g) {
		body.render(g);
		if (!children.isEmpty())
			for (FreeBody child : children) child.render(g);
	}
}
