package core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import entities.Submarine;
import jig.Vector;

public class SubPlatform {

	Image background = SubMission.getImage("sub_hull");
	boolean blink;
	
	Image decoyBtn = SubMission.getImage("decoy_btn");
	Image decoyEmpty = SubMission.getImage("nm_empty");
	
	Image decoyFull = SubMission.getImage("nm_full");
	Image overlay = SubMission.getImage("sub_hull0");
	
	Image retractBtn = SubMission.getImage("retract_btn");
	Image sonarBtn = SubMission.getImage("sonar_btn");
	
	Image torpedoEmpty = SubMission.getImage("torpedo_empty");
	
	Image torpedoFull = SubMission.getImage("torpedo_full");
	int towableState;
	
	public SubPlatform() {
		blink = false;
		setState(0);
	}
	
	public void render(Graphics g) {
		g.drawImage(background, 10, SubMission.ScreenHeight - 160);
		if (SubMission.player.getArmor() == 1) {
			g.drawImage(overlay, 10, SubMission.ScreenHeight - 160);
		}
		
		// render torpedo arsenal
		int i;
		int n = SubMission.player.getTorpedoes();
		for (i = 1; i <= 8; i++) {
			g.drawImage((i > n ? torpedoEmpty : torpedoFull), 35 + (i * 11), SubMission.ScreenHeight - 136);
		}
		
		// render torpedo arsenal
		n = SubMission.player.getDecoys();
		for (i = 1; i <= 4; i++) {
			g.drawImage((i > n ? decoyEmpty : decoyFull), 59 + (i * 10), SubMission.ScreenHeight - 54);
		}
		
		g.drawImage(sonarBtn, 29, SubMission.ScreenHeight - 97);
		g.drawImage(retractBtn, 69, SubMission.ScreenHeight - 97);
		g.drawImage(decoyBtn, 109, SubMission.ScreenHeight - 97);
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
	
	public int update(Input input, float dt, boolean mouse) {
		Vector m = new Vector(input.getMouseX(), input.getMouseY());
		Vector b;
		
		if (!SubMission.player.decoyDeployed() && towableState == 2) {
			towableState = 0;
		}
		if (!SubMission.player.haveTowedSonar() && towableState == 1) {
			towableState = 0;
		}
		
		// blink overlay
		if (SubMission.player.getArmor() == 1) {
			float a = overlay.getAlpha();
			if (blink) {
				overlay.setAlpha(a * 0.95f);
				if (a <= 0.1) blink = false;
			} else {
				overlay.setAlpha(a * 1.15f);
				if (a >= 0.99) blink = true;
			}
		}
		
		// used to reset values for when we've hovered over a button and changed it's opacity
		setState(towableState);
		
		if (SubMission.player.haveTowedSonar()) {
			// hovering over sonar button?
			b = new Vector(45.5f, SubMission.ScreenHeight - 80.5f);
			if (m.distance(b) < 17) {
				sonarBtn.setAlpha(1);
				if (mouse) {
					setState(1);
					return 2;
				}
			}
		}
		
		if (SubMission.player.getDecoys() > 0 || SubMission.player.haveTowedSonar()) {
			// hovering over retract button?
			b = new Vector(85.5f, SubMission.ScreenHeight - 80.5f);
			if (m.distance(b) < 17) {
				retractBtn.setAlpha(1);
				if (mouse) {
					setState(0);
					return 1;
				}
			}
		}
		
		if (SubMission.player.getDecoys() > 0) {				
			// hovering over decoy button?
			b = new Vector(125.5f, SubMission.ScreenHeight - 80.5f);
			if (m.distance(b) < 17) {
				decoyBtn.setAlpha(1);
				if (mouse) {
					setState(2);
					return 3;
				}
			}
		}
		
		return 0;
	}

}
