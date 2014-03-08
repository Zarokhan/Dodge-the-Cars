package se.zarokhan.dodgethecars;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class MainGameActivity extends LayoutGameActivity{
	/*
	static final int CAMERA_WIDTH = 1920;
	static final int CAMERA_HEIGHT = 1080;
	*/
	
	static final int CAMERA_WIDTH = 1080;
	static final int CAMERA_HEIGHT = 1920;
	
	SceneManager sceneManager;
	Camera camera;
	
	private mSoundManager sounds;
	
	private int splashSeconds = 1;
	private RelativeLayout relativeLayout;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		options.getAudioOptions().setNeedsMusic(true);
		options.getAudioOptions().setNeedsSound(true);
		return options;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		
		sounds = new mSoundManager(this, camera);
		sceneManager = new SceneManager(this, mEngine, camera, sounds);
		sounds.loadResources();
		sceneManager.loadSplashResources();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		
		mEngine.registerUpdateHandler(new TimerHandler(splashSeconds, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
					sceneManager.createMenuScene();
					sceneManager.setCurrentSence(AllScenes.MENU);
			}
		}));
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public synchronized void onResumeGame() {
		if (!sounds.isMusicNull() && !sounds.isMusicPlaying()){
			sounds.playMusic();
		}
		
		super.onResumeGame();
	}

	@Override
	public synchronized void onPauseGame() {
		if (!sounds.isMusicNull() && sounds.isMusicPlaying()){
			sounds.pause();
		}
		
		super.onPauseGame();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK){
	    	if(sceneManager.getCurrentSence() != SceneManager.AllScenes.MENU){
	    		camera.setHUD(null);
	    		GameManager.getInstance().resetGame();
	    		sceneManager.createMenuScene();
	    		sceneManager.setCurrentSence(AllScenes.MENU);
	    	}else{
	    		finish();
	    	}
	    }
	    return false; 
	}

	@Override
	protected int getLayoutID() {
		return R.layout.activity_main_game;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.SurfaceViewId;
	}
}
