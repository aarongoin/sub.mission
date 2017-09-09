package bounce;

import jig.ResourceManager;
import jig.Vector;

public class Debris extends FreeBody {

	String type;
	
	public Debris(String t, Vector pos, Vector vel, float m, float r) {
		super(pos, vel, m, r);

		type = t;
		switch (type) {
			case "S":
				addImageWithBoundingBox(ResourceManager.getImage(BounceGame.DEBRIS_S_RSC));
				break;
			case "M":
				addImageWithBoundingBox(ResourceManager.getImage(BounceGame.DEBRIS_M_RSC));
				break;
			case "C":
				addImageWithBoundingBox(ResourceManager.getImage(BounceGame.DEBRIS_C_RSC));
				break;
		}
	}

	@Override
	public void onCollide(FreeBody other) {
	}

}
