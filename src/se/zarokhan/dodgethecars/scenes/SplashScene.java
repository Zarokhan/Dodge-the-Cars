package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.color.Color;

import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.mSoundManager;

public class SplashScene {
	
	private LayoutGameActivity activity;
	private Camera camera;
	private SceneManager sceneManager;
	
	// SCENE
	private Scene scene;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas splashTA;
	private TextureRegion splashTR;
	
	public SplashScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager, mSoundManager sounds) {
		this.activity = activity;
		this.camera = camera;
		this.sceneManager = sceneManager;
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
		splashTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.REPEATING_NEAREST);
		splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTA, this.activity, "splash.png");
		
		
		try {
			splashTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			splashTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		sceneManager.loadMenuResources();
		sceneManager.loadGameResources();
		sceneManager.loadRetryResoruces();
		//sceneManager.loadCreditsResources();
	}
	
	public Scene createScene() {
		scene = new Scene();
		scene.setBackground(new Background(Color.WHITE));
		
		final Sprite splash = new Sprite(camera.getWidth()/2 - splashTR.getWidth()/2, camera.getHeight()/2 - splashTR.getHeight()/2, splashTR, this.activity.getVertexBufferObjectManager());
		
		scene.attachChild(splash);
		return scene;
	}

	public Scene getScene() {
		return scene;
	}
}
