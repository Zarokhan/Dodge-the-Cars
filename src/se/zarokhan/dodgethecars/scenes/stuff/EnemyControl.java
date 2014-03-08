package se.zarokhan.dodgethecars.scenes.stuff;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;

import se.zarokhan.dodgethecars.GameManager;
import android.util.Log;

public class EnemyControl {
	
	private LayoutGameActivity activity;
	private Camera camera;
	
	private ITextureRegion greenCarTR, grayCarTR, purpleCarTR;
	// ENEMY
	public Sprite enemy[];
	private float duration[] = new float[GameManager.INITIAL_ENEMIES];
	private int lane[] = new int[GameManager.INITIAL_ENEMIES];
	private float minDuration[] = new float[GameManager.INITIAL_ENEMIES]; // 1.25f; // minimum duration
	private float maxDuration[] = new float[GameManager.INITIAL_ENEMIES]; // 3.0f; // maximum duration
	private IUpdateHandler handler;
	private Random r;
	
	public EnemyControl(LayoutGameActivity activity, Camera camera, Random r){
		this.activity = activity;
		this.camera = camera;
		this.r = r;
	}
	
	public void loadResources(BuildableBitmapTextureAtlas entityTA){
		greenCarTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "enemy2.png");
		grayCarTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "enemy1.png");
		purpleCarTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "enemy3.png");
	}
	
	public void init(final Scene scene) {
		enemy = new Sprite[GameManager.INITIAL_ENEMIES];
		
		handler = new IUpdateHandler() {
			
			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// SPAWN FIRST ENEMY
				if(GameManager.getInstance().getScore() >= 0 && GameManager.getInstance().getEnemySpawned() == 0){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene, 2f, 3f);
				}
				// SPAWN SECOND ENEMY
				if(GameManager.getInstance().getScore() >= 30 && GameManager.getInstance().getEnemySpawned() == 1){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene, 2f, 3f);
				}
				// SPAWN THIRD ENEMY
				if(GameManager.getInstance().getScore() >= 90 && GameManager.getInstance().getEnemySpawned() == 2){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene, 1.5f, 2f);
				}
				// SPAWN FORUTH ENEMY
				if(GameManager.getInstance().getScore() >= 240 && GameManager.getInstance().getEnemySpawned() == 3){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene, 1.5f, 2f);
				}
				// SPAWN FIFTH ENEMY
				if(GameManager.getInstance().getScore() >= 1000 && GameManager.getInstance().getEnemySpawned() == 4){
					scene.unregisterUpdateHandler(handler);
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene, 0.8f, 1.5f);
					Log.i("Handler", "Unregistered");
				}
			}
		};
		
		scene.registerUpdateHandler(handler);
		Log.i("EnemyControl", "Loaded");
	}
	
	private void spawnEnemy(final int enemyID, boolean newEnemy, Scene scene, float minDur, float maxDur){
		if(newEnemy){
			minDuration[enemyID] = minDur;
			maxDuration[enemyID] = maxDur;
		}
		duration[enemyID] = r.nextFloat() * (maxDuration[enemyID] - minDuration[enemyID]) + minDuration[enemyID];
		lane[enemyID] = newLane(enemyID);
		carType(enemyID);
		MoveModifier moveModifier = new MoveModifier(duration[enemyID], lane[enemyID] * GameManager.lengthOfTile, lane[enemyID] * GameManager.lengthOfTile, -GameManager.lengthOfTile * 2, camera.getHeight() + GameManager.lengthOfTile*2){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				duration[enemyID] = r.nextFloat() * (maxDuration[enemyID] - minDuration[enemyID]) + minDuration[enemyID];
				lane[enemyID] = newLane(enemyID);
				addScore();
				this.reset(duration[enemyID], lane[enemyID] * GameManager.lengthOfTile, lane[enemyID] * GameManager.lengthOfTile, -GameManager.lengthOfTile * 2, camera.getHeight() + GameManager.lengthOfTile*2);
			}
		};
		enemy[enemyID].registerEntityModifier(moveModifier);
		scene.attachChild(enemy[enemyID]);
		if(newEnemy)GameManager.getInstance().enemySpawned();
		Log.i("SPAWNED: ", "ENEMY");
	}

	private void carType(int enemyID) {
		if(enemyID == 0 || enemyID == 1){
			enemy[enemyID] = new Sprite(-300, 0, grayCarTR, this.activity.getVertexBufferObjectManager());
		}else if(enemyID == 2 || enemyID == 3){
			enemy[enemyID] = new Sprite(-300, 0, greenCarTR, this.activity.getVertexBufferObjectManager());
		}else if(enemyID == 4){
			enemy[enemyID] = new Sprite(-300, 0, purpleCarTR, this.activity.getVertexBufferObjectManager());
		}
	}

	public int newLane(int enemyID){
		int lane = r.nextInt(6)+1;
		// CHECK IF FIRST CAR
		if(GameManager.getInstance().getEnemySpawned() <= 1) return lane;
		// CHECK IF ANY CAR IS ON THAT LANE
		for(int searchID = 0; searchID < GameManager.getInstance().getEnemySpawned(); searchID++){
			if(this.lane[searchID] == lane && searchID != enemyID){
				lane = r.nextInt(6)+1;
				searchID = 0;
				continue;
			}else if (searchID == GameManager.getInstance().getEnemySpawned() -1){
				return lane;
			}
		}
		return -GameManager.lengthOfTile;
	}
	
	public void resetEnemy(int enemyID, Scene scene) {
		Log.i("Reset", "Enemy");
		removeSprite(enemy[enemyID]);
		spawnEnemy(enemyID, false, scene, 0, 0);
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
}
