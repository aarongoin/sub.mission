package bounce;

import jig.ResourceManager;
import jig.Vector;

public class Debris extends FreeBody {

	public Debris(Vector pos, Vector vel, float m, float r) {
		super(pos, vel, m, r);
		
		addImageWithBoundingBox(ResourceManager.getImage(BounceGame.DEBRIS_RSC));
	}

	@Override
	void onCollide(FreeBody other) {
	}

}
