package se.zarokhan.dodgethecars;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;

import se.zarokhan.dodgethecars.scenes.CreditsScene;
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
	private CreditsScene creditScene;
	
	public enum AllScenes{
		SPLASH, MENU, GAME, RETRY, HIGHSCORE, CREDITS
	}
	
	public SceneManager(LayoutGameActivity act, Engine eng, Camera cam, mSoundManager sounds) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		
		splashSceneC = new SplashScene(this.activity, this.camera, this, sounds);
		menuSceneC = new MenuScene(this.activity, this.camera, this, sounds);
		gameSceneC = new GameScene(this.activity, this.engine, this.camera, this, sounds);
		retrySceneC = new RetryScene(activity, camera, this, sounds);
		creditScene = new CreditsScene(activity, camera, this, sounds);
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
	
	public void loadCreditsResources(){
		creditScene.loadResources();
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
		return retrySceneC.createScene(false);
	}
	
	public Scene createCreditScene(){
		return creditScene.createScene();
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
		case HIGHSCORE:
			engine.setScene(retrySceneC.createScene(true));
		case CREDITS:
			engine.setScene(creditScene.getScene());
			
		default:
			break;
		}
	}
}
