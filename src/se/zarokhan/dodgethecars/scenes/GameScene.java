package se.zarokhan.dodgethecars.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
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
import se.zarokhan.dodgethecars.mSoundManager;
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
	private mSoundManager sounds;
	
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
	
	public GameScene(LayoutGameActivity activity, Engine engine, Camera camera, SceneManager sceneManager, mSoundManager sounds) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
		this.sceneManager = sceneManager;
		this.sounds = sounds;
		
		map = new WorldMap(activity, camera, 22);
		r = new Random();
		player = new Player(activity);
		enemyControl = new EnemyControl(activity, camera, r);
		//input = new Input(activity, camera, player);
	}

	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mapTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.REPEATING_NEAREST);
		entityTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		map.loadResources(mapTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32*3f);
		font.load();
		player.loadResources(entityTA);
		arrowTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "arrow.png");
		hearthTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "hearth.png");
		hpTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "hp.png");
		enemyControl.loadResources(entityTA);
		
		sounds.loadResources(entityTA);
		
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
		//enemyControl();
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
			sounds.playCrash();
			afterDeath();
		}else{
			sounds.playSlide();
			removeSprite(hearth[GameManager.getInstance().getHealth() - 1]);
			GameManager.getInstance().removeHealth();
			enemyControl.resetEnemy(enemyID, scene);
		}
	}
	
	public void clearScene() {
		camera.setHUD(null);
		scene.clearChildScene();
		scene.clearEntityModifiers();
		scene.clearUpdateHandlers();
	}
	
	private void afterDeath() {
		clearScene();
		sceneManager.createRetryScene();
		sceneManager.setCurrentSence(AllScenes.RETRY);
	}

	private void removeSprite(Sprite sprite) {
		sprite.detachSelf();
		sprite.dispose();
		Log.i("Removed", "REMOVED A SPRITE");
	}
		
	//
	//	HUD
	//
	
	private void initHUD(){
		hud = new HUD();
		
		// ADD THE HEARTHS
		final Sprite hpText = new Sprite(0, (GameManager.lengthOfTile - hpTR.getHeight())/2, hpTR, this.activity.getVertexBufferObjectManager());
		hud.attachChild(hpText);
		hearth = new Sprite[GameManager.getInstance().getHealth()];
		for(int i = 0; i < GameManager.getInstance().getHealth(); i++){
			hearth[i] = new Sprite(GameManager.lengthOfTile + (i * GameManager.lengthOfTile), 0, hearthTR, this.activity.getVertexBufferObjectManager());
			hearth[i].setScale(0.7f);
			hud.attachChild(hearth[i]);
		}
		
		// GAME VERSION
		final Text version = new Text(GameManager.lengthOfTile * 6 - GameManager.lengthOfTile/2, 0, font, GameManager.GAME_VERSION, new TextOptions(HorizontalAlign.LEFT), this.activity.getVertexBufferObjectManager());
		version.setColor(Color.WHITE);
		hud.attachChild(version);
		
		// ADD THE ARROWS
		final Sprite arrowLeft = new Sprite(0, camera.getHeight()/6 * 4, arrowTR, this.activity.getVertexBufferObjectManager());
		final Sprite arrowRight = new Sprite(camera.getWidth()-arrowTR.getWidth(), camera.getHeight()/6 * 4, arrowTR, this.activity.getVertexBufferObjectManager());
		arrowRight.setRotation(180);
		arrowLeft.setScale(0.7f);
		arrowRight.setScale(0.7f);
		
		hud.attachChild(arrowRight);
		hud.attachChild(arrowLeft);
		
		// ADD CONTROLLABLE BUTTONS
		final Rectangle leftB = new Rectangle(0, 0, camera.getWidth() / 2, camera.getHeight(), this.activity.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown() && player.playerX > GameManager.lengthOfTile) {
					sounds.playBlop();
					player.moveCarLeft();
				}
				return true;
			};
		};
		final Rectangle rightB = new Rectangle(camera.getWidth() / 2, 0, camera.getWidth() / 2, camera.getHeight(), this.activity.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown() && player.playerX < GameManager.lengthOfTile * 6) {
					sounds.playBlop();
					player.moveCarRight();
				}
				return true;
			};
		};
		leftB.setAlpha(255);
		rightB.setAlpha(255);
		hud.registerTouchArea(leftB);
		hud.registerTouchArea(rightB);
		hud.attachChild(leftB);
		hud.attachChild(rightB);
		
		// ATTACH HUD
		camera.setHUD(hud);
		Log.i("HUD", "HUD loaded");
	}
	
	public Scene getScene() {
		return scene;
	}
}
