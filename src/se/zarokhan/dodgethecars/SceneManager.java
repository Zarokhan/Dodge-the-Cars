package se.zarokhan.dodgethecars;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;
import se.zarokhan.dodgethecars.scenes.GameScene;
import se.zarokhan.dodgethecars.scenes.MenuScene;
import se.zarokhan.dodgethecars.scenes.RetryScene;
import se.zarokhan.dodgethecars.scenes.SplashScene;

public class SceneManager {
	private AllScenes currentScene;
	private LayoutGameActivity activity;
	private Engine engine;
	private Camera camera;
	
	private SplashScene splashSceneC;
	private MenuScene menuSceneC;
	private GameScene gameSceneC;
	private RetryScene retrySceneC;
	
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
		retrySceneC = new RetryScene(activity, engine, camera, this);
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
	
	public void loadRetryResoruces(){
		retrySceneC.loadResources();
	}
	
	// SCENES
	public Scene createSplashScene() {
		return splashSceneC.createScene();
	}
	
	public org.andengine.entity.scene.menu.MenuScene createMenuScene() {
		return menuSceneC.createScene();
	}
	
	public Scene createGameScene() {
		return gameSceneC.createScene();
	}
	
	public org.andengine.entity.scene.menu.MenuScene createRetryScene() {
		return retrySceneC.createScene();
	}
	
	// OTHER
	public AllScenes getCurrentSence() {
		return currentScene;
	}

	public void setCurrentSence(AllScenes currentScene) {
		this.currentScene = currentScene;
		switch (currentScene) {
		case SPLASH:
			engine.setScene(splashSceneC.getScene());
			break;

		case MENU:
			engine.setScene(menuSceneC.getScene());
			break;
			
		case GAME:
			engine.setScene(gameSceneC.getScene());
			break;
		case RETRY:
			engine.setScene(retrySceneC.getScene());
			break;
			
		default:
			break;
		}
	}
}
