package se.zarokhan.dodgethecars.scenes;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import android.graphics.Typeface;
import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.UserData;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.mSoundManager;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class RetryScene {
	
	private final static int HOME_BTN_ID = 0;
	private final static int RETRY_BTN_ID = 1;
	
	private LayoutGameActivity activity;
	private Camera camera;
	private Engine mEngine;
	
	private SceneManager sceneManager;
	private WorldMap map;
	private MenuScene scene;
	private mSoundManager sounds;
	
	private BuildableBitmapTextureAtlas retryTA;
	private TextureRegion highscoreTR, retryTR, homeTR, recordTR, scoreTR, bestScoreTR;
	private TextureRegion[] numberTR = new TextureRegion[10];
	
	private Font font;
	
	float screenWidth, screenHeight;
	
	public RetryScene(LayoutGameActivity activity, Camera camera, Engine mEngine, SceneManager sceneManager, mSoundManager sounds){
		this.activity = activity;
		this.camera = camera;
		this.mEngine = mEngine;
		this.sceneManager = sceneManager;
		this.sounds = sounds;
		
		screenWidth = camera.getWidth();
		screenHeight = camera.getHeight();
		
		map = new WorldMap(this.activity, this.camera, 0);
	}
	
	public void loadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		retryTA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		highscoreTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "menu/highscore.png");
		retryTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "retry.png");
		homeTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "home.png");
		recordTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "score/record.png");
		scoreTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "score/score.png");
		bestScoreTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "score/bestscore.png");
		for(int i = 0; i < 10; i++){
			numberTR[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(retryTA, this.activity, "score/" + i + ".png");
		}
		
		map.loadResources(retryTA);
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), GameManager.lengthOfTile);
		font.load();
		
		sounds.loadResources();
		
		try {
			retryTA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			retryTA.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}
	
	public MenuScene createScene(){
		scene = new org.andengine.entity.scene.menu.MenuScene(camera);
		map.loadMap(scene);
		
		int currentScore = GameManager.getInstance().getScore();
		int bestScore = UserData.getInstance().getHighestScore();
		int padding = 50;
		int spacing = 125;
		
		// CHECK IF NEW RECORD AND SAVE NEW RECORD
		if(GameManager.getInstance().getScore() > UserData.getInstance().getHighestScore()){
			UserData.getInstance().setHighestScore(GameManager.getInstance().getScore());
			Sprite record = new Sprite(0, screenHeight/8 + highscoreTR.getHeight() + scoreTR.getHeight() * 4 + padding * 5, recordTR, this.activity.getVertexBufferObjectManager());
			record.registerEntityModifier(new MoveXModifier(5, camera.getWidth(), -recordTR.getWidth()){
				@Override
				protected void onModifierFinished(IEntity pItem) {
					super.onModifierFinished(pItem);
					this.reset(5, camera.getWidth(), -recordTR.getWidth());
				}
			});
			scene.attachChild(record);
		}
		
		final Sprite highscores = new Sprite((screenWidth - highscoreTR.getWidth())/2, screenHeight/8, highscoreTR, this.activity.getVertexBufferObjectManager());
		scene.attachChild(highscores);
		
		List<Integer> allDidgitsFromCurrentScore = new ArrayList<Integer>();
		List<Integer> allDidgitsFromBestScore = new ArrayList<Integer>();
		
		getAllDidgitsFromScore(currentScore, allDidgitsFromCurrentScore);
		getAllDidgitsFromScore(bestScore, allDidgitsFromBestScore);
		drawScore(allDidgitsFromCurrentScore, spacing, padding);
		if(GameManager.getInstance().getScore() > UserData.getInstance().getHighestScore()){
			drawBestScore(allDidgitsFromCurrentScore, spacing, padding);
		}else{
			drawBestScore(allDidgitsFromBestScore, spacing, padding);
		}
		
		if(allDidgitsFromCurrentScore.size() == 0){
			final Sprite loser = new Sprite(GameManager.lengthOfTile, screenHeight/8 + highscoreTR.getHeight() + scoreTR.getHeight() + padding * 2, numberTR[0], this.activity.getVertexBufferObjectManager());
			scene.attachChild(loser);
		}
		
		navigation();
		GameManager.getInstance().resetGame();
		return scene;
	}

	private void drawScore(List<Integer> allDidgits, int letterSpacing, int padding) {
		final Sprite scoreText = new Sprite((screenWidth - scoreTR.getWidth())/2, screenHeight/8 + highscoreTR.getHeight() + padding, scoreTR, this.activity.getVertexBufferObjectManager());
		scene.attachChild(scoreText);
		
		int iReverse = allDidgits.size();
		for(int i = 0; i < allDidgits.size(); i++){
			iReverse--;
			final Sprite number = new Sprite(GameManager.lengthOfTile + (i * letterSpacing), screenHeight/8 + highscoreTR.getHeight() + scoreTR.getHeight() + padding * 2, numberTR[allDidgits.get(iReverse)], this.activity.getVertexBufferObjectManager());
			scene.attachChild(number);
		}
	}
	
	private void drawBestScore(List<Integer> allDidgits, int letterSpacing, int padding) {
		final Sprite scoreText = new Sprite((screenWidth - bestScoreTR.getWidth())/2, screenHeight/8 + highscoreTR.getHeight() + scoreTR.getHeight() * 2 + padding * 3, bestScoreTR, this.activity.getVertexBufferObjectManager());
		scene.attachChild(scoreText);
		
		int iReverse = allDidgits.size();
		for(int i = 0; i < allDidgits.size(); i++){
			iReverse--;
			final Sprite number = new Sprite(GameManager.lengthOfTile + (i * letterSpacing), screenHeight/8 + highscoreTR.getHeight() + scoreTR.getHeight() * 3 + padding * 4, numberTR[allDidgits.get(iReverse)], this.activity.getVertexBufferObjectManager());
			scene.attachChild(number);
		}
	}
	
	private void getAllDidgitsFromScore(int score, List<Integer> allDidgits) {
		int didgit = 0;
		while(score > 0){
			didgit = score % 10;
			score /= 10;
			allDidgits.add(didgit);
		}
	}

	private void navigation() {
		// MENU ITEMS
		final IMenuItem buttonHome = new ScaleMenuItemDecorator(new SpriteMenuItem(HOME_BTN_ID, homeTR, this.activity.getVertexBufferObjectManager()), 1, 1);
		final IMenuItem buttonRetry = new ScaleMenuItemDecorator(new SpriteMenuItem(RETRY_BTN_ID, retryTR, this.activity.getVertexBufferObjectManager()), 1, 1);
		buttonHome.setPosition(screenWidth/2 - homeTR.getWidth()/2 - retryTR.getWidth()/2 - 64, screenHeight - homeTR.getHeight()*2);
		buttonRetry.setPosition(screenWidth/2 - retryTR.getWidth()/2 + homeTR.getWidth()/2 + 64, screenHeight - retryTR.getHeight()*2);
		
		mEngine.registerUpdateHandler(new TimerHandler(1, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				scene.addMenuItem(buttonHome);
				scene.addMenuItem(buttonRetry);
				
				scene.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
						
						switch(pMenuItem.getID()){
						case HOME_BTN_ID:
							
							sounds.playBlop();
							sceneManager.createMenuScene();
							sceneManager.setCurrentSence(AllScenes.MENU);
							break;
						case RETRY_BTN_ID:
							sounds.playCarStart();
							sceneManager.createGameScene();
							sceneManager.setCurrentSence(AllScenes.GAME);
							break;
						}
						
						return false;
					}
				});
			}
		}));
	}

	public Scene getScene() {
		return scene;
	}
}
