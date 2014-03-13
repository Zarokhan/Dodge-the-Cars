package se.zarokhan.dodgethecars.scenes.stuff;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoVerticalParallaxBackground;
import org.andengine.entity.scene.background.VerticalParallaxBackground.VerticalParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import se.zarokhan.dodgethecars.GameManager;

public class WorldMap {

	private LayoutGameActivity activity;
	private Camera camera;
	
	private TextureRegion grassTR;
	private TextureRegion roadTR;
	
	// WORLD
	private int speed;
	
	public WorldMap(LayoutGameActivity activity, Camera camera, int speed) {
		this.activity = activity;
		this.camera = camera;
		this.speed = speed;
	}

	public void loadResources(BuildableBitmapTextureAtlas textureTA) {
		grassTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureTA, this.activity, "grass.png");
		roadTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureTA, this.activity, "road.png");
	}
	
	public void loadMap(Scene scene) {
		Scene fixedScene = new Scene();
		fixedScene.setRotation(180);
		fixedScene.setPosition(0, -camera.getHeight());
		AutoVerticalParallaxBackground autoBG = new AutoVerticalParallaxBackground(0, 0, 0, speed);
		for(int i = 0; i < camera.getWidth()/GameManager.lengthOfTile; i++){
			switch(i){
			case 0:
				Sprite grass1 = new Sprite(GameManager.lengthOfTile * i, 0, grassTR, this.activity.getVertexBufferObjectManager());
				grass1.setRotation(270);
				autoBG.attachVerticalParallaxEntity(new VerticalParallaxEntity(speed, grass1, 1));
				break;
			case 7:
				Sprite grass2 = new Sprite(GameManager.lengthOfTile * i, 0, grassTR, this.activity.getVertexBufferObjectManager());
				grass2.setRotation(90);
				autoBG.attachVerticalParallaxEntity(new VerticalParallaxEntity(speed, grass2, 1));
				break;
			default:
				Sprite road = new Sprite(GameManager.lengthOfTile * i, 0, roadTR, this.activity.getVertexBufferObjectManager());
				road.setRotation(90);
				autoBG.attachVerticalParallaxEntity(new VerticalParallaxEntity(speed, road, 1));
			}
		}
		fixedScene.setBackground(autoBG);
		scene.attachChild(fixedScene);
	}
}
