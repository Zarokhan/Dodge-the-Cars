package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
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
import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.mSoundManager;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;
import android.graphics.Typeface;

public class HighScoreScene {
	private final static int HOME_BTN_ID = 0;
	
	private LayoutGameActivity activity;
	private Camera camera;
	
	private SceneManager sceneManager;
	private WorldMap map;
	private MenuScene scene;
	private mSoundManager sounds;
	
	private BuildableBitmapTextureAtlas retryTA;
	private TextureRegion carTR, highscoreTR, homeTR;
	
	private Font font;
	
	float screenWidth, screenHeight;
	
	public HighScoreScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager, mSoundManager sounds){
		this.activity = activity;
		this.camera = camera;
		this.sceneManager = sceneManager;
		this.sounds = sounds;
		
		screenWidth = camera.getWidth();
		screenHeight = camera.getHeight();
		
		map = new WorldMap(this.activity, this.camera, 0);
	}
	
	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		retryTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		carTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "player.png");
		highscoreTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "menu/highscore.png");
		homeTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "home.png");
		
		map.loadResources(retryTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), GameManager.lengthOfTile);
		font.load();
		sounds.loadResources(null);
		try {
			retryTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			retryTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}
	
	public MenuScene createScene(){
		
		scene = new org.andengine.entity.scene.menu.MenuScene(camera);

		map.loadMap(scene);
		
		final Sprite highscores = new Sprite((screenWidth - highscoreTR.getWidth())/2, screenHeight/8, highscoreTR, this.activity.getVertexBufferObjectManager());
		scene.attachChild(highscores);
		
		highscoreList();
		
		navigation();
		
		return scene;
	}

	private void navigation() {
		// MENU ITEMS
		final IMenuItem buttonHome = new ScaleMenuItemDecorator(new SpriteMenuItem(HOME_BTN_ID, homeTR, this.activity.getVertexBufferObjectManager()), 1, 1);
		buttonHome.setPosition(screenWidth/2 - homeTR.getWidth()/2, screenHeight - homeTR.getHeight()*2);
		scene.addMenuItem(buttonHome);
		
		scene.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
				
				switch(pMenuItem.getID()){
				case HOME_BTN_ID:
					sounds.playBlop();
					sceneManager.createMenuScene();
					sceneManager.setCurrentSence(AllScenes.MENU);
					break;
				}
				
				return false;
			}
		});
	}

	private void highscoreList() {
		
	}

	public Scene getScene() {
		return scene;
	}
}