package bounce;

import jig.ResourceManager;
import jig.Vector;

public class Debris extends FreeBody {

	public Debris(Vector pos, Vector vel, float m, float r) {
		super(pos, vel, m, r);
		//System.out.println("Pos: " + pos + " Vel: " + vel);
		addImageWithBoundingBox(ResourceManager.getImage(BounceGame.DEBRIS_RSC));
	}

	@Override
	public void onCollide(FreeBody other) {
	}

}
