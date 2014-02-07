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
	
	private ITextureRegion playerTR;
	// ENEMY
	public Sprite enemy[];
	private float duration[] = new float[GameManager.INITIAL_ENEMIES];
	private int lane[] = new int[GameManager.INITIAL_ENEMIES];
	private float minDuration = 1.25f; // minimum duration
	private float maxDuration = 3.0f; // maximum duration
	private IUpdateHandler handler;
	private Random r;
	
	public EnemyControl(LayoutGameActivity activity, Camera camera, Random r){
		this.activity = activity;
		this.camera = camera;
		this.r = r;
	}
	
	public void loadResources(BuildableBitmapTextureAtlas entityTA){
		playerTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(entityTA, this.activity, "player.png");
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
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene);
				}
				// SPAWN SECOND ENEMY
				if(GameManager.getInstance().getScore() >= 30 && GameManager.getInstance().getEnemySpawned() == 1){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene);
				}
				// SPAWN THIRD ENEMY
				if(GameManager.getInstance().getScore() >= 40 && GameManager.getInstance().getEnemySpawned() == 2){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene);
				}
				// SPAWN FORUTH ENEMY
				if(GameManager.getInstance().getScore() >= 80 && GameManager.getInstance().getEnemySpawned() == 3){
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene);
				}
				// SPAWN FIFTH ENEMY
				if(GameManager.getInstance().getScore() >= 120 && GameManager.getInstance().getEnemySpawned() == 4){
					scene.unregisterUpdateHandler(handler);
					spawnEnemy(GameManager.getInstance().getEnemySpawned(), true, scene);
					Log.i("Handler", "Unregistered");
				}
			}
		};
		
		scene.registerUpdateHandler(handler);
		Log.i("EnemyControl", "Loaded");
	}
	
	private void spawnEnemy(final int enemyID, boolean newEnemy, Scene scene){
		duration[enemyID] = r.nextFloat() * (maxDuration - minDuration) + minDuration;
		lane[enemyID] = newLane(enemyID);
		enemy[enemyID] = new Sprite(-300, 0, playerTR, this.activity.getVertexBufferObjectManager());
		MoveModifier moveModifier = new MoveModifier(duration[enemyID], -GameManager.lengthOfTile * 2, camera.getWidth() + GameManager.lengthOfTile*2, lane[enemyID] * GameManager.lengthOfTile, lane[enemyID] * GameManager.lengthOfTile){
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				duration[enemyID] = r.nextFloat() * (maxDuration - minDuration) + minDuration;
				lane[enemyID] = newLane(enemyID);
				addScore();
				this.reset(duration[enemyID], -GameManager.lengthOfTile * 2, camera.getWidth() + GameManager.lengthOfTile*2, lane[enemyID] * GameManager.lengthOfTile, lane[enemyID] * GameManager.lengthOfTile);
			}
		};
		enemy[enemyID].registerEntityModifier(moveModifier);
		scene.attachChild(enemy[enemyID]);
		if(newEnemy)GameManager.getInstance().enemySpawned();
		Log.i("SPAWNED: ", "ENEMY");
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
		spawnEnemy(enemyID, false, scene);
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
