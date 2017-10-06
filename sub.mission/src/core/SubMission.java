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

import jig.ResourceManager;
import jig.Vector;
import jig.Entity;


public class SubMission extends StateBasedGame {
	
	public static final int LOADINGSTATE 	= 0;
	public static final int MENUSTATE 		= 1;
	public static final int PLAYINGSTATE 	= 2;
	public static final int GAMEOVERSTATE 	= 3;
	
	
	public static HashMap<String, String> IMG = new HashMap<String, String>();
	public static HashMap<String, String> SND = new HashMap<String, String>();

	public static int ScreenWidth;
	public static int ScreenHeight;
	
	public int missionFailed;
		
	static HashMap<String, Integer> layers = new HashMap<String, Integer>();
	static public List<List<Entity>> entities = new ArrayList<List<Entity>>();
	
	static public TrueTypeFont title;
	static public TrueTypeFont subtitle;
	static public TrueTypeFont text;
	
	public Sound bg;
	
	public Image map;
	public Image depth;
	static public int[][] landMasses = {
			/* x, y, r */
			{37, 287, 27},
			{0, 75, 118},
			{80, 660, 250},
			{945, 76, 69}
	};
	
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
		IMG.put("mission_target", "resource/img/ui/mission_target.png");
		
		SND.put("bg", "resource/sound/115609__scratchikken__underwaterloop1.wav");
	}
	
	public static Image getImage(String key) {
		return ResourceManager.getImage(SubMission.IMG.get(key));
	}
	
	public static Sound getSound(String key) {
		return ResourceManager.getSound(SubMission.SND.get(key));
	}
	
	public void setTitleFont(TrueTypeFont t) {
		title = t;
	}
	
	public void setSubtitleFont(TrueTypeFont t) {
		subtitle = t;
	}
	
	public void setFont(TrueTypeFont t) {
		text = t;
	}
	
	public void setLayers(List<String> types) {
		for (String layer : types) {
			addLayer(layer);
		}
	}
	
	public void addLayer(String layer) {
		if (!layers.containsKey(layer))
			layers.put(layer, entities.size() + 1);
			entities.add( new ArrayList<Entity>() );
	}
	
	public void removeLayer(String layer) {
		if (layers.containsKey(layer)) {
			entities.remove( Math.abs(layers.get(layer)) - 1 );
			layers.remove(layer);
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
	
	public ArrayList<Integer> getVisibleLayers() {
		ArrayList<Integer> result = new ArrayList<Integer>();
	
		for (Integer i : layers.values())
			if (i > 0) result.add(i);
	
		return result;
	}
	
	static public int getLayerIndex(String layer) {
		return Math.abs(layers.get(layer)) - 1;
	}
	
	static public List<Entity> getLayer(String layer) {
		return entities.get(getLayerIndex(layer));
	}
	
	public boolean addEntity(String layer, Entity e) {
		if (layers.containsKey(layer)) {
			//System.out.println(entities.get(getLayerIndex(layer)));
			entities.get(getLayerIndex(layer)).add(e);
			return true;
		}
		return false;
	}
	
	public boolean removeEntity(String layer, Entity e) {
		if (layers.containsKey(layer)) {
			entities.get(getLayerIndex(layer)).remove(e);
			return true;
		}
		return false;
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
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			SubMission g = new SubMission("sub.mission");
			app = new AppGameContainer(g);
			
			g.ScreenWidth = app.getScreenWidth() - 40;
			g.ScreenHeight = app.getScreenHeight() - 50;
			
			System.out.println(g.ScreenWidth + " " + g.ScreenHeight);
			
			app.setDisplayMode(g.ScreenWidth, g.ScreenHeight, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	
}
