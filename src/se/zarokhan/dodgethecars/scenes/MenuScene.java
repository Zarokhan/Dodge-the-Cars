package se.zarokhan.dodgethecars.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
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

public class MenuScene {
	
	private final static float onSelected = 1f;
	private final static float unSelected = 1f;
	
	private final static int PLAY_BTN_ID = 0;
	private final static int HIGHSCORE_BTN_ID = 1;
	private final static int CREDITS_BTN_ID = 2;
	
	private LayoutGameActivity activity;
	private Camera camera;
	private Random r;
	
	private SceneManager sceneManager;
	private WorldMap map;
	private mSoundManager sounds;
	
	// SCENE
	private org.andengine.entity.scene.menu.MenuScene menuScene;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas menuTA, mapTA;
	private TextureRegion dodgecarsTR, playTR, highscoreTR, creditsTR, carTR;
	
	// CAR
	private int lane;
	
	// TEXT
	private Font font;
	
	// Banner
	private float startScale = 0.8f;
	private float endScale = 0.95f;
	private int scaleDuration = 5;
	private int startRot = -20;
	private int endRot = 10;
	private int rotDuration = 5;
	private boolean scaBan, rotBan;
	
	public MenuScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager, mSoundManager sounds) {
		this.activity = activity;
		this.camera = camera;
		this.sceneManager = sceneManager;
		this.sounds = sounds;
		
		map = new WorldMap(activity, camera, 0);
		r = new Random();
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		
		menuTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		dodgecarsTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "dodgethecars.png");
		playTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "play.png");
		highscoreTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "highscore.png");
		creditsTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "credits.png");
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mapTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		carTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mapTA, this.activity, "player.png");
		map.loadResources(mapTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32*3f);
		font.load();
		
		try {
			menuTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTA.load();
			mapTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			mapTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		sounds.loadResources();
	}

	public org.andengine.entity.scene.menu.MenuScene createScene() {
		float screenWidth = camera.getWidth();
		float screenHeight = camera.getHeight();
		menuScene = new org.andengine.entity.scene.menu.MenuScene(camera);
		
		// BACKGROUND SETUP
		map.loadMap(menuScene);
		//menuScene.attachChild(map.createScene(10));
		
		spawnCar(screenWidth, screenHeight);
		
		// BANNER/LOGO & banner animation
		final Sprite banner = new Sprite((screenWidth - dodgecarsTR.getWidth())/2, (screenHeight/7)/2, dodgecarsTR, this.activity.getVertexBufferObjectManager());
		rotBan = false;
		scaBan = false;
		banner.registerEntityModifier(new ScaleModifier(scaleDuration, startScale, endScale){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				if(scaBan){
					this.reset(scaleDuration, startScale, endScale, startScale, endScale);
					scaBan = !scaBan;
				}else{
					this.reset(scaleDuration, endScale, startScale, endScale, startScale);
					scaBan = !scaBan;
				}
			}
		});
		banner.registerEntityModifier(new RotationModifier(rotDuration, startRot, endRot){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				if(!rotBan){
					this.reset(rotDuration, endRot, startRot);
					rotBan = !rotBan;
				}else{
					this.reset(rotDuration, startRot, endRot);
					rotBan = !rotBan;
				}
			}
		});
		
		// GAME VERSION
		final Text GAME_STAGE = new Text(GameManager.lengthOfTile * 6 - GameManager.lengthOfTile/2, 0, font, GameManager.GAME_VERSION, new TextOptions(HorizontalAlign.LEFT), this.activity.getVertexBufferObjectManager());
		
		// MENU ITEMS
		final IMenuItem buttonPlay = new ScaleMenuItemDecorator(new SpriteMenuItem(PLAY_BTN_ID, playTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		final IMenuItem buttonHighScore = new ScaleMenuItemDecorator(new SpriteMenuItem(HIGHSCORE_BTN_ID, highscoreTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		final IMenuItem buttonCredits = new ScaleMenuItemDecorator(new SpriteMenuItem(CREDITS_BTN_ID, creditsTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		buttonPlay.setPosition((screenWidth - playTR.getWidth())/2, screenHeight/7 * 3);
		buttonHighScore.setPosition((screenWidth - highscoreTR.getWidth())/2, screenHeight/7 * 4);
		buttonCredits.setPosition((screenWidth - creditsTR.getWidth())/2, screenHeight/7 * 5);
		
		menuScene.attachChild(banner);
		menuScene.attachChild(GAME_STAGE);
		menuScene.addMenuItem(buttonPlay);
		menuScene.addMenuItem(buttonHighScore);
		menuScene.addMenuItem(buttonCredits);
		
		menuScene.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
				
				switch(pMenuItem.getID()){
				case PLAY_BTN_ID:
					sounds.playCarStart();
					sceneManager.createGameScene();
					sceneManager.setCurrentSence(AllScenes.GAME);
					break;
				case HIGHSCORE_BTN_ID:
					sounds.playBlop();
					sceneManager.createRetryScene();
					sceneManager.setCurrentSence(AllScenes.RETRY);
					break;
				case CREDITS_BTN_ID:
					sounds.playBlop();
					//sceneManager.createCreditScene();
					//sceneManager.setCurrentSence(AllScenes.CREDITS);
					break;
				}
				
				return false;
			}
		});
		
		return menuScene;
	}

	private void spawnCar(final float screenWidth, final float screenHeight) {
		lane = r.nextInt(6)+1;
		final Sprite car = new Sprite(-300, 0, carTR, this.activity.getVertexBufferObjectManager());
		car.setRotation(180);
		MoveModifier moveModifier = new MoveModifier(4, lane * GameManager.lengthOfTile, lane * GameManager.lengthOfTile, -GameManager.lengthOfTile * 2, screenHeight + GameManager.lengthOfTile*2){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				lane = r.nextInt(6)+1;
				this.reset(4, lane * GameManager.lengthOfTile, lane * GameManager.lengthOfTile, -GameManager.lengthOfTile * 2, screenHeight + GameManager.lengthOfTile*2);
			}
		};
		car.registerEntityModifier(moveModifier);
		menuScene.attachChild(car);
	}

	public Scene getScene() {
		return menuScene;
	}
}
