package se.zarokhan.dodgethecars.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;
import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.scenes.stuff.EnemyControl;
import se.zarokhan.dodgethecars.scenes.stuff.Player;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class GameScene {

	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private WorldMap map;
	private Player player;
	private SceneManager sceneManager;
	private EnemyControl enemyControl;
	private Random r;
	
	private Scene scene;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas mapTA;
	private BuildableBitmapTextureAtlas entityTA;
	private ITextureRegion hearthTR, arrowTR, hpTR;
	
	// TEXT
	private Font font;
	
	// HUD
	HUD hud;
	private Sprite hearth[];
	private ScaleModifier hearthScaleMod;
	private float hearthStartScale = 0.8f;
	private float hearthEndScale = 0.95f;
	private int hearthScaleDuration = 5;
	private boolean hearthBool = false;
	
	public GameScene(LayoutGameActivity activity, Engine engine, Camera camera, SceneManager sceneManager) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
		this.sceneManager = sceneManager;
		
		map = new WorldMap(activity, camera, engine, 22);
		r = new Random();
		player = new Player(activity);
		enemyControl = new EnemyControl(activity, camera, r);
		//input = new Input(activity, camera, player);
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mapTA = new BuildableBitmapTextureAtlas(
				this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.REPEATING_NEAREST);
		entityTA = new BuildableBitmapTextureAtlas(
				this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		map.loadResources(mapTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32*3f);
		font.load();
		player.loadResources(entityTA);
		arrowTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "arrow.png");
		hearthTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "hearth.png");
		hpTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "hp.png");
		enemyControl.loadResources(entityTA);
		
		try {
			mapTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			mapTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		try {
			entityTA
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));
			entityTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}

	public Scene createScene() {
		scene = null;
		scene = new Scene();
		
		map.loadMap(scene);
		player.loadPlayer(scene, camera.getWidth());
		enemyControl.init(scene);
		initCollision();
		initHUD();
		return scene;
	}
	
	//
	//	OTHER
	//
	
	private void initCollision() {
		engine.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// Check if enemy has spawned
				if(GameManager.getInstance().getEnemySpawned() > 0){
					float diffX[] = new float[GameManager.INITIAL_ENEMIES];
					float diffY[] = new float[GameManager.INITIAL_ENEMIES];
					// Check collision
					for(int i = 0; i < GameManager.getInstance().getEnemySpawned(); i++){
						diffX[i] = Math.abs( (player.getSprite().getX() +  player.getSprite().getWidth()/2 ) - (enemyControl.enemy[i].getX() + enemyControl.enemy[i].getWidth()/2 ));
						diffY[i] = Math.abs( (player.getSprite().getY() +  player.getSprite().getHeight()/2 ) - (enemyControl.enemy[i].getY() + enemyControl.enemy[i].getHeight()/2 ));
						
						if(diffX[i] < 20 + (player.getSprite().getWidth()/2 + enemyControl.enemy[i].getWidth()/3) && diffY[i] < (player.getSprite().getHeight()/2 + enemyControl.enemy[i].getHeight()/3)){
							checkHealth(i);
						}
					}
				}
			}
		});
		Log.i("Collisions", "Loaded");
	}
	
	private void checkHealth(int enemyID){
		if(GameManager.getInstance().getHealth() == 0){
			afterDeath();
		}else{
			removeSprite(hearth[GameManager.getInstance().getHealth() - 1]);
			GameManager.getInstance().removeHealth();
			enemyControl.resetEnemy(enemyID, scene);
		}
	}
	
	private void afterDeath() {
		camera.setHUD(null);
		scene.clearChildScene();
		scene.clearEntityModifiers();
		scene.clearUpdateHandlers();
		GameManager.getInstance().resetGame();
		sceneManager.createMenuScene();
		sceneManager.setCurrentSence(AllScenes.MENU);
	}

	private void removeSprite(Sprite sprite) {
		sprite.detachSelf();
		sprite.dispose();
		Log.i("Removed", "REMOVED A SPRITE");
	}
	
	private void addScore() {
		GameManager.getInstance().addScore(GameManager.POINTS_FOR_STANDARDENEMY);
		Log.i("Score: ", "" + GameManager.getInstance().getScore());
	}
		
	//
	//	HUD
	//
	
	private void initHUD(){
		int screenWidth = (int) camera.getHeight();
		int screenHeight = (int) camera.getWidth();
		
		hud = new HUD();
		// SETUP POS AND ANGLE
		hud.setRotation(270);
		hud.setPosition(0, camera.getHeight());
		
		// ADD TRANSPARENT BAR ATT THE TOP
		/*
		final Rectangle top = new Rectangle(0, 0, screenWidth, GameManager.lengthOfTile, this.activity.getVertexBufferObjectManager());
		top.setColor(new Color(Color.BLACK));
		top.setAlpha(50);
		hud.attachChild(top);
		*/
		
		// ADD THE HEARTHS
		final Sprite hpText = new Sprite(0, (GameManager.lengthOfTile - hpTR.getHeight())/2, hpTR, this.activity.getVertexBufferObjectManager());
		hud.attachChild(hpText);
		hearth = new Sprite[GameManager.getInstance().getHealth()];
		for(int i = 0; i < GameManager.getInstance().getHealth(); i++){
			hearth[i] = new Sprite(GameManager.lengthOfTile + (i * GameManager.lengthOfTile), 0, hearthTR, this.activity.getVertexBufferObjectManager());
			hearth[i].setScale(0.7f);
			hud.attachChild(hearth[i]);
		}
		
		/*
		hearthScaleMod = new ScaleModifier(hearthScaleDuration, hearthStartScale, hearthEndScale);
		hearth[GameManager.getInstance().getHealth() - 1].registerEntityModifier(hearthScaleMod);
		*/
		
		// GAME VERSION
		final Text version = new Text(GameManager.lengthOfTile * 6 + 18, 0, font, GameManager.GAME_VERSION, new TextOptions(HorizontalAlign.LEFT), this.activity.getVertexBufferObjectManager());
		version.setColor(Color.WHITE);
		hud.attachChild(version);
		
		// Buttons
		final Sprite leftButton = new Sprite(0, camera.getWidth()/6 * 4, arrowTR, this.activity.getVertexBufferObjectManager());
		final Sprite rightButton = new Sprite(camera.getHeight()-arrowTR.getWidth(), camera.getWidth()/6 * 4, arrowTR, this.activity.getVertexBufferObjectManager());
		leftButton.setScale(0.85f);
		rightButton.setScale(0.85f);
		rightButton.setRotation(180);
		
		final Rectangle left = new Rectangle(0, GameManager.lengthOfTile, screenWidth/2, screenHeight, this.activity.getVertexBufferObjectManager()){
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown() && player.playerY < GameManager.lengthOfTile * 6) {
					player.moveCarNorth();
				}
				return true;
			};
		};
		final Rectangle right = new Rectangle(screenWidth/2, GameManager.lengthOfTile, screenWidth/2, screenHeight, this.activity.getVertexBufferObjectManager()){
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown() && player.playerY > GameManager.lengthOfTile) {
					player.moveCarSouth();
				}
				return true;
			};
		};
		left.setAlpha(255);
		right.setAlpha(255);
		hud.registerTouchArea(left);
	    hud.registerTouchArea(right);
		hud.attachChild(leftButton);
		hud.attachChild(rightButton);
		hud.attachChild(left);
		hud.attachChild(right);
		
		// ATTACH HUD
		camera.setHUD(hud);
		Log.i("HUD", "HUD loaded");
	}
}
