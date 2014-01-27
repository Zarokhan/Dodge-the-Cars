package se.zarokhan.dodgethecars.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
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
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class MenuScene {
	
	private final static float onSelected = 1f;
	private final static float unSelected = 1f;
	
	private final static int PLAY_BTN_ID = 0;
	private final static int HOWTO_BTN_ID = 1;
	private final static int CREDITS_BTN_ID = 2;
	
	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	private Random r;
	
	private SceneManager sceneManager;
	private WorldMap map;
	
	// SCENE
	private org.andengine.entity.scene.menu.MenuScene menuScene;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas menuTA;
	private TextureRegion dodgecarsTR, playTR, howtoTR, creditsTR, grassTR, carTR;
	
	// CAR
	private int lane;
	
	// TEXT
	private Font font;
	
	public MenuScene(LayoutGameActivity activity, Engine engine, Camera camera, SceneManager sceneManager) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
		this.sceneManager = sceneManager;
		
		map = new WorldMap(activity, camera, 0);
		r = new Random();
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		dodgecarsTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "dodgecars.png");
		playTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "play.png");
		howtoTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "howto.png");
		creditsTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "credits.png");
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		carTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTA, this.activity, "player.png");
		map.loadResources(menuTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32*3f);
		font.load();
		
		try {
			menuTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			menuTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}

	public org.andengine.entity.scene.menu.MenuScene createScene() {
		float screenWidth = camera.getHeight();
		float screenHeight = camera.getWidth();
		menuScene = new org.andengine.entity.scene.menu.MenuScene(camera);
		// SCENE SETUP
		menuScene.setRotation(270);
		menuScene.setPosition(0, camera.getHeight());
		
		// BACKGROUND SETUP
		map.loadMap(menuScene);
		spawnCar(screenWidth, screenHeight);
		
		// BANNER/LOGO
		final Sprite banner = new Sprite((screenWidth - dodgecarsTR.getWidth())/2, screenHeight/6, dodgecarsTR, this.activity.getVertexBufferObjectManager());
		
		// GAME VERSION
		final Text GAME_STAGE = new Text(GameManager.lengthOfTile * 6 + 18, 0, font, GameManager.GAME_VERSION, new TextOptions(HorizontalAlign.LEFT), this.activity.getVertexBufferObjectManager());
		
		// MENU ITEMS
		final IMenuItem buttonPlay = new ScaleMenuItemDecorator(new SpriteMenuItem(PLAY_BTN_ID, playTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		final IMenuItem buttonHowto = new ScaleMenuItemDecorator(new SpriteMenuItem(HOWTO_BTN_ID, howtoTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		final IMenuItem buttonCredits = new ScaleMenuItemDecorator(new SpriteMenuItem(CREDITS_BTN_ID, creditsTR, this.activity.getVertexBufferObjectManager()), unSelected, onSelected);
		buttonPlay.setPosition((screenWidth - playTR.getWidth())/2, screenHeight/6 * 2);
		buttonHowto.setPosition((screenWidth - howtoTR.getWidth())/2, screenHeight/6 * 3);
		buttonCredits.setPosition((screenWidth - creditsTR.getWidth())/2, screenHeight/6 * 4);
		
		menuScene.attachChild(banner);
		menuScene.attachChild(GAME_STAGE);
		menuScene.addMenuItem(buttonPlay);
		menuScene.addMenuItem(buttonHowto);
		menuScene.addMenuItem(buttonCredits);
		
		menuScene.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
				
				switch(pMenuItem.getID()){
				case PLAY_BTN_ID:
					sceneManager.loadGameResources();
					sceneManager.createGameScene();
					sceneManager.setCurrentSence(AllScenes.GAME);
					break;
				case HOWTO_BTN_ID:
					break;
				case CREDITS_BTN_ID:
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
		car.setRotation(90);
		MoveModifier moveModifier = new MoveModifier(4, lane * GameManager.lengthOfTile - GameManager.lengthOfTile/2, lane * GameManager.lengthOfTile - GameManager.lengthOfTile/2, -GameManager.lengthOfTile * 2, screenHeight + GameManager.lengthOfTile*2){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				lane = r.nextInt(6)+1;
				this.reset(4, lane * GameManager.lengthOfTile - GameManager.lengthOfTile/2, lane * GameManager.lengthOfTile - GameManager.lengthOfTile/2, -GameManager.lengthOfTile * 2, screenHeight + GameManager.lengthOfTile*2);
			}
		};
		car.registerEntityModifier(moveModifier);
		menuScene.attachChild(car);
	}

}
