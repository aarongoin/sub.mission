package entities;

import org.newdawn.slick.Graphics;

import jig.Vector;

public class CommercialVessel extends Vessel {

	public CommercialVessel(String image, Vector p, float noise, float bearing) {
		super(image, p, noise, bearing, 4, 1, 0.5f);
	}
	
	public void render(Graphics g) {
		sprite.setAlpha(drawAlpha);
		super.render(g);
	}
}
