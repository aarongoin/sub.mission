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
	public boolean resetBall;
	Ball ball;
	
	public Sun(final Vector pos, final float g) {
		super(pos, g);
		
		body = new Entity(pos);
		body.addImageWithBoundingBox( ResourceManager.getImage(BounceGame.SUN_RSC) );
		
		resetBall = false;
	}
	
	public void removeDebris() {
		children.clear();
	}
	
	@Override
	public void addChild(FreeBody child) {
		if (child.getClass().getName() == "bounce.Ball") {
			ball = (Ball) child;
			resetBall = false;
		} else children.add(child);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);

		if (ball != null) {
			body.rotate(0.5);
			// Real acceleration
			Vector A = accelerateBall(dt);
			// Dampen tangential motion to Sun
			ball.setVelocity( ball.getVelocity().add(A) );
			ball.update(dt);
	
			if ( Physics.didCollide( body.getPosition(), ball.getPosition(), 20f, ball.getRadius() ) ) {
				ball.setLives(ball.getLives() - 1);
				if (ball.getLives() > 0) {
					resetBall = true;
				}
				ball = null;
			}
		}
			
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
	
	public Vector accelerateBall(float dt) { 
		Vector normal = ball.getPosition().subtract(getPosition());
		return normal.unit().scale((normal.length() > 80 ? 1 : 5) *dt * getGravity() / ball.getMass()); 
	}
	
	public void render(final Graphics g) {
		body.render(g);
		if (ball != null) ball.render(g);

		if (!children.isEmpty()) {
			for (FreeBody child : children) child.render(g);
		}
	}
}
