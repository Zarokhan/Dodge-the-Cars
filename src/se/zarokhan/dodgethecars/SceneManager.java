package se.zarokhan.dodgethecars;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;
import se.zarokhan.dodgethecars.scenes.GameScene;
import se.zarokhan.dodgethecars.scenes.MenuScene;
import se.zarokhan.dodgethecars.scenes.RetryScene;
import se.zarokhan.dodgethecars.scenes.SplashScene;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class SceneManager {
	private AllScenes currentScene;
	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private SplashScene splashSceneC;
	private MenuScene menuSceneC;
	private GameScene gameSceneC;
	private RetryScene retrySceneC;
	
	private Scene splashScene, gameScene, retryScene;
	private org.andengine.entity.scene.menu.MenuScene menuScene;
	
	public enum AllScenes{
		SPLASH, MENU, GAME, RETRY
	}
	
	public SceneManager(LayoutGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		
		splashSceneC = new SplashScene(this.activity, this.engine, this.camera);
		menuSceneC = new MenuScene(this.activity, this.engine, this.camera, this);
		gameSceneC = new GameScene(this.activity, this.engine, this.camera, this);
		retrySceneC = new RetryScene(activity, camera, this);
	}
	
	// RESOURCES
	public void loadSplashResources() {
		splashSceneC.loadResources();
	}
	
	public void loadMenuResources() {
		menuSceneC.loadResources();
	}
	
	public void loadGameResources() {
		gameSceneC.loadResources();
	}
	
	public void loadRetryResources(){
		retrySceneC.loadResources();
	}
	
	// SCENES
	public Scene createSplashScene() {
		splashScene = new Scene();
		splashScene.attachChild(splashSceneC.createScene());
		return splashScene;
	}
	
	public org.andengine.entity.scene.menu.MenuScene createMenuScene() {
		
		menuScene = new org.andengine.entity.scene.menu.MenuScene(camera);
		menuScene.setChildScene(menuSceneC.createScene());
		return menuScene;
	}
	
	public Scene createGameScene() {
		gameScene = new Scene();
		gameScene.attachChild(gameSceneC.createScene());
		return gameScene;
	}
	
	public Scene createRetryScene(WorldMap map){
		retryScene = new Scene();
		retryScene.attachChild(retrySceneC.createScene(map));
		return retryScene;
	}
	
	// OTHER
	public AllScenes getCurrentSence() {
		return currentScene;
	}

	public void setCurrentSence(AllScenes currentScene) {
		this.currentScene = currentScene;
		switch (currentScene) {
		case SPLASH:
			engine.setScene(splashScene);
			break;

		case MENU:
			engine.setScene(menuScene);
			break;
			
		case GAME:
			engine.setScene(gameScene);
			break;
		case RETRY:
			engine.setScene(retryScene);
			break;
		default:
			break;
		}
	}
}
