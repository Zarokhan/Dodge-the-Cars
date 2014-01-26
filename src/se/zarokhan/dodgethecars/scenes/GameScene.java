package se.zarokhan.dodgethecars.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
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
import se.zarokhan.dodgethecars.scenes.stuff.Player;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class GameScene {

	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private WorldMap map;
	private Player player;
	//private Input input;
	private Random r;
	
	private Scene scene;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas mapTA;
	private BuildableBitmapTextureAtlas entityTA;
	private ITextureRegion playerTR, hearthTR, arrowTR;
	
	// TEXT
	private Font font;
	private Text textScore;
	
	// ENEMY
	private Sprite enemy[];
	private int speed[] = new int[GameManager.INITIAL_ENEMIES];
	private int lane[] = new int[GameManager.INITIAL_ENEMIES];
	private int minSpeed = 2; // minimum speed
	private int ranSpeed = 3; // Max random speed
	
	// HUD
	HUD hud;
	private Sprite hearth[];
	
	public GameScene(LayoutGameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
		
		map = new WorldMap(activity, camera, 22);
		r = new Random();
		player = new Player(activity);
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
		playerTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "player.png");
		
		
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
		scene = new Scene();
		
		map.loadMap(scene);
		player.loadPlayer(scene, camera.getWidth());
		enemyControl();
		initCollision();
		//input.loadInput(scene);
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
						diffX[i] = Math.abs( (player.getSprite().getX() +  player.getSprite().getWidth()/2 ) - (enemy[i].getX() + enemy[i].getWidth()/2 ));
						diffY[i] = Math.abs( (player.getSprite().getY() +  player.getSprite().getHeight()/2 ) - (enemy[i].getY() + enemy[i].getHeight()/2 ));
						
						if(diffX[i] < 20 + (player.getSprite().getWidth()/2 + enemy[i].getWidth()/3) && diffY[i] < (player.getSprite().getHeight()/2 + enemy[i].getHeight()/3)){
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
			textScore = new Text(0, 0, font, "Score: " + GameManager.getInstance().getScore(), "Score: 1234567890".length(), this.activity.getVertexBufferObjectManager());
			hud.attachChild(textScore);
			
			engine.stop();
		}else{
			removeSprite(hearth[GameManager.getInstance().getHealth() - 1]);
			GameManager.getInstance().removeHealth();
			resetEnemy(enemyID);
		}
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
	//	Enemy Control
	//
	
	private IUpdateHandler handler;
	
	private void enemyControl() {
		enemy = new Sprite[GameManager.INITIAL_ENEMIES];
		
		handler = new IUpdateHandler() {
			
			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// SPAWN FIRST ENEMY
				if(GameManager.getInstance().getScore() >= 0 && GameManager.getInstance().getEnemySpawned() == 0){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true);
				}
				// SPAWN SECOND ENEMY
				if(GameManager.getInstance().getScore() >= 30 && GameManager.getInstance().getEnemySpawned() == 1){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true);
				}
				// SPAWN THIRD ENEMY
				if(GameManager.getInstance().getScore() >= 40 && GameManager.getInstance().getEnemySpawned() == 2){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true);
				}
				// SPAWN FORUTH ENEMY
				if(GameManager.getInstance().getScore() >= 80 && GameManager.getInstance().getEnemySpawned() == 3){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true);
				}
				// SPAWN FIFTH ENEMY
				if(GameManager.getInstance().getScore() >= 120 && GameManager.getInstance().getEnemySpawned() == 4){
					scene.unregisterUpdateHandler(handler);
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true);
					ranSpeed -= 1;
					Log.i("Handler", "Unregistered");
				}
			}
		};
		
		scene.registerUpdateHandler(handler);
		Log.i("EnemyControl", "Loaded");
	}
	
	private void spawnEnemy(final int enemyID, boolean newEnemy){
		speed[enemyID] = r.nextInt(ranSpeed) + minSpeed;
		lane[enemyID] = newLane(enemyID);
		enemy[enemyID] = new Sprite(-300, 0, playerTR, this.activity.getVertexBufferObjectManager());
		MoveModifier moveModifier = new MoveModifier(speed[enemyID], -GameManager.lengthOfTile * 2, camera.getWidth() + GameManager.lengthOfTile*2, lane[enemyID] * GameManager.lengthOfTile, lane[enemyID] * GameManager.lengthOfTile){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				speed[enemyID] = r.nextInt(ranSpeed) + minSpeed;
				lane[enemyID] = newLane(enemyID);
				addScore();
				this.reset(speed[enemyID], -GameManager.lengthOfTile * 2, camera.getWidth() + GameManager.lengthOfTile*2, lane[enemyID] * GameManager.lengthOfTile, lane[enemyID] * GameManager.lengthOfTile);
			}
		};
		enemy[enemyID].registerEntityModifier(moveModifier);
		scene.attachChild(enemy[enemyID]);
		if(newEnemy)GameManager.getInstance().enemySpawned();
		Log.i("SPAWNED: ", "ENEMY");
	}
	
	private int newLane(int enemyID){
		int lane = r.nextInt(6)+1;
		
		// CHECK IF ONLY CAR
		if(GameManager.getInstance().getEnemySpawned() < 2){
			return lane;
		}else{
			// CHECK IF ANY CAR IS ON THAT LANE
			for(int i = 0; i < GameManager.getInstance().getEnemySpawned(); i++){
				if(i == enemyID)continue;
				if(this.lane[i] == lane){
					// CHECK IF CAR IS OVER HALF THE MAP
					if(enemy[i].getX() >= camera.getWidth()/2){
						lane = r.nextInt(6)+1;
						i = 0;
					}else{
						return lane;
					}
				}else{
					return lane;
				}
			}
		}
		
		return lane;
	}
	
	private void resetEnemy(int enemyID) {
		Log.i("Reset", "Enemy");
		removeSprite(enemy[enemyID]);
		spawnEnemy(enemyID, false);
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
		
		// ADD THE HEARTHS
		hearth = new Sprite[GameManager.getInstance().getHealth()];
		for(int i = 0; i < GameManager.getInstance().getHealth(); i++){
			hearth[i] = new Sprite(i * GameManager.lengthOfTile, 0, hearthTR, this.activity.getVertexBufferObjectManager());
			hearth[i].setScale(0.7f);
			hud.attachChild(hearth[i]);
		}
		
		// GAME VERSION
		final Text version = new Text(GameManager.lengthOfTile * 6 + 18, 0, font, GameManager.GAME_VERSION, new TextOptions(HorizontalAlign.LEFT), this.activity.getVertexBufferObjectManager());
		version.setColor(Color.WHITE);
		hud.attachChild(version);
		
		// Buttons
		final Sprite leftButton = new Sprite(0, camera.getWidth()/6 * 4, arrowTR, this.activity.getVertexBufferObjectManager());
		final Sprite rightButton = new Sprite(camera.getHeight()-arrowTR.getWidth(), camera.getWidth()/6 * 4, arrowTR, this.activity.getVertexBufferObjectManager());
		rightButton.setRotation(180);
		
		final Rectangle left = new Rectangle(0, 0, screenWidth/2, screenHeight, this.activity.getVertexBufferObjectManager()){
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if(touchEvent.isActionDown() && player.playerY < GameManager.lengthOfTile * 6) {
					player.moveCarNorth();
				}
				return true;
			};
		};
		final Rectangle right = new Rectangle(screenWidth/2, 0, screenWidth/2, screenHeight, this.activity.getVertexBufferObjectManager()){
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
