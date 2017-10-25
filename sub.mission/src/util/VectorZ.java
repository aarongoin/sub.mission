package util;

import jig.Vector;

public class VectorZ {
	
	protected float x;
	protected float y;
	protected float z;
	
	public VectorZ(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public VectorZ(Vector xy, float z) {
		this.x = xy.getX();
		this.y = xy.getY();
		this.z = z;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public Vector getVectorXY() {
		return new Vector(x, y);
	}
	
	public void pointTo(VectorZ other, float length) {
		x = other.getX();
		y = other.getY();
		z = other.getZ();
		
		float scale = (float) ( length / Math.sqrt( x*x + y*y + z*z ));
		
		x *= scale;
		y *= scale;
		z *= scale;
	}
	
	public float distance(VectorZ other) {
		float dx = other.getX() - x;
		float dy = other.getY() - y;
		float dz = other.getZ() - z;
		return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
}
