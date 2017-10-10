package core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import entities.Submarine;

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
