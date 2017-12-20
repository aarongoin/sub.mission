package core;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entities.Airplane;
import entities.MissionTarget;
import entities.Submarine;
import entities.Torpedo;
import entities.Vessel;
import jig.Entity;
import jig.Vector;

public class InstructionState extends BasicGameState {

	Button menu;
	Button next;
	Button prev;
	
	int state;
	
	DepthMeter depth;
	SpeedMeter speed;
	SubPlatform platform;
	
	Image airplane;
	Image patrol;
	Image commercial;
	Image torpedo;
	Image sonobuoy;
	
	String panelTitle = "";
	String[] text;
	
	MissionTarget mission;
	
	Vector playerStart = new Vector(630, 130);
	Vector playerSpawn = new Vector(SubMission.ScreenWidth - 200, 100);

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		menu = new Button(container, SubMission.text, "Menu", SubMission.ScreenWidth / 2 - 300, SubMission.ScreenHeight - 100, 8);
		next = new Button(container, SubMission.text, "Next", SubMission.ScreenWidth / 2 + 100, SubMission.ScreenHeight - 100, 8);
		prev = new Button(container, SubMission.text, "Prev", SubMission.ScreenWidth / 2, SubMission.ScreenHeight - 100, 8);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		Vector[] zones = { new Vector(SubMission.ScreenWidth - 200, SubMission.ScreenHeight - 200) };
		resetPlayer(playerStart, 0);
		
		depth = new DepthMeter((int) SubMission.player.getDepth(), new Vector(SubMission.ScreenWidth - 24, 12));
		speed = new SpeedMeter(0, new Vector(SubMission.ScreenWidth - 415, SubMission.ScreenHeight - 24));
		platform = new SubPlatform();
		
		airplane = SubMission.getImage("airplane");
		patrol = SubMission.getImage("patrol");
		commercial = SubMission.getImage("ship2");
		torpedo = SubMission.getImage("enemy_torpedo");
		sonobuoy = SubMission.getImage("sonobuoy_even");
		
		SubMission.patrolManager = new PatrolManager(zones, zones[0], new Vector(SubMission.ScreenWidth - 200, SubMission.ScreenHeight + 100));

		
		state = 0;
		stage();
		
	}
	
	public void stage() {
		switch (state) {
		case 0:
			resetPlayer(playerStart, 0);
			SubMission.player.setSpeed(0);
			panelTitle = "Welcome";
			text = panelA;
			break;
		case 1:
			resetPlayer(playerStart, 0);
			SubMission.player.setSpeed(0);
			panelTitle = "NPC Units";
			text = panelB;
			break;
		case 2:
			resetPlayer(playerStart, 0);
			SubMission.player.setSpeed(0);
			panelTitle = "Depth Meter";
			text = panelC;
			break;
		case 3:
			resetPlayer(playerStart, 0);
			panelTitle = "Speed Meter";
			text = panelD;
			break;
		case 4:
			resetPlayer(playerSpawn, 90);
			panelTitle = "Other Controls";
			text = panelE;
			break;
		case 5:
			resetPlayer(playerSpawn, 90);
			SubMission.patrolManager.reset();
			SubMission.patrolManager.setPatrol(3);
			SubMission.removeLayer("torpedo");
			SubMission.addLayer("torpedo");
			mission = new MissionTarget(new Vector(SubMission.ScreenWidth - 200, SubMission.ScreenHeight - 200), 60f);
			panelTitle = "Mission & Combat";
			text = panelF;
		}
	}
	
	public void resetPlayer(Vector pos, int bearing) {
		SubMission.player = new Submarine(90, 10, bearing, pos);
	}
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		g.setColor(Color.white);
		
		// render section title
		g.setFont(SubMission.subtitle);
		g.drawString(panelTitle, 250, 30);
		
		// render section text
		g.setFont(SubMission.text);
		int i = 0;
		int s = 30;
		int m = 100;
		for (String line : text) {
			g.drawString(line, 250, i*s + m);
			i += 1;
		}
		
		switch(state) {
		case 0: // intro
			SubMission.player.render(g);
			break;
			
		case 1: // enemies
			g.drawImage(commercial, 200, 90);
			g.drawImage(patrol, 205, 220);
			g.drawImage(airplane, 205, 330);
			g.drawImage(sonobuoy, 200, 400);
			g.drawImage(torpedo, 210, 470);
			break;
			
		case 2: // depth and bearing
			depth.render(g);
			break;
			
		case 3:
			speed.render(g);
			SubMission.player.render(g);
			break;
			
		case 4:
			platform.render(g);
			speed.render(g);
			SubMission.player.render(g);
			break;
		case 5:
			SubMission.patrolManager.render(g);
			SubMission.player.render(g);
			for (Entity e : SubMission.getLayer("torpedo")) {
				((Torpedo) e).render(g);
			}
			mission.render(g);
			
			depth.render(g);
			speed.render(g);
			platform.render(g);
			
			g.setFont(SubMission.text);
			g.setColor(Color.yellow);
			g.drawString("Get to the Objective!", 75f, 15f);
			g.setColor(Color.white);
			break;
		}
		
		menu.render(g);
		if (state > 0) prev.render(g);
		if (state < 5) next.render(g);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		SubMission G = (SubMission) game;
		float dt = delta / 1000f;
				
		// draw depth lines or land depending on submarine depth
		int d = (int) (SubMission.player.getDepth() / 100);
		if (d > 0) G.depth = SubMission.getImage("d" + d);
		else G.depth = SubMission.getImage("land");
		
		
		Input input = container.getInput();
		boolean mouse = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);

		if (state > 0 && prev.clicked(input, mouse)) {
			state -= 1;
			stage();
		} else if (state < 5 && next.clicked(input, mouse)) {
			state += 1;
			stage();
		} else if (menu.clicked(input, mouse))
			game.enterState(SubMission.MENUSTATE);
		
		switch (state) {
		case 0:
			SubMission.player.update(input, 0, dt, mouse);
			break;
		case 1:
			SubMission.player.update(input, 0, dt, mouse);
			break;
		case 2:
			SubMission.player.update(input, 0, dt, mouse);
			// don't let player kill themselves on tutorial
			if (SubMission.player.getDepth() > 450) {
				SubMission.player.setDepth(50);
				depth.target = 50;
			} else {
				SubMission.player.setDepth( depth.update(input, (int) SubMission.player.getDepth(), mouse) );
			}
			break;
		case 3:
			SubMission.player.update(input, 0, dt, mouse);
			if (SubMission.player.getPosition().distance(playerStart) > 200) resetPlayer(playerStart, 0); 
			SubMission.player.setSpeed( speed.update(input, (int) SubMission.player.getSpeed(), mouse) );
			break;
		case 4:
			SubMission.player.update(input, 0, dt, mouse);
			if (SubMission.player.getPosition().distance(playerSpawn) > 200) resetPlayer(playerSpawn, 90); 
			SubMission.player.setSpeed( speed.update(input, (int) SubMission.player.getSpeed(), mouse) );
			SubMission.player.setTowState( platform.update(input, dt, mouse) );
			break;
		case 5:
			mission.update(dt);
			if (SubMission.player.isSunk || mission.getPercent() <= 0f) stage();
			SubMission.patrolManager.update(dt, input, (SubMission.getLayer("patrol").size() + 5) * 20, mouse);
			SubMission.player.update(input, 0, dt, mouse);
			SubMission.player.setDepth( depth.update(input, (int) SubMission.player.getDepth(), mouse) );
			SubMission.player.setSpeed( speed.update(input, (int) SubMission.player.getSpeed(), mouse) );
			SubMission.player.setTowState( platform.update(input, dt, mouse) );
			
			for (Entity e : SubMission.getLayer("torpedo")) {
				((Torpedo) e).update(dt);
				if (((Vessel) e).didRunAground(SubMission.map) || !((Torpedo) e).haveFuel())
					SubMission.removeEntity("torpedo", e);
			}
			break;
		}

		G.update();
	}

	@Override
	public int getID() {
		return SubMission.INSTRUCTIONSTATE;
	}
	
	
	
	final String[] panelA = {
		"",
		"You control this nuclear submarine.",
		"",
		"Hover around the submarine and click to set your target bearing.",
		"",
		"",
		"",
		"The blue on the map is water, and is shaded darker with depth.",
		"",
		"Red areas are land, and will be colored red when at periscope depth (100 meters or higher), and grey otherwise.",
		"",
		"You move below the surface of the water, all other ships move at the surface, and aircraft fly above the map."
	};
	
	final String[] panelB = {
		"This is a commercial ship. It’s carrying goods to and from our enemies.",
		"Heavy ship traffic is noisy and helps to keep you concealed.",
		"",
		"This is an enemy patrol boat.",
		"They patrol sections of the map, using their sonar to listen for any submarine activity.",
		"They typically carry 2 heavy torpedoes onboard, and they're not shy about using them.", 
		"",
		"This is an enemy aircraft that will deploy sonobuoys across the map in an effort to find you.",
		"Aircraft can only be seen at periscope depth.",
		"",
		"Sonobuoys indicate their depth relative to your own, and can be seen from all depths.",
		"",
		"This is an enemy torpedo targeting your vessel."
	};
	final String[] panelC = {
		"On the right is your depth meter, which shows your current depth,",
		"and will show the depth of friendly and enemy torpedoes.",
		"",
		"Hover over the meter and click to set your target depth.",
		"",
		"Between 100 and 400 meters is the thermocline, where changing water temperature bends sound waves, giving",
		"submarines beneath it a stealth advantage against surface sonar.",
		"",
		"Below 600 meters is your crush depth, and diving below these depths may result in damage to your hull.",
		"",
		"As your depth changes, the map will change to outline more shallow waters in white.",
		"If you cross over the white line, or into land, you will run aground, and fail your mission."
	};
	
	final String[] panelD = {
		"",
		"",
		"",
		"",
		"",
		"In the bottom-right corner is your speed meter, which shows your current speed and works much like the depth meter.",
		"",
		"Hover over the meter and click to set your target speed.",
		"",
		"At speeds exceeding 20 knots, your engines make a great deal more noise, and drag forces will snap tow lines.",
		"",
		"Speeds exceeding 35 knots are possible, but may damage your vessel."
	};
	
	final String[] panelE = {
		"In the bottom left corner are other submarine controls, where you can",
		"see your current arsenal of torpedoes (white row of icons), and",
		"torpedo decoys (green row of icons).",
		"",
		"Deploying a torpedo decoy will draw enemy torpedoes away from your",
		"vessel. You start with a limited arsenal, so use them wisely.",
		"",
		"The three buttons in the center control your tow lines. The button on the left will deploy your towed",
		"sonar array, which will boost your sonar and eliminate the blind spot behind your vessel. The button on",
		"the right will deploy a torpedo decoy. The button in the center will retract all towed lines.",
		"",
		"Should you take damage, the control panel will flash red. Taking any more damage will sink your submarine."
	};
	final String[] panelF = {
		"Mission objectives are shown in yellow text in top left corner.",
		"If your objective is to get to a specific location, a yellow circle will",
		"mark the location. If the objective is timed, the inside of the circle",
		"will show a decreasing timer.",
		"",
		"To fire a torpedo: click on a ship to try to get a lock on it and fire at it. If you cannot get a lock, it",
		"is because your sonar is not strong enough to detect your target.",
		"",
		"When an enemy vessels thinks they have a lock on your submarine, they will fire torpedoes at you.",
		"Torpedoes are not capable of avoiding land-masses, so you can use underwater features to avoid a torpedo.",
		"If outmaneuvering an incoming torpedoes is not possible, you can deploy a towable torpedo decoy."
	};
}
