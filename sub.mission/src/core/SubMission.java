package core;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import entities.Airplane;
import entities.Submarine;
import jig.ResourceManager;
import jig.Vector;
import util.VectorUtil;
import jig.Entity;


public class SubMission extends StateBasedGame {
	
	static public List<List<Entity>> entities = new ArrayList<List<Entity>>();
	public static final int GAMEOVERSTATE 	= 3;
	public static HashMap<String, String> IMG = new HashMap<String, String>();
	static public int[][] landMasses = {
			/* x, y, r */
			{37, 287, 27},
			{0, 75, 118},
			{80, 660, 250},
			{945, 76, 69}
	};
	static public int[][][] shippingLanes = {
			{
				{100, -50, 850, -100},
				{400, 900, 1200, 950}
			}
	};
	static public Vector[] patrolZones = {
			new Vector(580, 270),
			new Vector(1080, 500),
			new Vector(640, 725)
	};
	
	static HashMap<String, Integer> layers = new HashMap<String, Integer>();
	public static final int LOADINGSTATE 	= 0;
	public static final int MENUSTATE 		= 1;
	public static final int PLAYINGSTATE 	= 2;
	
	public static int ScreenHeight;
	public static int ScreenWidth;
	
	public static HashMap<String, String> SND = new HashMap<String, String>();
	static public TrueTypeFont subtitle;
	
	static public TrueTypeFont text;
	static public TrueTypeFont title;
	static HashMap<Entity, Integer> toRemove = new HashMap<Entity, Integer>();
	
	public static Submarine player;
	static public Airplane airSupport;
	static public CommercialManager trafficManager;
	static public PatrolManager patrolManager;
	static public MissionManager missionManager;
	
	static public Image map;
	
	static public int missionFailed;
	
	static public boolean addEntity(String layer, Entity e) {
		if (e == null) return false;
		if (layers.containsKey(layer)) {
			//System.out.println(entities.get(getLayerIndex(layer)));
			entities.get(getLayerIndex(layer)).add(e);
			return true;
		}
		return false;
	}
	
	public static Image getImage(String key) {
		return ResourceManager.getImage(SubMission.IMG.get(key));
	}
	static public List<Entity> getLayer(String layer) {
		return entities.get(getLayerIndex(layer));
	}
	static public int getLayerIndex(String layer) {
		return Math.abs(layers.get(layer)) - 1;
	}
	
	public static Sound getSound(String key) {
		return ResourceManager.getSound(SubMission.SND.get(key));
	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			SubMission g = new SubMission("sub.mission");
			app = new AppGameContainer(g);
			
			SubMission.ScreenWidth = app.getScreenWidth() - 40;
			SubMission.ScreenHeight = app.getScreenHeight() - 50;
			
			System.out.println(SubMission.ScreenWidth + " " + SubMission.ScreenHeight);
			
			app.setDisplayMode(SubMission.ScreenWidth, SubMission.ScreenHeight, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}
	
	static public boolean removeEntity(String layer, Entity e) {
		//System.out.println("Removing: " + e);
		if (layers.containsKey(layer)) {
			toRemove.put(e, getLayerIndex(layer));
			return true;
		}
		return false;
	}
	
	public Sound bg;
	
	public Image depth;
	
	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public SubMission(String title) {
		super(title);
				
		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
		//camera = new Camera(new Vector(ScreenWidth / 2, ScreenHeight / 2), ScreenWidth, ScreenHeight);
		
		
		missionFailed = 0;
		
		IMG.put("land", "resource/img/map1/land.png");
		IMG.put("map", "resource/img/map1/map.png");
		IMG.put("d1", "resource/img/map1/depth_100.png");
		IMG.put("d2", "resource/img/map1/depth_200.png");
		IMG.put("d3", "resource/img/map1/depth_300.png");
		IMG.put("d4", "resource/img/map1/depth_400.png");
		IMG.put("d5", "resource/img/map1/depth_500.png");
		IMG.put("d6", "resource/img/map1/depth_600.png");
		IMG.put("d7", "resource/img/map1/depth_700.png");
		
		IMG.put("sub0", "resource/img/vessel/sub0.png");
		IMG.put("sub1", "resource/img/vessel/sub1.png");
		IMG.put("sub2", "resource/img/vessel/sub2.png");
		
		IMG.put("patrol", "resource/img/vessel/patrol.png");
		IMG.put("destroyer", "resource/img/vessel/destroyer.png");
		IMG.put("airplane", "resource/img/vessel/airplane.png");
		
		IMG.put("ship1", "resource/img/vessel/feeder.png");
		IMG.put("ship2", "resource/img/vessel/panamax.png");
		IMG.put("ship3", "resource/img/vessel/post-panamax.png");
		IMG.put("ship4", "resource/img/vessel/ulcv.png");
		
		IMG.put("speed", "resource/img/ui/speed.png");
		IMG.put("depth", "resource/img/ui/depth.png");
		IMG.put("speed_target", "resource/img/ui/speed_target.png");
		IMG.put("depth_target", "resource/img/ui/depth_target.png");
		IMG.put("bearing_target", "resource/img/ui/bearing_target.png");
		IMG.put("marker", "resource/img/ui/marker.png");
		IMG.put("torpedo_marker", "resource/img/ui/torpedo_marker.png");
		IMG.put("sub_torpedo_marker", "resource/img/ui/sub_torpedo_marker.png");
		IMG.put("mission_target", "resource/img/ui/mission_target.png");
		
		IMG.put("sub_hull", "resource/img/ui/sub_hull.png");
		IMG.put("sub_hull0", "resource/img/ui/sub_hull0.png");
		IMG.put("torpedo_full", "resource/img/ui/torpedo_full.png");
		IMG.put("torpedo_empty", "resource/img/ui/torpedo_empty.png");
		IMG.put("nm_full", "resource/img/ui/nm_full.png");
		IMG.put("nm_empty", "resource/img/ui/nm_empty.png");
		IMG.put("decoy_btn", "resource/img/ui/decoy_btn.png");
		IMG.put("retract_btn", "resource/img/ui/retract_btn.png");
		IMG.put("sonar_btn", "resource/img/ui/sonar_btn.png");
		IMG.put("target_lock", "resource/img/ui/target_lock.png");
		
		IMG.put("towed_sonar", "resource/img/items/towed_sonar.png");
		IMG.put("towed_decoy", "resource/img/items/towed_decoy.png");
		IMG.put("decoy_waves", "resource/img/items/decoy_waves.png");
		IMG.put("sonar_waves", "resource/img/items/sonar_waves.png");
		IMG.put("sub_torpedo", "resource/img/items/sub_torpedo.png");
		IMG.put("enemy_torpedo", "resource/img/items/enemy_torpedo.png");
		IMG.put("sonobuoy_above", "resource/img/items/sonobuoy_above.png");
		IMG.put("sonobuoy_even", "resource/img/items/sonobuoy_even.png");
		IMG.put("sonobuoy_below", "resource/img/items/sonobuoy_below.png");
		
		SND.put("bg", "resource/sound/115609__scratchikken__underwaterloop1.wav");
		SND.put("fire_torpedo", "resource/sound/35530__jobro__torpedo-launch-underwater.wav");
		SND.put("torpedo_explosion", "resource/sound/159402__noirenex__overheadexplosion.wav");
		SND.put("explosion_a", "resource/sound/203331__veiler__explosion-documentary-veiler.wav");
		SND.put("explosion_b", "resource/sound/94185__nbs-dark__explosion.wav");
	}
	
	static public void addLayer(String layer) {
		if (!layers.containsKey(layer)) {
			layers.put(layer, entities.size() + 1);
			entities.add( new ArrayList<Entity>() );
		}
	}
	
	public ArrayList<Integer> getVisibleLayers() {
		ArrayList<Integer> result = new ArrayList<Integer>();
	
		for (Integer i : layers.values())
			if (i > 0) result.add(i);
	
		return result;
	}
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		
		container.setShowFPS(false);
		
		// game states
		addState(new LoadingState());
		addState(new MenuState());
		addState(new GameOverState());
		addState(new PlayingState());
		
		try {	 
			Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("resource/Verdana.ttf"));
			
			title = new TrueTypeFont(font.deriveFont(44f), true);
			subtitle = new TrueTypeFont(font.deriveFont(24f), true);
			text = new TrueTypeFont(font.deriveFont(16f), true);
	 
		} catch (Exception e) {
			e.printStackTrace();
			
			title = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 48), true);
			subtitle = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 24), true);
			text = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 16), true);
		}
	}
	
	static public void removeLayer(String layer) {
		if (layers.containsKey(layer)) {
			entities.remove( Math.abs(layers.get(layer)) - 1 );
			int removed = layers.remove(layer);
			int k;
			for (String key : layers.keySet()) {
				k = layers.get(key);
				if (k > removed) {
					layers.put(key, k - 1);
				}
			}
		}
	}
	
	public void setFont(TrueTypeFont t) {
		text = t;
	}
	
	public void setLayers(List<String> types) {
		for (String layer : types) {
			addLayer(layer);
		}
	}
	
	public void setLayerVisibility(String layer, boolean b) {
		if (layers.containsKey(layer)) {
			if (b)
				layers.put( layer, Math.abs(layers.get(layer)) );
			else	
				layers.put(layer, 0 - Math.abs(layers.get(layer)) );
		}
	}
	
	public void setSubtitleFont(TrueTypeFont t) {
		subtitle = t;
	}
	

	public void setTitleFont(TrueTypeFont t) {
		title = t;
	}
	
	public void update() {
		for (Entity e : toRemove.keySet()) {
			entities.get(toRemove.get(e)).remove(e);
		}
		toRemove.clear();
	}

	
}
