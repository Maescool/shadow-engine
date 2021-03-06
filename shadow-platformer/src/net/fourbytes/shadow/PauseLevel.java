package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import net.fourbytes.shadow.Input.Key;

public class PauseLevel extends TitleLevel {

	public PauseLevel() {
		this(null);
	}

	public PauseLevel(MenuLevel parent) {
		super(parent);
		
		Array<MenuItem> items = new Array<MenuItem>();
		
		items.add(new MenuItem(this, "Continue", new Runnable(){public void run(){
			Shadow.level = bglevel;
			}}));
		items.add(new MenuItem(this, "Main Menu", new Runnable(){public void run(){
			Shadow.level = new TitleLevel();
			}}));
		
		items.addAll(this.items);
		items.removeIndex(2);
		this.items = items;
		
		hasvoid = false;
		ready = true;
	}
	
}
