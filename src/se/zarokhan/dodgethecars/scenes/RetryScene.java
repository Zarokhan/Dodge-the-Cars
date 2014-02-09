package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class RetryScene {
	
	private LayoutGameActivity activity;
	private Camera camera;
	private SceneManager sceneManager;
	
	// SCENE
	private Scene scene;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas entityTA;
	private TextureRegion zarokhanTR;
	
	// TEXT
	private Font font;
		
	
	public RetryScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager){
		this.activity = activity;
		this.camera = camera;
		this.sceneManager = sceneManager;
	}
	
	public void loadResources(){
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32*3f);
		font.load();
	}
	
	public Scene createScene(WorldMap map){
		int screenWidth = (int) camera.getHeight();
		int screenHeight = (int) camera.getWidth();
		
		scene = new Scene();
		map.loadMap(scene);
		map.slowDown();
		
		HUD hud = new HUD();
		// SETUP POS AND ANGLE
		hud.setRotation(270);
		hud.setPosition(0, camera.getHeight());
		
		Text highscore = new Text(GameManager.lengthOfTile, GameManager.lengthOfTile, font, "SCORE: " + GameManager.getInstance().getScore(), this.activity.getVertexBufferObjectManager());
		Rectangle buttonRetry = new Rectangle(GameManager.lengthOfTile,
				GameManager.lengthOfTile * 3, GameManager.lengthOfTile * 4,
				GameManager.lengthOfTile,
				this.activity.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					camera.setHUD(null);
					sceneManager.setCurrentSence(AllScenes.GAME);
				}
				return true;
			}
		};
        buttonRetry.setColor(new Color(Color.BLUE));
        Rectangle buttonHome = new Rectangle(GameManager.lengthOfTile * 5, GameManager.lengthOfTile * 3, GameManager.lengthOfTile * 2, GameManager.lengthOfTile, this.activity.getVertexBufferObjectManager()){
        	public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y){
                if (touchEvent.isActionDown()){
                	sceneManager.createMenuScene();
                	sceneManager.setCurrentSence(AllScenes.MENU);
                }
                return true;
            }	
        };
        buttonHome.setColor(new Color(Color.RED));
        
        hud.registerTouchArea(buttonHome);
        hud.registerTouchArea(buttonRetry);
        hud.attachChild(buttonHome);
        hud.attachChild(highscore);
        hud.attachChild(buttonRetry);
        camera.setHUD(hud);
		return scene;
	}
}
