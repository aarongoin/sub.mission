package core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import entities.Submarine;
import jig.Vector;

public class SubPlatform {

	Image background = SubMission.getImage("sub_hull");
	Image torpedoFull = SubMission.getImage("torpedo_full");
	Image torpedoEmpty = SubMission.getImage("torpedo_empty");
	Image decoyFull = SubMission.getImage("nm_full");
	Image decoyEmpty = SubMission.getImage("nm_empty");
	
	Image sonarBtn = SubMission.getImage("sonar_btn");
	Image retractBtn = SubMission.getImage("retract_btn");
	Image decoyBtn = SubMission.getImage("decoy_btn");
	
	Submarine sub;
	
	int towableState;
	
	public SubPlatform(Submarine s) {
		sub = s;
		setState(0);
	}
	
	public void setState(int s) {
		towableState = s;
		switch(s) {
		case 0: // retracted
			retractBtn.setAlpha(1);
			retractBtn.setRotation(90);
			sonarBtn.setAlpha(0.5f);
			decoyBtn.setAlpha(0.5f);
			break;
		case 1: // sonar
			retractBtn.setAlpha(0.5f);
			retractBtn.setRotation(0);
			sonarBtn.setAlpha(1);
			decoyBtn.setAlpha(0.5f);
			break;
		case 2: // decoy
			retractBtn.setAlpha(0.5f);
			retractBtn.setRotation(0);
			sonarBtn.setAlpha(0.5f);
			decoyBtn.setAlpha(1);
			break;
		}
	}
	
	public int update(Input input, float dt) {
		Vector m = new Vector(input.getMouseX(), input.getMouseY());
		Vector b;
		
		if (!sub.decoyDeployed() && towableState == 2) {
			towableState = 0;
		}
		
		// used to reset values for when we've hovered over a button and changed it's opacity
		setState(towableState);
		
		if (sub.haveTowedSonar()) {
			// hovering over sonar button?
			b = new Vector(45.5f, SubMission.ScreenHeight - 80.5f);
			if (m.distance(b) < 17) {
				sonarBtn.setAlpha(1);
				if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					setState(1);
					return 2;
				}
			}
		}
		
		if (sub.getDecoys() > 0 || sub.haveTowedSonar()) {
			// hovering over retract button?
			b = new Vector(85.5f, SubMission.ScreenHeight - 80.5f);
			if (m.distance(b) < 17) {
				retractBtn.setAlpha(1);
				if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					setState(0);
					return 1;
				}
			}
		}
		
		if (sub.getDecoys() > 0) {				
			// hovering over decoy button?
			b = new Vector(125.5f, SubMission.ScreenHeight - 80.5f);
			if (m.distance(b) < 17) {
				decoyBtn.setAlpha(1);
				if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					setState(2);
					return 3;
				}
			}
		}
		
		return 0;
	}
	
	public void render(Graphics g) {
		g.drawImage(background, 10, SubMission.ScreenHeight - 160);
		
		// render torpedo arsenal
		int i;
		int n = sub.getTorpedoes();
		for (i = 1; i <= 8; i++) {
			g.drawImage((i > n ? torpedoEmpty : torpedoFull), 35 + (i * 11), SubMission.ScreenHeight - 136);
		}
		
		// render torpedo arsenal
		n = sub.getDecoys();
		for (i = 1; i <= 4; i++) {
			g.drawImage((i > n ? decoyEmpty : decoyFull), 59 + (i * 10), SubMission.ScreenHeight - 54);
		}
		
		g.drawImage(sonarBtn, 29, SubMission.ScreenHeight - 97);
		g.drawImage(retractBtn, 69, SubMission.ScreenHeight - 97);
		g.drawImage(decoyBtn, 109, SubMission.ScreenHeight - 97);
	}

}
