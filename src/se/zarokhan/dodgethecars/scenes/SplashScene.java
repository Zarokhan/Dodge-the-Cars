package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.color.Color;

public class SplashScene {
	
	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	// SCENE
	private Scene scene;
	
	// TEXTURE
	private BitmapTextureAtlas splashTA;
	private TextureRegion zarokhanTR;
	private TextureRegion gamesTR;
	
	public SplashScene(LayoutGameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
		splashTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 1024, 512);
		zarokhanTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTA, this.activity, "zarokhan.png", 0, 0);
		gamesTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTA, this.activity, "games.png", 0, 200);
		splashTA.load();
	}
	
	public Scene createScene() {
		scene = new Scene();
		scene.setBackground(new Background(Color.BLACK));
		
		//final HUD hud = new HUD();
		final Scene hud = new Scene();
		// SETUP POS AND ANGLE
		hud.setRotation(270);
		hud.setPosition(0, camera.getHeight());
		
		final Sprite zarokhan = new Sprite((camera.getHeight() - zarokhanTR.getWidth())/2, (camera.getWidth() - zarokhanTR.getHeight())/2 - 150, zarokhanTR, this.activity.getVertexBufferObjectManager());
		final Sprite games = new Sprite((camera.getHeight() - gamesTR.getWidth())/2, (camera.getWidth() - gamesTR.getHeight())/2 + 167 - 150, gamesTR, this.activity.getVertexBufferObjectManager());
		
		hud.attachChild(zarokhan);
		hud.attachChild(games);
		
		//camera.setHUD(hud);
		scene.attachChild(hud);
		return scene;
	}
}
