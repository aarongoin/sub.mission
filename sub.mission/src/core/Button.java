package core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.MouseOverArea;

public class Button {

	MouseOverArea bounds;
		
	TrueTypeFont font;
	int height;
	int margin;
	
	int[] pos = {0, 0};
	
	String text;
	
	int width;
	
	public Button(GameContainer c, TrueTypeFont f, String t, int x, int y, int m) {
		text = t;
		
		font = f;
		margin = m;
		
		width = f.getWidth(text) + margin*2;
		height = f.getHeight() + margin*2;

		pos[0] = x - (width / 2);
		pos[1] = y - (height / 2);
		
		bounds = new MouseOverArea(c, null, pos[0] - margin, pos[1] - margin, width, height);
	}
	
	public boolean clicked(Input input, boolean mouse) {
		return (bounds.isMouseOver() && mouse);
	}
	
	public void render(Graphics g) {
		g.setFont(font);
		g.drawString(text, pos[0], pos[1]);
		g.drawRect(pos[0] - margin, pos[1] - margin, width, height);
	}

}
