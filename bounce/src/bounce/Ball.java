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
	 
	Vector zero = new Vector(0f, -1f);

	private int lives;

	public Ball(final float x, final float y, final float vx, final float vy, final float m) {
		super(new Vector(x, y), new Vector(vx, vy), m, 5.0f);
		addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BALL_RSC));
		lives = 3;
	}
	
	public void setLives(int l) {
		lives = l;
	}
	
	public int getLives() {
		return lives;
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
		// rotate to stay facing the center
		Vector toSun = (new Vector(650, 400)).subtract(getPosition());
		setRotation(toSun.angleTo(zero) - 90);
	}
	
	public void onCollide(FreeBody other) {
	}
}
