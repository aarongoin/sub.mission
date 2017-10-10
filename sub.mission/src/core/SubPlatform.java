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
	
	Submarine sub;
	
	public SubPlatform(Submarine s) {
		sub = s;
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
	}

}
