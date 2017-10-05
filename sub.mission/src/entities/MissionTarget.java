package entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import core.SubMission;
import jig.Entity;
import jig.Vector;

public class MissionTarget extends Entity {

	float time;
	float timeRemaining;
	float percent;
	Image sprite;
	
	public MissionTarget(Vector p, float t) {
		super(p);
		
		sprite = SubMission.getImage("mission_target");
		addImageWithBoundingBox(sprite);
		
		timeRemaining = t;
		time = t;
		percent = 1f;
	}

	@Override
	public void render(Graphics g) {
		
		// render timer countdown
		g.setColor(new Color(1f, 1f, 0f, 0.25f));
		g.fillArc(getPosition().getX() - sprite.getWidth() / 2, getPosition().getY()  - sprite.getHeight() / 2, sprite.getWidth(), sprite.getHeight(), 0, (360 * timeRemaining / time));
		g.setColor(Color.white);
		
		super.render(g);
	}
	
	public void update(float dt) {
		timeRemaining -= dt;
		if (timeRemaining < 0f) timeRemaining = 0f;
		percent = timeRemaining / time;
	}
	
	public float getPercent() {
		return percent;
	}

}
