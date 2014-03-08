package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
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
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;
import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.mSoundManager;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class RetryScene {
	
	private final static int HOME_BTN_ID = 0;
	private final static int RETRY_BTN_ID = 1;
	
	private LayoutGameActivity activity;
	private Camera camera;
	
	private SceneManager sceneManager;
	private WorldMap map;
	private MenuScene scene;
	private mSoundManager sounds;
	
	private BuildableBitmapTextureAtlas retryTA;
	private TextureRegion carTR, highscoreTR, retryTR, homeTR;
	
	private Font font;
	
	float screenWidth, screenHeight;
	
	public RetryScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager, mSoundManager sounds){
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
		retryTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "retry.png");
		homeTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "home.png");
		
		map.loadResources(retryTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), GameManager.lengthOfTile);
		font.load();
		
		try {
			retryTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			retryTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		sounds.loadResources();
	}
	
	public MenuScene createScene(){
		
		scene = new org.andengine.entity.scene.menu.MenuScene(camera);

		map.loadMap(scene);
		initCrashedCar();
		
		final Sprite highscores = new Sprite((screenWidth - highscoreTR.getWidth())/2, screenHeight/8, highscoreTR, this.activity.getVertexBufferObjectManager());
		scene.attachChild(highscores);
		
		highscoreList();
		
		navigation();
		
		return scene;
	}

	private void navigation() {
		// MENU ITEMS
		final IMenuItem buttonHome = new ScaleMenuItemDecorator(new SpriteMenuItem(HOME_BTN_ID, homeTR, this.activity.getVertexBufferObjectManager()), 1, 1);
		final IMenuItem buttonRetry = new ScaleMenuItemDecorator(new SpriteMenuItem(RETRY_BTN_ID, retryTR, this.activity.getVertexBufferObjectManager()), 1, 1);
		buttonHome.setPosition(screenWidth/2 - homeTR.getWidth()/2 - retryTR.getWidth()/2 - 64, screenHeight - homeTR.getHeight()*2);
		buttonRetry.setPosition(screenWidth/2 - retryTR.getWidth()/2 + homeTR.getWidth()/2 + 64, screenHeight - retryTR.getHeight()*2);
		
		scene.addMenuItem(buttonHome);
		scene.addMenuItem(buttonRetry);
		
		scene.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
				
				switch(pMenuItem.getID()){
				case HOME_BTN_ID:
					sounds.playBlop();
					sceneManager.createMenuScene();
					sceneManager.setCurrentSence(AllScenes.MENU);
					break;
				case RETRY_BTN_ID:
					sounds.playCarStart();
					sceneManager.createGameScene();
					sceneManager.setCurrentSence(AllScenes.GAME);
					break;
				}
				
				return false;
			}
		});
	}

	private void highscoreList() {
		
	}

	private void initCrashedCar() {
		final Text score = new Text(GameManager.lengthOfTile, GameManager.lengthOfTile * 3, font, "" + GameManager.getInstance().getScore(), new TextOptions(HorizontalAlign.LEFT), this.activity.getVertexBufferObjectManager());
		scene.attachChild(score);
		GameManager.getInstance().resetGame();
	}

	public Scene getScene() {
		return scene;
	}
}
