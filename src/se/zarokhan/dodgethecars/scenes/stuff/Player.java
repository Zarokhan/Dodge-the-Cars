package se.zarokhan.dodgethecars.scenes.stuff;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;

import se.zarokhan.dodgethecars.GameManager;

public class Player{

	private LayoutGameActivity activity;
	
	private TextureRegion playerTR;
	
	// PLAYER
	private Sprite player;
	private int Lane = 4;
	public float playerX;
	public float playerY;
	
	public Player(LayoutGameActivity activity) {
		this.activity = activity;
	}

	public void loadResources(BuildableBitmapTextureAtlas textureTA) {
		playerTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureTA, this.activity, "player.png");
	}

	public void loadPlayer(Scene scene, float screenWidth) {
		playerX = screenWidth - GameManager.lengthOfTile * 5;
		playerY = GameManager.lengthOfTile * Lane;
		player = new Sprite(playerX, playerY, GameManager.lengthOfTile*2, GameManager.lengthOfTile -1, playerTR, this.activity.getVertexBufferObjectManager());
		player.setRotation(180);
		scene.attachChild(player);
	}

	public void moveCarNorth() {
		playerY += GameManager.lengthOfTile;
		player.setPosition(playerX, playerY);
	}

	public void moveCarSouth() {
		playerY -= GameManager.lengthOfTile;
		player.setPosition(playerX, playerY);
	}
	
	public int getLane(){
		return Lane;
	}
	
	public void setLane(int Lane){
		this.Lane = Lane;
	}
	
	public Sprite getSprite() {
		return player;
	}
}
