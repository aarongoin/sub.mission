package entities;



import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import util.VectorUtil;

public class VesselNavigator {

	Entity lane;
	
	float bearing;
	
	float hardToPort = -60;
	float toPort = -30;
	float toStarboard = 30;
	float hardToStarboard = 60;
	
	public VesselNavigator(float lane_width, float lane_height, float offset) {
		lane = new Entity();
		lane.addShape(new ConvexPolygon(lane_width, lane_height), new Vector(0, offset));
		//Entity.setDebug(true);
	}
	
	public void update(float bearing, Vector position) {
		this.bearing = bearing - 90;
		lane.setRotation(this.bearing);
		lane.setPosition(position);
	}
	
	public Vector getDirection() {
		return new Vector(1,0).setRotation(bearing);
	}
	
	public Entity getLane() { 
		return lane;
	}
	
	public boolean isBlockingLane(VesselNavigator other) {
		return (lane.collides(other.getLane()) != null);
	}
	
	public float turnToAvoid(VesselNavigator other) {
		Entity l = other.getLane();

		lane.setRotation(bearing + toStarboard);
		if (lane.collides(l) == null) {
			lane.setRotation(bearing);
			return toStarboard;
		}
		
		lane.setRotation(bearing + hardToStarboard);
		if (lane.collides(l) == null) {
			lane.setRotation(bearing);
			return hardToStarboard;
		}
		
		lane.setRotation(bearing + toPort);
		if (lane.collides(l) == null) {
			lane.setRotation(bearing);
			return toPort;
		}
		
		lane.setRotation(bearing + hardToPort);
		if (lane.collides(l) == null) {
			lane.setRotation(bearing);
			return hardToPort;
		}
		lane.setRotation(bearing);
		
		return 0; // should slowDown instead
	}
	
	public boolean isApproaching(VesselNavigator other) {
		Vector p = other.getLane().getPosition();
		return (lane.getPosition().distance(p) > lane.getPosition().add(new Vector(1, 0).setRotation(bearing)).distance(p));
	}
	
	public boolean shouldGiveWay(VesselNavigator other) {
		Vector direction = getDirection();
		Vector otherDirection = other.getDirection();
		Vector delta = other.getLane().getPosition().subtract(lane.getPosition());
		float theta = VectorUtil.getAngleBetween(direction, otherDirection);
		return ((Math.abs(VectorUtil.getAngleBetween(direction, delta)) < 90) && (theta < 0 || theta > 135));
	}
	
}
