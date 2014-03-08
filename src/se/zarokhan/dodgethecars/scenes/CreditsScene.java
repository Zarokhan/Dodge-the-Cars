package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.ui.activity.LayoutGameActivity;

import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.mSoundManager;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class CreditsScene {
	
	private LayoutGameActivity activity;
	private Camera camera;
	private SceneManager sceneManager;
	private mSoundManager sounds;
	private WorldMap map;
	private Scene scene;
	private BuildableBitmapTextureAtlas TA;
	
	public CreditsScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager, mSoundManager sounds) {
		this.activity = activity;
		this.camera = camera;
		this.sceneManager = sceneManager;
		
		map = new WorldMap(activity, camera, 0);
	}
	
	public void loadResources(){
		TA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		map.loadResources(TA);
		
		try {
			TA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 0));
			TA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}
	
	public Scene createScene(){
		scene = new Scene();
		// SCENE SETUP
		scene.setRotation(270);
		scene.setPosition(0, camera.getHeight());
		
		map.loadMap(scene);
		
		return scene;
	}

	public Scene getScene() {
		return scene;
	}
}
