package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class TimeDayEngine {

	public Level level;
	//delta in seconds!
	public static float fullday = 60f * 5f; //36000 ticks is 20 minutes at 30 FPS
	public static float day = 0;
	public float delta = fullday/2f - fullday/5f;
	
	public static Array<Color> colors = new Array<Color>();
	public static Array<Color> colorsbg = new Array<Color>();
	static {
		colors.add(new Color(0f, 0.1f, 0.25f, 1f));
		colors.add(new Color(0.7f, 0.6f, 0.4f, 1f));
		colors.add(new Color(1f, 1f, 1f, 1f));
		colors.add(new Color(0.7f, 0.6f, 0.3f, 1f));
		
		colorsbg.add(new Color(0f, 0.125f, 0.3f, 1f));
		colorsbg.add(new Color(0.7f, 0.6f, 0.3f, 1f));
		colorsbg.add(new Color(0.2f, 0.5f, 0.7f, 1f));
		colorsbg.add(new Color(0.7f, 0.6f, 0.3f, 1f));
	}
	
	public TimeDayEngine(Level level) {
		this.level = level;
	}
	
	protected Color tmp = new Color(1f, 1f, 1f, 1f);
	protected Color tmpbg = new Color(1f, 1f, 1f, 1f);
	protected Color tmpbg2 = new Color(1f, 1f, 1f, 1f);
	protected Color tmpc = new Color(1f, 1f, 1f, 1f);
	
	public void tick() {
		if (Shadow.isAndroid) {
			//return; //FIXME PERFORMANCE
		}
		int ci = (int)((delta/fullday)*colors.size)%colors.size;
		float f = ((delta%(fullday/colors.size))/(fullday/colors.size))%1f;
		int cii = ci + 1;
		if (cii >= colors.size) {
			cii = 0;
		}
		int cci = (int)(((delta+fullday/16f)/fullday)*colors.size)%colors.size;
		float ff = (((delta+fullday/16f)%(fullday/colors.size))/(fullday/colors.size))%1f;
		int ccii = cci + 1;
		if (ccii >= colors.size) {
			ccii = 0;
		}
		
		tmp.set(tmpc.set(colors.get(ci)).mul(1f-f));
		tmp.add(tmpc.set(colors.get(cii)).mul(f));
		
		tmpbg.set(tmpc.set(colorsbg.get(ci)).mul(1f-f));
		tmpbg.add(tmpc.set(colorsbg.get(cii)).mul(f));
		tmpbg2.set(tmpc.set(colorsbg.get(cci)).mul(1f-ff));
		tmpbg2.add(tmpc.set(colorsbg.get(ccii)).mul(ff));
		
		level.globalLight.set(tmp);
		Shadow.cam.bg.c1.set(tmpbg);
		Shadow.cam.bg.c2.set(tmpbg2).sub(0.2f, 0.2f, 0.2f, 0f);
		if (ci == 0 || cii == 0) {
				if (cii == 0) {
					Shadow.cam.bg.starsAlpha = (delta-3f*(fullday/4f))/(fullday/4f);
				} else {
					Shadow.cam.bg.starsAlpha = 1f-delta/(fullday/4f);
				}
		} else {
			Shadow.cam.bg.starsAlpha = 0f;
		}
		Shadow.cam.bg.starsScrollX = 0f;
		Shadow.cam.bg.starsScrollY = 0f;
		float adddelta = Gdx.graphics.getDeltaTime();
		Shadow.cam.bg.sunPos += adddelta;
		Shadow.cam.bg.sunRound = fullday;
		delta += adddelta;
		if (delta > fullday) {
			delta -= fullday;
			day++;
		}
	}

}
