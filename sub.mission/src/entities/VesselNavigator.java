package entities;


import org.newdawn.slick.Graphics;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import util.VectorUtil;

public class VesselNavigator {

	Entity lane;
	
	float bearing;
	
	float hardToPort = -60;
	float toPort = -30;
	float toStarboard = 30;
	float hardToStarboard = 60;
	
	public VesselNavigator(float lane_width, float lane_height) {
		lane = new Entity();
		lane.addShape(new ConvexPolygon(lane_width, lane_height), new Vector(0, 3*lane_height / 4));
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
		lane.setRotation(bearing + toPort);
		if (lane.collides(l) == null) {
			return toPort;
		}
		
		lane.setRotation(bearing + toStarboard);
		if (lane.collides(l) == null) {
			return toStarboard;
		}
		
		lane.setRotation(bearing + hardToStarboard);
		if (lane.collides(l) == null) {
			return hardToStarboard;
		}
		
		lane.setRotation(bearing + hardToPort);
		if (lane.collides(l) == null) {
			return hardToPort;
		}
		lane.setRotation(bearing);
		return 0; // should slowDown instead
	}
	
	public boolean isApproaching(VesselNavigator other) {
		Vector p = other.getLane().getPosition();
		return (lane.getPosition().distance(p) < lane.getPosition().add(new Vector(1, 0).setRotation(bearing)).distance(p));
	}
	
	public boolean shouldGiveWay(VesselNavigator other) {
		float theta = VectorUtil.getAngleBetween(getDirection(), other.getDirection());
		return (theta < 0 || theta > 135);
	}
	
}
