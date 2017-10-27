package entities;

import java.util.HashMap;

import org.newdawn.slick.Graphics;

import core.CommercialManager;
import core.SubMission;
import jig.Entity;
import jig.Vector;

public class CommercialVessel extends Vessel {
	
	String collideWith[] = {"traffic", "patrol"};
	
	float shouldUpdate;
	CommercialManager control;
	
	public CommercialVessel(String image, Vector p, float noise, float bearing, CommercialManager control) {
		super(image, p, noise, bearing, 10, 5, 10);
		maxSpeed = 10;
		lookahead = 10;
		movedFor = new HashMap<Vessel, Float>();
		layer = "traffic";
		armor = 1;
		//debug = false;
		navi = new VesselNavigator(sprite.getWidth(), sprite.getHeight() * 1.5f, sprite.getHeight() / 4);
		this.control = control;
	}
	
	public void render(Graphics g) {
		sprite.setAlpha(drawAlpha);
		super.render(g);
	}
	
	@Override
	public void sink() {
		control.onSink();
		super.sink();
	}
	
	@Override
	public void update(float dt) {
		shouldUpdate += dt;
		if (shouldUpdate > 1) {
			shouldUpdate = 0;
			
			navi.update(currentBearing, getPosition());
			betterSteering(collideWith);
			//avoidLand();
		}
		//fieldNav(collideWith);
		//navigate(collideWith);
		super.update(dt);
	}
}
