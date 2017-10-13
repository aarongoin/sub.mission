package entities;

import java.util.HashMap;

import org.newdawn.slick.Graphics;

import jig.Vector;

public class CommercialVessel extends Vessel {
	
	public CommercialVessel(String image, Vector p, float noise, float bearing) {
		super(image, p, noise, bearing, 4, 5, 10);
		maxSpeed = 10;
		lookahead = 60f;
		movedFor = new HashMap<Vessel, Float>();
		layer = "traffic";
		armor = 1;
		//debug = false;
	}
	
	public void render(Graphics g) {
		sprite.setAlpha(drawAlpha);
		super.render(g);
	}
}
