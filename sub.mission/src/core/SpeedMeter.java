package core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import jig.Vector;

public class SpeedMeter {
	
	int mark;
	int target;
	int hover;
	int speed;
	String depth;
	Vector position;

	public SpeedMeter(int value, Vector pos) {
		position = pos;
		hover = markFromSpeed(value);
		mark = hover;
		target = hover;
		speed = value;
	}
	
	int speedFromMark(int hover) {
		return (int) ((379 - hover) / 8.422222);
	}
	
	int markFromSpeed(int speed) {
		return (int) (379 - 8.4222*speed);
	}
	
	public void render(Graphics g) {
		g.drawImage(SubMission.getImage("speed"), position.getX(), position.getY());
		g.drawImage(SubMission.getImage("marker"), position.getX() + mark - 3, position.getY() + 2);
		
		g.drawImage(SubMission.getImage("speed_target"), position.getX() + hover - 3, position.getY() - 10);
		g.setFont(SubMission.text);
		if (speed > 9)
			g.drawString(Integer.toString(speed), position.getX() + hover - 9, position.getY() - 30);
		else
			g.drawString(Integer.toString(speed), position.getX() + hover - 3, position.getY() - 30);
	}
	
	// returns target speed
	public int update(Input input, int current) {
		
		mark = markFromSpeed(current);
		
		int x = input.getMouseX();
		int y = input.getMouseY();
		if (position.getX() - 12 < x && x < (position.getX() + 379) &&
			position.getY() - 2 < y && y < (position.getY() + 24)
		) {
			hover = x - (int) position.getX();
			if (hover > 379) hover = 379;
			else if (hover < 0) hover = 0;
			speed = speedFromMark(hover);
			hover = markFromSpeed(speed);
			if (input.isMousePressed(input.MOUSE_LEFT_BUTTON))
				target = hover;
		} else {
			hover = target;
			speed = speedFromMark(target);
		}
		
		return speedFromMark(target);
	}
}
