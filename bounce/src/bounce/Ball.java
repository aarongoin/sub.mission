package bounce;

import bounce.FreeBody;
import jig.ResourceManager;
import jig.Vector;

/**
 * The Ball class is an Entity that has a velocity (since it's moving). When
 * the Ball bounces off a surface, it temporarily displays a image with
 * cracks for a nice visual effect.
 * 
 */
 class Ball extends FreeBody {

	private int countdown;

	public Ball(final float x, final float y, final float vx, final float vy, final float m) {
		super(new Vector(x, y), new Vector(vx, vy), m, 5.0f);
		addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BALL_RSC));
		countdown = 0;
	}

	/**
	 * Update the Ball based on how much time has passed...
	 * 
	 * @param delta
	 *            the number of milliseconds since the last update
	 */
	public void update(final float delta) {
		super.update(delta);
		setVelocity(getVelocity().clampLength(0, 10f));
	}
	
	public void onCollide(FreeBody other) {
	}
}
