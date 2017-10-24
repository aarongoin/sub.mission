package core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import entities.Torpedo;
import entities.Vessel;
import jig.Entity;
import jig.Vector;

public class DepthMeter {
	
	String depth;
	int hover;
	int mark;
	Vector position;
	int target;
	
	Image torpedo;

	public DepthMeter(int value, Vector pos) {
		position = pos;
		hover = value;
		mark = value;
		target = value;
		torpedo = SubMission.getImage("torpedo_marker");
	}
	
	
	public void render(Graphics g) {
		g.drawImage(SubMission.getImage("depth"), position.getX(), position.getY());
		g.drawImage(SubMission.getImage("marker"), position.getX() + 2, mark + 8);
		
		g.drawImage(SubMission.getImage("depth_target"), position.getX() - 10, hover + 8);
		g.setFont(SubMission.text);
		if (hover > 99)
			g.drawString(Integer.toString(hover), position.getX() - 42, hover + 1);
		else if (hover > 9)
			g.drawString(Integer.toString(hover), position.getX() - 32, hover + 1);
		else
			g.drawString(Integer.toString(hover), position.getX() - 22, hover + 1);
		
		for (Entity e : SubMission.getLayer("torpedo")) {
			g.drawImage(torpedo, position.getX() + 3, ((Vessel) e).getDepth() + 8);
		}
	}
	
	// returns target depth
	public int update(Input input, int current) {
		
		mark = current;
		
		int x = input.getMouseX();
		int y = input.getMouseY();
		if (position.getX() - 2 < x && x < (position.getX() + 24) &&
			position.getY() - 12 < y && y < (position.getY() + 800)
		) {
			hover = y - (int) position.getY();
			if (hover > 795) hover = 795;
			else if (hover < 5) hover = 5;
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				target = hover;
		} else {
			hover = target;
		}
		
		return target;
	}
}
