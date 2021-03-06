package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MobTest extends Mob {

	public MobTest(Vector2 position, Layer layer) {
		super(position, layer);
		maxframe = 4;
		alpha = 0.5f;
	}

	@Override
	public TextureRegion getTexture() {
		Sprite sheet = new Sprite(Images.getTexture("player"));
		TextureRegion[][] regs = sheet.split(16, 16);
		TextureRegion reg = null;
		reg = regs[facingLeft?0:1][frame];
		return reg;
	}
	
	@Override
	public void tint() {
		super.tint();
		tmpimg.getColor().mul(0f, 0f, 0f, 1f);
	}
	
}
