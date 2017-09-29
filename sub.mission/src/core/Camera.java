package core;

import jig.Vector;

public class Camera {
	
	Vector position;
	
	public float width;
	public float height;
	
	float minX;
	float maxX;
	float minY;
	float maxY;

	public Camera(Vector p, float w, float h) {
		width = w;
		height = h;
		setPosition(p);
	}
	
	public void setPosition(Vector p) {
		position = p;
		minX = position.getX() - width / 2;
		maxX = minX + width;
		minY = position.getY() - height / 2;
		maxY = minY + height;
	}

	// return true if position is within camera bounds plus-or-minus some margin
	public boolean inBounds(Vector pos, float margin) {
		if ( pos.getX() > minX - margin && pos.getX() < maxX + margin &&
			 pos.getY() > minY - margin && pos.getY() < maxY + margin ) {
			return true;
		}
		
		return false;
	}
	
}
