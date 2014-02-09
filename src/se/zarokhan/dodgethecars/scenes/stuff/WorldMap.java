package se.zarokhan.dodgethecars.scenes.stuff;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import se.zarokhan.dodgethecars.GameManager;

public class WorldMap {

	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private TextureRegion grassTR;
	private TextureRegion roadTR;
	
	private AutoParallaxBackground autoBG;
	
	// WORLD
	private int speed;
	
	public WorldMap(LayoutGameActivity activity, Camera camera, Engine engine, int speed) {
		this.activity = activity;
		this.camera = camera;
		this.engine = engine;
		this.speed = speed;
	}

	public void loadResources(BuildableBitmapTextureAtlas textureTA) {
		grassTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureTA, this.activity, "grass.png");
		roadTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureTA, this.activity, "road.png");

	}
	
	public void loadMap(Scene scene) {
		
		autoBG = new AutoParallaxBackground(0, 0, 0, speed);
		for(int i = 0; i < camera.getWidth()/GameManager.lengthOfTile; i++){
			if(i == 1){
				autoBG.attachParallaxEntity(new ParallaxEntity(speed, new Sprite(0, 0, grassTR, this.activity.getVertexBufferObjectManager())));
			}
			if(i > 0 && i < 7){
				autoBG.attachParallaxEntity(new ParallaxEntity(speed, new Sprite(0, GameManager.lengthOfTile*i, roadTR, this.activity.getVertexBufferObjectManager())));
			}
			if(i == 7){
				Sprite roadRotated = new Sprite(0, GameManager.lengthOfTile*i, grassTR, this.activity.getVertexBufferObjectManager());
				roadRotated.setRotation(180);
				autoBG.attachParallaxEntity(new ParallaxEntity(speed, roadRotated));
			}
		}
		scene.setBackground(autoBG);
	}

	public void slowDown() {
		autoBG.setParallaxChangePerSecond(0);
	}
}
