package core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This state is active prior to the Game starting. In this state, sound is
 * turned off, and the bounce counter shows '?'. The user can only interact with
 * the game by pressing the SPACE key which transitions to the Playing State.
 * Otherwise, all game objects are rendered and updated normally.
 * 
 * Transitions From (Initialization), GameOverState
 * 
 * Transitions To PlayingState
 */
class MenuState extends BasicGameState {
	
	Button back;
	int d;
	int dir;
	
	Button help;
	Button next;
	Button quit;
	
	Button start;
	int substate;
	
	float t;
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SubMission G = (SubMission) game;
		// start background sound looping
		G.bg.loop();
		t = 0;
		d = 0;
		dir = 1;
		
		back = new Button(container, SubMission.text, "Back", SubMission.ScreenWidth / 2 - 200, SubMission.ScreenHeight - 100, 8);
		next = new Button(container, SubMission.text, "Next", SubMission.ScreenWidth / 2 + 200, SubMission.ScreenHeight - 100, 8);
		
		start = new Button(container, SubMission.text, "Start", SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2, 8);
		help = new Button(container, SubMission.text, "Instructions", SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2 + 60, 8);
		quit = new Button(container, SubMission.text, "Quit", SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2 + 120, 8);
		
		substate = 0;
		
		SubMission.missionManager = new CommercialMission(); // new SealTeamMission();
	}
	
	@Override
	public int getID() {
		return SubMission.MENUSTATE;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException { 
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		switch(substate) {
		case 0:
			g.setFont(SubMission.title);
			g.drawString("sub.mission", SubMission.ScreenWidth / 2 - 120, SubMission.ScreenHeight / 5 - 12);
			
			g.setFont(SubMission.text);
			g.drawString("by: Aaron Goin", SubMission.ScreenWidth / 2 - 50, SubMission.ScreenHeight / 5 + 40);
			
			start.render(g);
			help.render(g);
			quit.render(g);
			break;
		case 1:
			SubMission.missionManager.renderMission(g);
			
			back.render(g);
			next.render(g);
			break;
		case 2:
			g.setFont(SubMission.text);
			g.drawString("sub.mission is a 2D naval warfare game that puts the you in control of a nuclear submarine.", 300, 100);
			g.drawString("You must navigate an underwater landscape, use stealth to slip past enemy vessels hunting", 300, 130);
			g.drawString("you, or use the element of surprise to sink unwary surface ships. Enemy vessels and aircraft", 300, 160);
			g.drawString("will work together to find and sink you, as you work to complete one of several missions.", 300, 190);
			
			g.drawString("Use your mouse to navigate the map by altering your bearing, depth, and speed. Your submarine", 300, 250);
			g.drawString("is rated to a maximum depth of 600 meters. Diving below this point is highly dangerous and may", 300, 280);
			g.drawString("result in irreparable damage to your vessel. Speeds in excess of 35 knots can also result in", 300, 310);
			g.drawString("irreparable damage to your vessel. You may exceed 20 knots, but be careful--speeds greater than", 300, 340);
			g.drawString("20 knots create excess hull cavitation, thus making you much easier to detect.", 300, 370);
			
			back.render(g);
			next.render(g);
			break;
		case 3:
			g.setFont(SubMission.text);
			g.drawString("The blue on the map is water, the grey (or red) areas are land. You move below the surface of", 300, 100);
			g.drawString("the water, ships move at the surface, and aircraft fly at various levels above the map.", 300, 130);
			g.drawString("Land will appear red when you are at depth less than 100 meters. You are most easily detected", 300, 160);
			g.drawString("during that time, so be wary. The different shades of blue correspond to the deepest water in", 300, 190);
			g.drawString("that area--darker is deeper. At depths below 100 meters, land is grey, and more shallow waters", 300, 220);
			g.drawString("are outlined in white. Should you cross the white line, or cross into land, you will run", 300, 250);
			g.drawString("aground. Running aground at speeds above 10 knots will incapacitate your sub and cause you to", 300, 280);
			g.drawString("lose the game. At speeds below 10 knots, running aground will damage your hull and every enemy", 300, 310);
			g.drawString("ship will hear it.", 300, 340);
			
			g.drawString("Enemy vessels are patrolling these waters, employing sonar to listen for any hint of your", 300, 400);
			g.drawString("movements. Run slow and deep to maximize your stealth and avoid detection. Heavy ship traffic", 300, 430);
			g.drawString("increases the ambient noise in the area, reducing the sonar detection for all vessels which", 300, 460);
			g.drawString("includes your own. Commercial ships are especially noisy, which may benefit you.", 300, 490);

			g.drawString("The thermocline extends from 100 meters down to 400 meters. Operating your vessel above the", 300, 550);
			g.drawString("middle of the thermocline is risky as you are more likely to be discovered by surface ships.", 300, 580);
			g.drawString("However, running below the thermocline will limit your ability to detect surface vessels.", 300, 610);
			g.drawString("Deploy your towable sonar array to boost your sonar, and eliminate your blind spot.", 300, 640);
			
			back.render(g);
			next.render(g);
			break;
		case 4:
			g.setFont(SubMission.text);
			g.drawString("When an enemy vessels thinks they have a lock on your submarine, they will fire torpedoes at", 300, 100);
			g.drawString("you. If you cannot outrun, or outmaneuver incoming torpedoes, you may deploy a towable decoy.", 300, 130);
			g.drawString("Decoys present a more attractive target to incoming torpedoes, and thus diverts their path.", 300, 160);

			g.drawString("Enemy aircraft can be especially deadly. Airplanes will drop a line of sonobuoys on the", 300, 220);
			g.drawString("surface that employ active sonar to try and discover your position. Helicopters can dip their", 300, 250);
			g.drawString("sonobuoy down below the thermocline and discover even the deepest submarine. Enemy aircraft", 300, 280);
			g.drawString("can be seen when at depths less than 100 meters thanks to your periscope.", 300, 310);
			
			g.drawString("You are not a tank, and cannot hope to repel more than a single torpedo. Two will very likely", 300, 370);
			g.drawString("sink you. But you are not toothless. You carry a deadly arsenal of torpedoes. Click on a ship", 300, 400);
			g.drawString("to try and target it. If you successfully target a ship, a torpedo is fired targeting that", 300, 430);
			g.drawString("class of vessel at an expected location. All torpedoes will enter a circling search pattern", 300, 460);
			g.drawString("if they fail to detect their target when they arrive. If they do detect their target, they", 300, 490);
			g.drawString("will employ active sonar to accurately target it. If they run out of fuel, they will sink", 300, 520);
			g.drawString("harmlessly to the sea floor.", 300, 550);
			
			back.render(g);
			break;
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		//float dt = delta / 1000f;
		
		Input input = container.getInput();
		if (substate == 0) {
			if (help.clicked(input))
				substate = 2;
			else if (quit.clicked(input))
				container.exit();
			else if (start.clicked(input))
				substate = 1;
				
		} else if (substate == 1) {
			if (next.clicked(input))
				game.enterState(SubMission.PLAYINGSTATE);
			else if (back.clicked(input))
				substate = 0;
			
		} else {
			if (back.clicked(input))
				substate = 0;
			else if (substate < 4 && next.clicked(input))
				substate += 1;
		}
	}
	
}