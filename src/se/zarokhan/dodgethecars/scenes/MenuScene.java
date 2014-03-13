package se.zarokhan.dodgethecars.scenes;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
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
	private TextureRegion dodgecarsTR, playTR, highscoreTR, creditsTR, carTR, soundsOnTR, soundsOffTR, musicOnTR, musicOffTR;
	
	private Sprite soundOnBtn, musicOnBtn, musicOffBtn, soundOffBtn;
	
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
		
		soundsOnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mapTA, this.activity, "sound on.png");
		soundsOffTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mapTA, this.activity, "sound off.png");
		musicOnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mapTA, this.activity, "music on.png");
		musicOffTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mapTA, this.activity, "music off.png");
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32*3f);
		font.load();
		
		sounds.loadResources();
		
		try {
			menuTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTA.load();
			mapTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			mapTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
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
		
		// MENU ITEMS
		final IMenuItem buttonPlay = new ScaleMenuItemDecorator(new SpriteMenuItem(PLAY_BTN_ID, playTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		final IMenuItem buttonHighScore = new ScaleMenuItemDecorator(new SpriteMenuItem(HIGHSCORE_BTN_ID, highscoreTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		final IMenuItem buttonCredits = new ScaleMenuItemDecorator(new SpriteMenuItem(CREDITS_BTN_ID, creditsTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		buttonPlay.setPosition((screenWidth - playTR.getWidth())/2, screenHeight/7 * 3);
		buttonHighScore.setPosition((screenWidth - highscoreTR.getWidth())/2, screenHeight/7 * 4);
		buttonCredits.setPosition((screenWidth - creditsTR.getWidth())/2, screenHeight/7 * 5);
		
		// Sound and Music on/off buttons
		soundOnBtn = new Sprite(camera.getWidth() - soundsOnTR.getWidth() * 2, 0, soundsOnTR, this.activity.getVertexBufferObjectManager());
		soundOffBtn = new Sprite(camera.getWidth() - soundsOnTR.getWidth() * 2, 0, soundsOffTR, this.activity.getVertexBufferObjectManager());
		musicOnBtn = new Sprite(camera.getWidth() - musicOnTR.getWidth(), 0, musicOnTR, this.activity.getVertexBufferObjectManager());
		musicOffBtn = new Sprite(camera.getWidth() - musicOnTR.getWidth(), 0, musicOffTR, this.activity.getVertexBufferObjectManager());
		
		menuScene.attachChild(banner);
		menuScene.addMenuItem(buttonPlay);
		menuScene.addMenuItem(buttonHighScore);
		menuScene.addMenuItem(buttonCredits);
		
		if(sounds.playMusic){
			menuScene.attachChild(musicOffBtn);
			menuScene.attachChild(musicOnBtn);
			musicOffBtn.setVisible(false);
			musicOnBtn.setVisible(true);
		}else{
			menuScene.attachChild(musicOnBtn);
			menuScene.attachChild(musicOffBtn);
			musicOnBtn.setVisible(false);
			musicOffBtn.setVisible(true);
		}
		
		if(sounds.playSounds){
			menuScene.attachChild(soundOffBtn);
			menuScene.attachChild(soundOnBtn);
			soundOffBtn.setVisible(false);
			soundOnBtn.setVisible(true);
		}else{
			menuScene.attachChild(soundOnBtn);
			menuScene.attachChild(soundOffBtn);
			soundOnBtn.setVisible(false);
			soundOffBtn.setVisible(true);
		}
		
		final Rectangle soundRec = new Rectangle(camera.getWidth() - soundsOnTR.getWidth() * 2, 0, GameManager.lengthOfTile, GameManager.lengthOfTile, this.activity.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown()) {
					if(sounds.playSounds){
						sounds.setSoundsEnable(false);
						soundOnBtn.setVisible(false);
						soundOffBtn.setVisible(true);
					}else{
						sounds.setSoundsEnable(true);
						soundOffBtn.setVisible(false);
						soundOnBtn.setVisible(true);
					}
				}
				return true;
			};
		};
		final Rectangle musicRec = new Rectangle(camera.getWidth() - musicOnTR.getWidth(), 0, GameManager.lengthOfTile, GameManager.lengthOfTile, this.activity.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown()) {
					if(sounds.playMusic){
						sounds.setMusicEnable(false);
						sounds.pauseMusic();
						musicOnBtn.setVisible(false);
						musicOffBtn.setVisible(true);
					}else{
						sounds.setMusicEnable(true);
						sounds.playMusic();
						musicOffBtn.setVisible(false);
						musicOnBtn.setVisible(true);
					}
				}
				return true;
			};
		};
		soundRec.setAlpha(255);
		musicRec.setAlpha(255);
		menuScene.registerTouchArea(soundRec);
		menuScene.registerTouchArea(musicRec);
		menuScene.attachChild(soundRec);
		menuScene.attachChild(musicRec);
		
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
					sceneManager.createHighScoreScene();
					sceneManager.setCurrentSence(AllScenes.HIGHSCORE);
					break;
				case CREDITS_BTN_ID:
					sounds.playBlop();
					sceneManager.createCreditScene();
					sceneManager.setCurrentSence(AllScenes.CREDITS);
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
