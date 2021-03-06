package net.fourbytes.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;

/**
 *	Placeholder for the layers in the levels. Processing happens in {@link Level}.
 */
public class Layer {
	
	public static enum BlockMapSystem {
		coordinate, //Default, most performance, most garbage
		row, //performance decreased, less garbage
		column, //performance decreased, less garbage
		none //least performance, "no garbage"
	}
	
	public Level level;
	public Array<Block> blocks = new Array<Block>(true, 4096);
	public Array<Entity> entities = new Array<Entity>(true, 512);
	//protected ObjectMap<Coord, Array<Block>> blockmap = new ObjectMap<Coord, Array<Block>>(1024, 0.95f);
	protected IntMap<Array<Block>> rowmap = new IntMap<Array<Block>>(256);
	protected LongMap<Array<Block>> blockmap = new LongMap<Array<Block>>(1024);
	
	public boolean cache = false;
	public Array<GameObject> addcache = new Array<GameObject>(true, 64);
	public Array<GameObject> remcache = new Array<GameObject>(true, 64);
	
	public Color tint = new Color(1f, 1f, 1f, 1f);
	
	public static BlockMapSystem bms = BlockMapSystem.coordinate;
	protected BlockMapSystem lastbms;
	public static float round = 5f;
	
	public Layer(Level level) {
		this.level = level;
	}
	
	public void add(GameObject go) {
		if (this != level.mainLayer) {
			level.mainLayer.add(go);
		}
		if (cache) {
			addcache.add(go);
			return;
		}
		if (go instanceof Block) {
			blocks.add((Block) go);
			Array<Block> al = get0(Coord.get((int)(go.pos.x/round), (int)(go.pos.y/round)));
			if (al == null) {
				al = put0(Coord.get((int)(go.pos.x/round), (int)(go.pos.y/round)));
			}
			al.add((Block) go);
		} else if (go instanceof Entity) {
			entities.add((Entity) go);
		}
	}
	
	public void remove(GameObject go) {
		if (this != level.mainLayer) {
			level.mainLayer.remove(go);
		}
		if (cache) {
			remcache.add(go);
			return;
		}
		if (go instanceof Block) {
			blocks.removeValue((Block) go, true);
			Array al = get0(Coord.get((int)(go.pos.x/round), (int)(go.pos.y/round)));
			if (al != null) {
				al.removeValue((Block) go, true);
				if (al.size == 0) {
					remove0(Coord.get((int)(go.pos.x/round), (int)(go.pos.y/round)));
				}
			}
		} else if (go instanceof Entity) {
			entities.removeValue((Entity) go, true);
		}
	}

	public void move(Block b, long oldc, long newc) {
		if (this != level.mainLayer) {
			level.mainLayer.move(b, oldc, newc);
		}
		int oldx = Coord.getX(oldc);
		int oldy = Coord.getY(oldc);
		int newx = Coord.getX(newc);
		int newy = Coord.getY(newc);
		Array oal = get0(Coord.get((int)(oldx/round), (int)(oldy/round)));
		Array nal = get0(Coord.get((int)(newx/round), (int)(newy/round)));
		if (oal == nal) {
			return;
		}
		if (nal == null) {
			nal = put0(Coord.get((int)(newx/round), (int)(newy/round)));
		}
		if (oal != null) {
			oal.removeValue(b, true);
			if (oal.size == 0) {
				remove0(Coord.get((int)(oldx/round), (int)(oldy/round)));
			}
		}
		nal.add(b);
	}
	
	public Array<Block> get(long c) {
		int cx = Coord.getX(c);
		int cy = Coord.getY(c);
		Array<Block> vv = null;
		vv = get0(Coord.get((int)(cx/round), (int)(cy/round)));
		/*
		if (v == null) {
			v = new Array<Block>(true, 32);
			blockmap.put(c, v);
		}
		*/
		Array<Block> v = new Array<Block>(4);
		if (vv != null) {
			for (Block b : vv) {
				if (cx == (int)b.pos.x && cy == (int)b.pos.y) {
					v.add(b);
				}
			}
		}
		if (v != null && v.size == 0) {
			for (GameObject go : addcache) {
				if (go instanceof Block) {
					Block b = (Block)go;
					if (cx == (int)b.pos.x && cy == (int)b.pos.y) {
						v.add(b);
					}
				}
			}
		}
		if (vv != null && vv.size == 0) {
			remove0(Coord.get((int)(cx/round), (int)(cy/round)));
		}
		return v;
	}
	
	protected Array<Block> get0(long c){
		updateSystem0();
		
		Array<Block> al = null;
		switch (bms) {
		case coordinate:
			al = blockmap.get(c);
			break;
		case row:
			al = rowmap.get(Coord.getY(c));
			break;
		case column:
			al = rowmap.get(Coord.getX(c));
			break;
		case none:
			al = blocks;
			break;
		}
		return al;
	}
	
	protected Array<Block> put0(long c){
		updateSystem0();
		
		Array<Block> al = null;
		switch (bms) {
		case coordinate:
			al = new Array<Block>(32);
			blockmap.put(c, al);
			break;
		case row:
			al = new Array<Block>(4);
			rowmap.put(Coord.getY(c), al);
			break;
		case column:
			al = new Array<Block>(4);
			rowmap.put(Coord.getX(c), al);
			break;
		case none:
			break;
		}
		return al;
	}
	
	protected void remove0(long c){
		updateSystem0();
		
		switch (bms) {
		case coordinate:
			blockmap.remove(c);
			break;
		case row:
			rowmap.remove(Coord.getY(c));
			break;
		case column:
			rowmap.remove(Coord.getX(c));
			break;
		case none:
			break;
		}
	}
	
	protected void updateSystem0() {
		if (lastbms != null && lastbms != bms) {
			//TODO change the system properly instead of throwing error
			throw new Error("Change of the blockmap system while in-level not supported!");
		}
	}
	
}
